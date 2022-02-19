package com.ant_waters.covidstatistics.Utils

import java.io.File
import java.io.InputStream
import java.util.*

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

class SimpleTable2<TRowHdr, Tval>() {
    val NumRows
        get() = Rows.count()

    val NumColumns
        get() = Headers.count()

    var Headers = mutableListOf<String>()
    var Rows = mutableListOf<Pair<TRowHdr, List<Tval/*Value*/>>>()

    fun addHeaders(items: List<String>)
    {
        Headers.addAll(items)
    }
    fun addRow(rowHdr:TRowHdr, items: List<Tval>)
    {
        if (items.count() != Headers.count()-1) { throw Exception("Bad row in csv file") }
        Rows.add(Pair<TRowHdr, List<Tval>>(rowHdr, items))
    }
}


// -----------------------------------

fun daysDiff(dEnd: Date, dStart:Date):Long {
    val dayInMilliSecs:Long = 24*60*60*1000
    val diffInMillies: Long = Math.abs(dEnd.getTime() - dStart!!.getTime())
    val diffInDays = diffInMillies/dayInMilliSecs
    return diffInDays
}

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
