package com.ant_waters.covidstatistics.ui.data_table

//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ant_waters.covidstatistics.databinding.FragmentDataTableBinding


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Gravity

import android.widget.TextView;
import android.content.Context

import android.widget.TableLayout
import android.widget.TableRow

import android.widget.TableRow.LayoutParams;
import android.util.Log
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import com.ant_waters.covidstatistics.MainActivity
import com.ant_waters.covidstatistics.Utils.HorizontalScrollViewListener
import com.ant_waters.covidstatistics.Utils.ObservableHorizontalScrollView
import com.ant_waters.covidstatistics.Utils.SimpleTable2
import com.ant_waters.covidstatistics.enDataLoaded
import com.ant_waters.covidstatistics.model.DataManager
import java.text.SimpleDateFormat
import java.util.*

// The starting point for this code came from: https://stackoverflow.com/questions/7119231/android-layout-how-to-implement-a-fixed-freezed-header-and-column
// and other sources:

// See also: https://stackoverflow.com/questions/3948934/synchronise-scrollview-scroll-positions-android

class DataTableFragment : Fragment(), HorizontalScrollViewListener {

    private var _context: Context? = null

    private var horizontalScrollView1: ObservableHorizontalScrollView? = null
    private var horizontalScrollView2: ObservableHorizontalScrollView? = null
    private var interceptScroll: Boolean = true

    fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / resources.displayMetrics.density).toInt()
    }

    private lateinit var dataTableViewModel: DataTableViewModel
    private var _binding: FragmentDataTableBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _context = getActivity()?.getApplicationContext()

        dataTableViewModel =
            ViewModelProvider(this).get(DataTableViewModel::class.java)

        _binding = FragmentDataTableBinding.inflate(inflater, container, false)
        val root: View = binding.root

        horizontalScrollView1 = _binding!!.columnHeaderScroll
        horizontalScrollView2 = _binding!!.dataHorizontalScroll

        horizontalScrollView1!!.setScrollViewListener(this);
        horizontalScrollView2!!.setScrollViewListener(this);

        // Display the table
        if (MainActivity.DataInitialised.value==enDataLoaded.All) {
            val allCells = displayDataTable(inflater)

            val content: View = _binding!!.mainArea
            content.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    //Remove the observer so we don't get this callback for EVERY layout pass
                    content.viewTreeObserver.removeGlobalOnLayoutListener(this)

                    //Resize the columns to match the maximum width
                    setColumnWidths(allCells, fun (v: View, w: Int) {
                        val tv = v.findViewById<View>(com.ant_waters.covidstatistics.R.id.cell_text_view) as TextView
                        var lp  = LayoutParams(w, LayoutParams.WRAP_CONTENT)
                        v.layoutParams = lp

                        tv.setGravity(Gravity.CENTER)
                    })
                }
            })
        }

        return root
    }

    fun getTestData(numCols:Int, numRows:Int) : SimpleTable2<String, String>
    {
        val table = SimpleTable2<String, String>()
        val longHdrCol = 2
        val longDataCol = 1
        val numLongRows = 4
        val warning_row = 6


        val headers = mutableListOf<String>()
        for (c in 0..numCols-1)
        {
            val hdr: String = (if (c == longHdrCol) "Longer Col${c+1}" else "Col ${c+1}")
            headers.add(hdr);
        }
        table.addHeaders(headers.toList())

        val values = mutableListOf<String>()
        for (r in 0..numRows-1)
        {
            values.clear()
            var rowHdr = ""
            for (c in 0..numCols-1)
            {
                var datVal: String = (if ((r < numLongRows) && (c == longDataCol)) "Longer Val${r},${c+1}" else "Val${r},${c+1}")
                if  (r==warning_row) { datVal = "War${r},${c+1}" }

                if (c == 0){
                    rowHdr = datVal
                } else {
                    values.add(datVal);
                }
            }
            table.addRow(rowHdr, values.toList())
        }
        return table
    }

    fun displayDataTable(inflater: LayoutInflater): Array<Array<View?>>
    {
//        val testData = getTestData(20, 100)
//        return displayTable<String>(inflater, testData)

        val ranking = DataManager.CountryAggregates.SortedByProportionalCases

        val numCountriesToInclude = 10      // Top ten!
        val includeColumns = mutableListOf<String>()
        for (p in 1..numCountriesToInclude) {
            includeColumns.add(ranking[p-1].first.name)
        }

        return displayTable<Date, Int>(inflater, DataManager.CovidCasesTable,
            includeColumns, "End Date")
    }

    // includeColumns should not include the row header
    fun <TRowHdr, Tval>displayTable(inflater: LayoutInflater, dataTable: SimpleTable2<TRowHdr, Tval>,
                                    includeColumns:List<String>, topLeftText:String): Array<Array<View?>>
    {
        val maxDataRows = 100

        Log.i(MainActivity.LOG_TAG, "displayTable: Starting")

        val numRows = (if (dataTable.NumRows <= maxDataRows) { dataTable.NumRows+1} else {maxDataRows+1})
        var allCells = Array(numRows) {Array<View?>(includeColumns.size+1) {null} }

        val wrapWrapTableRowParams: TableRow.LayoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        // ------------- Top left cell (fixed completely)
        var row = TableRow(_context)
        val topLeftTL = _binding?.topLeftCell as TableLayout
        row.setLayoutParams(wrapWrapTableRowParams)
        row.setGravity(Gravity.CENTER)
        //val topLeftText = dataTable.Headers[0]
        allCells[0][0] = createHeaderCellFromTemplate(inflater, topLeftText)
        setHeaderBg(allCells[0][0] as View)
        row.addView(allCells[0][0])
        topLeftTL.addView(row)

        // ------------- Main header (fixed vertically)
        row = TableRow(_context)
        val header = _binding?.tableHeader as TableLayout
        row.setLayoutParams(wrapWrapTableRowParams)
        row.setGravity(Gravity.CENTER)

        var cIndex=0
        val columnMap = mutableMapOf<Int/*Index in display*/,Int/*Index in table*/>()

        for (sc in 0..includeColumns.size-1)
        {
            for (c in 1..dataTable.NumColumns-1) {
                val colHdrText = dataTable.Headers[c]
                if (includeColumns[sc] == colHdrText) {
                    cIndex = sc+1
                    columnMap.put(cIndex,c)
                    allCells[0][cIndex] = createHeaderCellFromTemplate(inflater, colHdrText)
                    setHeaderBg(allCells[0][cIndex] as View)
                    row.addView(allCells[0][cIndex])
                }
            }
        }

        header.addView(row)

        // ------------- Row header (fixed horizontally)
        val fixedColumn = _binding?.fixedColumn as TableLayout

        // ------------- Rest of the table (within a scroll view)
        val scrollablePart = _binding?.scrollablePart as TableLayout

        // ------------- Create the rows
        for (r in 0..dataTable.NumRows-1) {
            if  (r>maxDataRows-1){ break}
            Log.i(MainActivity.LOG_TAG, "displayTable: Row ${r}")

            allCells[r+1][0] = createRowHeaderCellFromTemplate<TRowHdr>(inflater, dataTable.Rows[r].first)
            setHeaderBg(allCells[r+1][0] as View)
            row = TableRow(_context)
            row.addView(allCells[r+1][0])
            fixedColumn.addView(row)

            // ----------- Create Row Data
            row = TableRow(_context)
            for (sc in 0..columnMap.size-1) {
                cIndex = sc+1
                val c = columnMap[cIndex]
                allCells[r+1][cIndex] = createDataCellFromTemplate<Tval>(inflater, dataTable.Rows[r].second[c!!-1])
                setContentBg(allCells[r+1][cIndex] as View)
                row.addView(allCells[r+1][cIndex])
            }

            row.setLayoutParams(wrapWrapTableRowParams)
            row.setGravity(Gravity.CENTER)
            scrollablePart.addView(row)
        }

        Log.i(MainActivity.LOG_TAG, "displayTable: Finished")
        return  allCells
    }

    fun setColumnWidths(allCells: Array<Array<View?>>, setItemWidth: (v: View, w: Int) -> Unit)
    {
        val numColumns: Int = allCells[0].size
        val colWidths = Array<Int>(numColumns, {0})
        for (r in 0..allCells.size-1)
        {
            for (c in 0..numColumns-1)
            {
                val vw : View = allCells[r][c]!!
                if (vw.width > colWidths[c]) { colWidths[c] = vw.width}
            }
        }
        for (r in 0..allCells.size-1)
        {
            for (c in 0..numColumns-1)
            {
                val vw = allCells[r][c]!! as LinearLayout
                setItemWidth(vw, colWidths[c])
            }
        }
    }

    fun createHeaderCellFromTemplate(inflater: LayoutInflater, text: String?): View {
        val cellView: View = inflater.inflate(com.ant_waters.covidstatistics.R.layout.header_cell, null)
        val tv = cellView.findViewById<View>(com.ant_waters.covidstatistics.R.id.cell_text_view) as TextView
        tv.text = text

        return cellView
    }
    fun <TRowHdr>createRowHeaderCellFromTemplate(inflater: LayoutInflater, rowHdr: TRowHdr): View {
        val cellView: View = inflater.inflate(com.ant_waters.covidstatistics.R.layout.header_cell, null)
        val tv = cellView.findViewById<View>(com.ant_waters.covidstatistics.R.id.cell_text_view) as TextView

        if  (rowHdr is Date)
        {
            val dt = rowHdr as Date
            var formatter = SimpleDateFormat("dd/MM/yy")
            tv.text = formatter.format(dt)
        }
        else {
            tv.text = rowHdr.toString()
        }

        return cellView
    }

    fun <Tval>createDataCellFromTemplate(inflater: LayoutInflater, theVal: Tval): View {
        var templateId = com.ant_waters.covidstatistics.R.layout.data_cell
        val text = theVal.toString()
        // TODO: NOW!!
        if (text?.startsWith("War")!!) { templateId = com.ant_waters.covidstatistics.R.layout.warning_data_cell }
        val cellView: View = inflater.inflate(templateId, null)

        val tv = cellView.findViewById<View>(com.ant_waters.covidstatistics.R.id.cell_text_view) as TextView
        tv.text = text

        return cellView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setHeaderBg(view: View) {
        view.setBackgroundResource(com.ant_waters.covidstatistics.R.drawable.table_header_cell_bg)
    }

    private fun setContentBg(view: View) {
        view.setBackgroundResource(com.ant_waters.covidstatistics.R.drawable.table_content_cell_bg)
    }

    override fun onScrollChanged(
        scrollView: ObservableHorizontalScrollView?,
        x: Int, y: Int, oldx: Int, oldy: Int
    ) {
        if (interceptScroll)
        {
            interceptScroll = false
            if(scrollView == horizontalScrollView1) {
                horizontalScrollView2!!.scrollTo(x, y);
//                horizontalScrollView2!!.onOverScrolled(x,y,true,true);
            } else if(scrollView == horizontalScrollView2) {
                horizontalScrollView1!!.scrollTo(x, y)
//                horizontalScrollView1!!.onOverScrolled(x,y,true,true)
            }
            interceptScroll = true
        }
    }
}