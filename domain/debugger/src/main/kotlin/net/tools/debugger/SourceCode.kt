package net.tools.debugger

data class Id(val value: String)

data class Range(val start: Int, val end: Int)

data class BreakPoint(val sourceCode: SourceCode, val line: Int)

abstract class SourceCode(val id: Id, private val code: String) {

    private val range by lazy { computeRange() }

    private val lines: List<String> by lazy { code.split("\n") }

    abstract fun computeRange(): Range

    private fun getLine(line: Int): String {
        return lines[line - range.start]
    }

    private fun isBlank(line: Int): Boolean {
        return getLine(line).trim().isBlank()
    }

    private fun isValidForBreakPoint(line: Int): Boolean {
        return line in range.start..range.end && !isBlank(line)
    }

    fun createBreakPoint(line: Int): BreakPoint? {
        return if (isValidForBreakPoint(line)) BreakPoint(this, line) else null
    }
}
