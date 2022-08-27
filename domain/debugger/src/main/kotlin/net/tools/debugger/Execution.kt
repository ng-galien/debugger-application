package net.tools.debugger

import java.util.Collections
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

const val QUEUE_SIZE = 100

interface Command {
    fun execute(): Frame?
}

interface StepIntoCommand : Command

interface StepOverCommand : Command

interface ContinueCommand : Command

interface AbortCommand : Command

interface Monitor {
    fun frameReached(frame: Frame)
    fun sourceReached(sourceCode: SourceCode)
    fun onStarted()
    fun onStopped()
}

interface Config {
    fun singleRun(): Boolean
}

interface Owner {
    fun getId(): Id
}

interface Plugin<T: Owner> {
    fun init(): T
    fun running(owner: T): Boolean
    fun fetchSource(owner: T, id: Id): SourceCode
    fun start(owner: T): Frame?
    fun stepIntoCommand(owner: T): StepIntoCommand
    fun stepOverCommand(owner: T): StepOverCommand
    fun continueCommand(owner: T): ContinueCommand
    fun abort(owner: T): AbortCommand
    fun stop(owner: T)
}

interface Repository {
    fun storeCode(code: SourceCode)
    fun getCode(id: Id): SourceCode?
}

interface Controller {
    fun start()
    fun stop()
    fun stepInto()
    fun stepOver()
    fun continueExecution()
    fun addMonitor(monitor: Monitor)
}

abstract class Runner<T: Owner, K: Plugin<T>>
    (private val owner: T, private val facade: K): Runnable {

    private val commandQueue: BlockingQueue<Command> = LinkedBlockingQueue(QUEUE_SIZE)
    private var running = false

    override fun run() {
        facade.start(owner)?.let { frame ->
            onFrame(frame)
            running = true
            while (running) {
                runCatching {
                    val command = commandQueue.take()
                    command.execute()?.let { onFrame(it) } ?: { running = false }
                }.onFailure {
                    running = false
                }
            }
            facade.stop(owner)
        }
    }

    fun offerCommand(command: Command) {
        commandQueue.offer(command)
    }

    abstract fun onFrame(frame: Frame)
}

class ControllerImpl<T: Owner, K: Plugin<T>>(
    private val config: Config,
    private val plugin: K,
    private val repository: Repository): Controller {

    private val owner: T by lazy { plugin.init() }
    private val stack = Stack(owner.getId())
    private val monitors = Collections.synchronizedCollection(mutableListOf<Monitor>())

    private val runner = object : Runner<T, K>(owner, plugin) {
        override fun onFrame(frame: Frame) {
            stack.push(frame)
            repository.getCode(frame.sourceId())?: run {
                val code = plugin.fetchSource(owner, frame.sourceId())
                repository.storeCode(code)
                monitors().forEach { it.sourceReached(code) }
            }
            monitors().forEach{ it.frameReached(frame) }
        }
    }

    private val processThread = Thread(runner)

    private val monitorThread = Thread {
        while (processThread.isAlive) {
            if (!plugin.running(owner) && config.singleRun()) {
                processThread.interrupt()
            }
        }
    }

    fun monitors(): List<Monitor> = monitors.filterNotNull()

    override fun start() {
        processThread.start()
        monitorThread.start()
        monitors().forEach{ it.onStarted() }
    }

    override fun stop() {
        runner.offerCommand(plugin.abort(owner))
        processThread.join()
        monitors().forEach{ it.onStopped() }
    }

    override fun stepInto() {
        runner.offerCommand(plugin.stepIntoCommand(owner))
    }

    override fun stepOver() {
        runner.offerCommand(plugin.stepOverCommand(owner))
    }

    override fun continueExecution() {
        runner.offerCommand(plugin.continueCommand(owner))
    }

    override fun addMonitor(monitor: Monitor) {
        monitors.add(monitor)
    }
}