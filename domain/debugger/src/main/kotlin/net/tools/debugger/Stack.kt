package net.tools.debugger

data class VariableType(val type: String)

interface Variable {
    fun name(): String
    fun value(): String
    fun type(): VariableType
    fun isFinal(): Boolean
}

interface PrimitiveVariable : Variable {
    override fun isFinal(): Boolean = true
}

interface ArrayVariable : Variable {
    fun arrayType(): VariableType
    fun length(): Int
    fun get(index: Int): Variable
    fun elements(): List<Variable>
    override fun isFinal(): Boolean = false
}

interface CompositeVariable : Variable {
    override fun isFinal(): Boolean = false
    fun members(): List<Variable>
}

interface Frame {
    fun sourceId(): Id
    fun line(): Int
    fun args(): List<Variable>
    fun locals(): List<Variable>
}

class Stack(val id: Id) {

    private val frames: MutableList<Frame> = mutableListOf()
    val currentFrame: Frame? by lazy { frames.lastOrNull() }

    fun push(frame: Frame) {
        frames.add(frame)
    }

}