package com.ant_waters.covidstatistics.ui.data_table

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TableRow.LayoutParams
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ant_waters.covidstatistics.MainViewModel
import com.ant_waters.covidstatistics.Utils.SimpleTable2
import com.ant_waters.covidstatistics.databinding.FragmentDataTableBinding
import com.ant_waters.covidstatistics.enDataLoaded
import com.ant_waters.covidstatistics.model.DataManager
import com.ant_waters.covidstatistics.ui.HorizontalScrollViewListener
import com.ant_waters.covidstatistics.ui.ObservableHorizontalScrollView
import com.ant_waters.covidstatistics.ui.country_pop_up.CountryPopupFragment
import com.ant_waters.covidstatistics.ui.display__options.DisplayOptions
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Code for the fragment that displays a data table
 *
 * The starting point for this code came from:
 * https://stackoverflow.com/questions/7119231/android-layout-how-to-implement-a-fixed-freezed-header-and-column
 * and other sources.
 * See also: https://stackoverflow.com/questions/3948934/synchronise-scrollview-scroll-positions-android
 */
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

    // This property is only valid between onCreateView and onDestroyView.
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

        MainViewModel.DisplayOptionsChanged.observe(viewLifecycleOwner, Observer {
            Log.i(MainViewModel.LOG_TAG, "Observer: DataTableFragment, Globals.DisplayOptionsChanged: Started")
            refreshTheDataTable(inflater)   // Note that this get's called on first load also
        })

        return root
    }

    fun refreshTheDataTable(inflater: LayoutInflater)
    {
        binding.progressBar1.setVisibility(View.VISIBLE)    // This doesn't work because the view is not visible till we finish this routine

        clearTheDataTable()
        displayTheDataTable(inflater)

        binding.progressBar1.setVisibility(View.GONE)

        // In the code below I was trying to just refresh the fragment, but it doesn't work
        // because observing LIVE data seems to automatically recreate the fragment, so at this
        // point findFragmentById returns null for both this fragment and it's navigation parent
        //
        //            val currentFragment = requireActivity()?.getSupportFragmentManager()?.findFragmentById(R.id.nav_data_table)
        //             Or:
        //            val navHostFragment = requireActivity()?.getSupportFragmentManager()?.findFragmentById(R.id.mobile_navigation)
        //            val currentFragment = navHostFragment?.childFragmentManager?.findFragmentById(R.id.nav_data_table)
        //
        //            if (currentFragment != null) {
        //                Log.i(MainViewModel.LOG_TAG, "Detaching and reattaching fragment")
        //                val fragTransaction = (requireActivity())?.getSupportFragmentManager()?.beginTransaction()
        //                fragTransaction?.detach(currentFragment)?.attach(currentFragment)?.commit()
        //            }
    }

    fun clearTheDataTable()
    {
        val topLeftTL = _binding?.topLeftCell as TableLayout
        val header = _binding?.tableHeader as TableLayout
        val fixedColumn = _binding?.fixedColumn as TableLayout
        val scrollablePart = _binding?.scrollablePart as TableLayout

        topLeftTL.removeAllViewsInLayout()
        header.removeAllViewsInLayout()
        fixedColumn.removeAllViewsInLayout()
        scrollablePart.removeAllViewsInLayout()
    }

    fun displayTheDataTable(inflater: LayoutInflater)
    {
        if (MainViewModel.DataInitialised.value==enDataLoaded.All) {
            // Display the table by creating Views for cells, headers etc.
            val allCells = displayDataTable(inflater)

            // Add a callback to set the column widths at the end (when onGlobalLayout is called)
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
    }


    fun displayDataTable(inflater: LayoutInflater): Array<Array<View?>>
    {
        //        // ------------ Use test data
        //        val testData = getTestData(20, 100)
        //        return displayTable<String>(inflater, testData)

        // ------------ Decide which table to display
        var dataTable_Int : SimpleTable2<Date, Int>? = null
        var dataTable_Double : SimpleTable2<Date, Double>? = null

        var ranking = DataManager.CountryAggregates.SortedByTotalCases
        var label = "${DataManager.AggregationPeriodInDays} day "
        when (MainViewModel.DisplayOptions.tableValueType) {

            DisplayOptions.enTableValueType.TotalCases -> {
                dataTable_Int = DataManager.CovidCasesTable
                ranking = DataManager.CountryAggregates.SortedByTotalCases
                label += "cases" }

            DisplayOptions.enTableValueType.TotalDeaths -> {
                dataTable_Int = DataManager.CovidDeathsTable
                ranking = DataManager.CountryAggregates.SortedByTotalDeaths
                label += "deaths" }

            DisplayOptions.enTableValueType.ProportionalCases -> {
                dataTable_Double = DataManager.ProportionalCovidCasesTable
                ranking = DataManager.CountryAggregates.SortedByProportionalCases
                label += "cases per ${DataManager.PopulationScalerLabel}" }

            DisplayOptions.enTableValueType.ProportionalDeaths -> {
                dataTable_Double = DataManager.ProportionalCovidDeathsTable
                ranking = DataManager.CountryAggregates.SortedByProportionalDeaths
                label += "deaths per ${DataManager.PopulationScalerLabel}" }
        }

        // ------------ Decide which countries to display
        val numCountriesToInclude = 10      // Top ten!
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.title = label
        actionBar?.subtitle = "worst ${numCountriesToInclude} countries"

        // Note that the way this is used assumes that the table headers match the country names.  This is a weakness.
        val includeColumns = mutableListOf<String>()
        for (p in 1..numCountriesToInclude) {
            includeColumns.add(ranking[p-1].first.name)
        }

        val startDate = MainViewModel.DisplayOptions.startDate
        val endDate = MainViewModel.DisplayOptions.endDate
        val rowTestFun: (Date)->Boolean = { d -> ((d >= startDate) && (d <= endDate))}

        // ------------ Display
        if (dataTable_Int != null) {
            return displayTable<Date, Int>(inflater, dataTable_Int, includeColumns, rowTestFun,"End Date")
        } else {
            return displayTable<Date, Double>(inflater, dataTable_Double!!, includeColumns, rowTestFun, "End Date")
        }
    }

    // includeColumns should not include the row header
    fun <TRowHdr, Tval>displayTable(inflater: LayoutInflater, dataTable: SimpleTable2<TRowHdr, Tval>,
                                    includeColumns:List<String>, rowTestFun: (TRowHdr)->Boolean,
                                    topLeftText:String): Array<Array<View?>>
    {
        val maxDataRows = MainViewModel.DisplayOptions.tableMaxNumberOfRows

        Log.i(MainViewModel.LOG_TAG, "displayTable: Starting")

        val numRows = (if (dataTable.NumRows <= maxDataRows) { dataTable.NumRows+1} else {maxDataRows+1})
        var allCells = Array(numRows) {Array<View?>(includeColumns.size+1) {null} }

        val wrapWrapTableRowParams: TableRow.LayoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        // ------------- Top left cell (fixed completely)
        var row = TableRow(_context)
        val topLeftTL = _binding?.topLeftCell as TableLayout
        row.setLayoutParams(wrapWrapTableRowParams)
        row.setGravity(Gravity.CENTER)
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
        var rCount = 0
        for (r in 0..dataTable.NumRows-1) {
            rCount++
            if  (rCount>maxDataRows){ break}
            if (!rowTestFun(dataTable.Rows[r].first)) {
                continue
            }

            Log.i(MainViewModel.LOG_TAG, "displayTable: Row ${r}")

            allCells[r+1][0] = createRowHeaderCellFromTemplate<TRowHdr>(inflater, dataTable.Rows[r].first)
            setHeaderBg(allCells[r+1][0] as View)
            row = TableRow(_context)
            row.addView(allCells[r+1][0])
            fixedColumn.addView(row)

            // ----------- Find the worst column
            var worstCol = -1
                                // This is the real code to do it, but the result is not so interesting...
                                //            var worstVal = -1
                                //            for (sc in 0..columnMap.size-1) {
                                //                cIndex = sc+1
                                //                val c = columnMap[cIndex]
                                //                val theVal = dataTable.Rows[r].second[c!!-1]
                                //                if (theVal > worstVal) {
                                //                    worstVal = theVal
                                //                    worstCol = sc
                                //                }
                                //            }
            // Choose the worst column randomly, to make the display more interesting
            worstCol = (0..4).random()

            // ----------- Create Row Data
            row = TableRow(_context)
            for (sc in 0..columnMap.size-1) {
                cIndex = sc+1
                val c = columnMap[cIndex]
                allCells[r+1][cIndex] = createDataCellFromTemplate<Tval>(inflater,
                                            dataTable.Rows[r].second[c!!-1], (sc == worstCol), dataTable.Headers[c])
                setContentBg(allCells[r+1][cIndex] as View)
                row.addView(allCells[r+1][cIndex])
            }

            row.setLayoutParams(wrapWrapTableRowParams)
            row.setGravity(Gravity.CENTER)
            scrollablePart.addView(row)
        }

        Log.i(MainViewModel.LOG_TAG, "displayTable: Finished")
        return  allCells
    }

    fun setColumnWidths(allCells: Array<Array<View?>>, setItemWidth: (v: View, w: Int) -> Unit)
    {
        val numColumns: Int = allCells[0].size
        val colWidths = Array<Int>(numColumns, {0})
        for (r in 0..allCells.size-1)
        {
            if ((allCells[r] == null) || (allCells[r][0] == null)) { continue }    // Row was skipped
            for (c in 0..numColumns-1)
            {
                val vw : View = allCells[r][c]!!
                if (vw.width > colWidths[c]) { colWidths[c] = vw.width}
            }
        }
        for (r in 0..allCells.size-1)
        {
            if ((allCells[r] == null) || (allCells[r][0] == null)) { continue }    // Row was skipped
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

    fun <Tval>createDataCellFromTemplate(inflater: LayoutInflater, theVal: Tval,
                                         showWarning: Boolean, countryName: String
    ): View {
        var templateId = com.ant_waters.covidstatistics.R.layout.data_cell

        var text = theVal.toString()
        if (theVal is Double) {
            val df1 = DecimalFormat("#")
            val df2 = DecimalFormat("#.0")
            text = getProportionalDisplayText(text.toDouble(), df1, df2)
        }

        if (showWarning) { templateId = com.ant_waters.covidstatistics.R.layout.warning_data_cell }
        val cellView: View = inflater.inflate(templateId, null)

        val tv = cellView.findViewById<View>(com.ant_waters.covidstatistics.R.id.cell_text_view) as TextView
        tv.text = text


        if (DataManager.CountriesByName.containsKey(countryName)) {
            cellView.setOnClickListener(View.OnClickListener {
                val cpf = CountryPopupFragment()

                // Supply country as an argument.
                val args = Bundle()

                args.putString("geoId", DataManager.CountriesByName[countryName]!!.geoId)
                cpf.setArguments(args)

                cpf.show(requireActivity()?.getSupportFragmentManager(), "countrypopup_from_datatable")
            })
        }

        return cellView
    }
    fun getProportionalDisplayText(stat : Double, df1: DecimalFormat, df2: DecimalFormat) : String
    {
        var df: DecimalFormat = df1
        if (stat < 1)
        {
            df = df2
        }
        return "${df.format((stat))}"
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

    // ------------------ No longer used
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
}