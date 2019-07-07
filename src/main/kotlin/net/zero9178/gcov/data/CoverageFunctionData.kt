package net.zero9178.gcov.data

import java.util.*

class CoverageFunctionData(var startLine: Int = -1, internal var endLine: Int = -1, var functionName: String = "<unknown function>") {

    private val myLines = TreeMap<Int, CoverageLineData>()

    var fileData: CoverageFileData? = null
        internal set

    constructor(fileData: CoverageFileData) : this() {
        this.fileData = fileData
    }

    val coverage: Int
        get() = myLines.values.count{it.executionCount > 0}

    val lines: Map<Int, CoverageLineData>
        get() = myLines

    fun emplaceLine(lineNumber: Int, executionCount: Int) {
        this.emplaceLine(CoverageLineData(lineNumber, executionCount))
    }

    fun emplaceLine(lineData: CoverageLineData) {
        val line = myLines[lineData.lineNumber]
        if (line == null) {
            myLines[lineData.lineNumber] = lineData
            fileData?.lineData?.put(lineData.lineNumber, lineData)
        } else {
            line.executionCount += lineData.executionCount
        }
    }

    fun lineIsInFunction(line: Int): Boolean {
        return line in startLine..endLine
    }

    override fun toString(): String {
        return functionName
    }
}