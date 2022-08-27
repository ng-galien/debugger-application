package net.tools.debugger.test

import net.tools.debugger.*

class FakeOwner : Owner {
    override fun getId(): Id = Id("id")
}

class Fake: Plugin<FakeOwner> {
    override fun init(): FakeOwner {
        TODO("Not yet implemented")
    }

    override fun stop(owner: FakeOwner) {
        TODO("Not yet implemented")
    }

    override fun abort(owner: FakeOwner): AbortCommand {
        TODO("Not yet implemented")
    }

    override fun continueCommand(owner: FakeOwner): ContinueCommand {
        TODO("Not yet implemented")
    }

    override fun stepOverCommand(owner: FakeOwner): StepOverCommand {
        TODO("Not yet implemented")
    }

    override fun stepIntoCommand(owner: FakeOwner): StepIntoCommand {
        TODO("Not yet implemented")
    }

    override fun start(owner: FakeOwner): Frame? {
        TODO("Not yet implemented")
    }

    override fun fetchSource(owner: FakeOwner, id: Id): SourceCode {
        TODO("Not yet implemented")
    }

    override fun running(owner: FakeOwner): Boolean {
        TODO("Not yet implemented")
    }

}