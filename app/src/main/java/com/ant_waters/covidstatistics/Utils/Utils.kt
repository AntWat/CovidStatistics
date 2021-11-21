package com.ant_waters.covidstatistics.Utils

import java.io.File
import java.io.InputStream

class SimpleTable() {
    val NumRows
        get() = Rows.count()

    val NumColumns
        get() = Headers.count()

    var Headers = mutableListOf<String>()
    var Rows = mutableListOf<List<String/*Value*/>>()

    fun addHeaders(items: List<String>)
    {
        Headers.addAll(items)
    }
    fun addRow(items: List<String>)
    {
        if (items.count() != Headers.count()) { throw Exception("Bad row in csv file") }
        Rows.add(items)
    }
}

/*
class SimpleTable<T>() {
    public var NumRows: Int = 0
    public var NumColumns: Int = 0
    public var Headers = mutableListOf<String>()
    public var rows = mutableListOf<Map</*Header*/String, T/*Value*/>>()

    fun readFirstRowAndHeaders(firstRowAsMap: Map</*Header*/String, T/*Value*/>): Unit {
        if ((NumColumns != 0) || (Headers.count() != 0)) {
            throw Exception("Bad call to readFirstRowAndHeaders")}

        NumColumns = firstRowAsMap.size
        for (kvp in firstRowAsMap) {Headers.add(kvp.key)}
        addRowFromMap(firstRowAsMap)
    }

    fun addRowFromMap(rowAsMap: Map</*Header*/String, T/*Value*/>) : Unit
    {
        rows.add(rowAsMap)
    }
}
*/


// -----------------------------------

//fun readCsv(fileName: String) : SimpleTable
//{
//    val csv = File(fileName)
//    var table = readCsv(csv)
//    return  table
//}

fun readCsv(inputStream: InputStream) : SimpleTable
{
    // TODO: Protect newlines and commas that are within quotes

    //val inputStream: InputStream = csv.inputStream()
    //val lineList = mutableListOf<String>()

    val table = SimpleTable()
    var firstRow = true
    val BOM = "\uFEFF"  // See: https://stackoverflow.com/questions/44061143/read-csv-line-by-line-in-kotlin

    inputStream.bufferedReader().forEachLine {
        val line: String = if (firstRow) it.replace(BOM, "") else it
        val items: List<String> = line.split(",")

        if (firstRow) {
            table.addHeaders(items)
            firstRow = false
        } else {
            table.addRow(items)
        }
    }

    return table

//    var firstRow = true
//    var table = SimpleTable<String>()
//    val processor: (Map</*Header*/String, String/*Value*/>) -> Unit = fun(row: Map<String, String>) : Unit
//    {
//        if (firstRow)
//        {
//            table.readFirstRowAndHeaders(row)
//            firstRow = false
//        }
//        else {
//            table.addRowFromMap(row)
//        }
//    }
//
//    return table
}

// -----------------------------------

// This code came from: https://stackoverflow.com/questions/44061143/read-csv-line-by-line-in-kotlin
//fun processLineByLine(csv: File, processor: (Map<String, String>) -> Unit)  {
//    val BOM = "\uFEFF"
//    val header = csv.useLines { it.firstOrNull()?.replace(BOM, "")?.split(",") }
//        ?: throw Exception("This file does not contain a valid header")
//
//    csv.useLines { linesSequence ->
//        linesSequence
//            .drop(1)
//            .map { it.split(",") }
//            .map { header.zip(it).toMap() }
//            .forEach(processor)
//    }
//}
