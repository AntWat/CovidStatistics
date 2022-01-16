package com.ant_waters.covidstatistics.ui.display__options

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.ant_waters.covidstatistics.MainViewModel
import com.ant_waters.covidstatistics.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat

// TODO:
// *) Still not showing correctly, hacky use of margins required
// *) Cancel button is 2 lines on my phone
// *) setDate

class DisplayOptionsFragment() : DialogFragment()  {

    private lateinit var _displayOptions: DisplayOptions

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _displayOptions = MainViewModel.DisplayOptionsBeingEdited
        return buildDisplayOptionsEditor(activity)
    }

    lateinit var myView: View
    lateinit var btn__start_date:Button
    lateinit var btn__end_date: Button
    lateinit var rdo__proportional: RadioButton
    lateinit var rdo__Totals: RadioButton
    lateinit var spinner__sortby: Spinner
    lateinit var switch__reverse_sort: Switch
    lateinit var spinner__table_value_type: Spinner
    lateinit var edittext__max_table_rows: EditText
    lateinit var btn__reset_all_options: Button
    lateinit var btn__ok: Button
    lateinit var btn__cancel: Button

    val map__sortby = mapOf<DisplayOptions.enSortBy, String>(
        DisplayOptions.enSortBy.CountryName to "Country Name",
        DisplayOptions.enSortBy.ProportionalCases to "Proportional Cases",
        DisplayOptions.enSortBy.ProportionalDeaths to "Proportional Deaths",
        DisplayOptions.enSortBy.TotalCases to "Total Cases",
        DisplayOptions.enSortBy.TotalDeaths to "Total Deaths",
           )

    val map__table_value_type = mapOf<DisplayOptions.enTableValueType, String>(
        DisplayOptions.enTableValueType.ProportionalCases to "Proportional Cases",
        DisplayOptions.enTableValueType.ProportionalDeaths to "Proportional Deaths",
        DisplayOptions.enTableValueType.TotalCases to "Total Cases",
        DisplayOptions.enTableValueType.TotalDeaths to "Total Deaths",
    )

    fun buildDisplayOptionsEditor(activity: FragmentActivity?) : Dialog
    {
        try {
            Log.i(MainViewModel.LOG_TAG, "buildDisplayOptionsEditor: Started")
            if (activity == null) {throw IllegalStateException("Activity cannot be null")}

            val builder = MaterialAlertDialogBuilder(activity)         // or use MaterialAlertDialogBuilder
            val inflater = requireActivity().layoutInflater;
            myView = inflater.inflate(R.layout.fragment_display_options, null)

            // -------------- Add the items to the sortby list
            spinner__sortby = myView.findViewById<Spinner>(R.id.spinner__sortby)
            val items = map__sortby.values.toTypedArray()
            val adapter = ArrayAdapter<String>(
                this.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
            spinner__sortby.setAdapter(adapter)

            // -------------- Add the items to the table_value_type list
            spinner__table_value_type = myView.findViewById<Spinner>(R.id.spinner__table_value_type)
            val items2 = map__table_value_type.values.toTypedArray()
            val adapter2 = ArrayAdapter<String>(
                this.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items2
            )
            spinner__table_value_type.setAdapter(adapter2)

            // -------------- Initialise the UI
            getWidgets()
            showDisplayOptions()

            btn__start_date.setOnClickListener { setDate(btn__start_date, "Start date") }
            btn__end_date.setOnClickListener { setDate(btn__end_date, "End date") }
            btn__ok.setOnClickListener { btnOk_Click() }
            btn__cancel.setOnClickListener { btnCancel_Click() }
            btn__reset_all_options.setOnClickListener { btnResetAll_Click() }

            // -------------- Build the dialog
            builder.setView(myView)
                // Add action buttons: // .setPositiveButton, .setNeutralButton, .setNegativeButton
                // Note: I am not using these because they always close the dialog when the button is pressed,
                // so there is no way to validate the choices first.  I am using custom buttons instead.

                //builder.setCanceledOnTouchOutside(false);     // This alos doesn't seem to work as expected!

            return builder.create()
        }
        catch (ex: Exception)
        {
            //Log.e(MainViewModel.LOG_TAG, ex.message?:"")
            throw(ex)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.getWindow()?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    fun getWidgets()
    {
        btn__start_date = myView.findViewById<View>(com.ant_waters.covidstatistics.R.id.btn__start_date) as Button
        btn__end_date = myView.findViewById<View>(com.ant_waters.covidstatistics.R.id.btn__end_date) as Button
        rdo__proportional = myView.findViewById<View>(com.ant_waters.covidstatistics.R.id.rdo__proportional) as RadioButton
        rdo__Totals = myView.findViewById<View>(com.ant_waters.covidstatistics.R.id.rdo__Totals) as RadioButton
        switch__reverse_sort = myView.findViewById<View>(com.ant_waters.covidstatistics.R.id.switch__reverse_sort) as Switch
        edittext__max_table_rows = myView.findViewById<View>(com.ant_waters.covidstatistics.R.id.edittext__max_table_rows) as EditText
        btn__reset_all_options = myView.findViewById<View>(com.ant_waters.covidstatistics.R.id.btn__reset_all_options) as Button
        btn__ok = myView.findViewById<View>(com.ant_waters.covidstatistics.R.id.btn__ok) as Button
        btn__cancel = myView.findViewById<View>(com.ant_waters.covidstatistics.R.id.btn__cancel) as Button
    }

    val datePattern = "dd/MMM/yyyy"

    fun showDisplayOptions()
    {
        btn__start_date.text = (SimpleDateFormat(datePattern).format(_displayOptions.startDate))
        btn__end_date.text = (SimpleDateFormat(datePattern).format(_displayOptions.endDate))

        if (_displayOptions.listStatisticsFormat == DisplayOptions.enStatisticsFormat.Proportional) {
            rdo__proportional.isChecked = true
        } else {
            rdo__Totals.isChecked = true
        }

        val iSortBy = map__sortby.keys.indexOfFirst { it==_displayOptions.listSortBy }
        spinner__sortby.setSelection(iSortBy)

        switch__reverse_sort.isChecked = _displayOptions.listReverseSort

        val iTable_value_type = map__table_value_type.keys.indexOfFirst { it==_displayOptions.tableValueType }
        spinner__table_value_type.setSelection(iTable_value_type)

        edittext__max_table_rows.setText(_displayOptions.tableMaxNumberOfRows.toString())
    }

    // Returns an error message if validation fails
    fun readDisplayOptions(): String
    {
        _displayOptions.startDate = SimpleDateFormat(datePattern).parse(btn__start_date.text!!.toString())
        _displayOptions.endDate = SimpleDateFormat(datePattern).parse(btn__end_date.text!!.toString())

        if (rdo__proportional.isChecked) {
            _displayOptions.listStatisticsFormat = DisplayOptions.enStatisticsFormat.Proportional
        } else {
            _displayOptions.listStatisticsFormat = DisplayOptions.enStatisticsFormat.Totals
        }

        val iSortBy = spinner__sortby.getSelectedItemPosition()
        _displayOptions.listSortBy = map__sortby.keys.elementAt(iSortBy)

        _displayOptions.listReverseSort = switch__reverse_sort.isChecked

        val iTable_value_type = spinner__table_value_type.getSelectedItemPosition()
        _displayOptions.tableValueType = map__table_value_type.keys.elementAt(iTable_value_type)

        val iTableMaxNumberOfRows = edittext__max_table_rows.getText().toString().toInt()
        if (iTableMaxNumberOfRows > 200) { return "Too many table rows!"}  // An example of validation

        _displayOptions.tableMaxNumberOfRows = iTableMaxNumberOfRows

        return  "" // Success
    }

    fun setDate(btn: Button, title: String)
    {
        // TODO Next:
    }
    fun btnOk_Click()
    {
        val errMsg = readDisplayOptions()
        if (errMsg.length > 0) {
            Toast.makeText(getActivity()?.getApplicationContext(),
                "Error! ${errMsg}",Toast.LENGTH_SHORT).show();      // TODO: Red text
        } else {
            MainViewModel.UpdateDisplayOptionsChanged()
            getDialog()?.dismiss()
        }
    }
    fun btnCancel_Click()
    {
        getDialog()?.dismiss()
    }
    fun btnResetAll_Click()
    {
        _displayOptions.ResetAll()
        showDisplayOptions()
    }

}