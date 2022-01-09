package com.ant_waters.covidstatistics.ui.display__options

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.ant_waters.covidstatistics.MainViewModel
import com.ant_waters.covidstatistics.R
import java.text.SimpleDateFormat

class DisplayOptionsFragment(val displayOptions: DisplayOptions) : DialogFragment()  {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return buildDisplayOptionsEditor(activity)
    }

    fun buildDisplayOptionsEditor(activity: FragmentActivity?) : Dialog
    {
        try {
            Log.i(MainViewModel.LOG_TAG, "buildDisplayOptionsEditor: Started")
            if (activity == null) {throw IllegalStateException("Activity cannot be null")}

            val builder = AlertDialog.Builder(activity)         // or use MaterialAlertDialogBuilder
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val vw = inflater.inflate(R.layout.fragment_display_options, null)

            // -------------- Add the items to the sortby list
            val spinner__sortby = vw.findViewById<Spinner>(R.id.spinner__sortby)
            val items = arrayOf("Country Name", "Proportional Deaths", "Proprotional Cases", "Total Deaths", "Total Cases")
            val adapter = ArrayAdapter<String>(
                this.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
            spinner__sortby.setAdapter(adapter)

            // -------------- Add the items to the table_value_type list
            val spinner__table_value_type = vw.findViewById<Spinner>(R.id.spinner__table_value_type)
            val items2 = arrayOf("Proportional Deaths", "Proprotional Cases", "Total Deaths", "Total Cases")
            val adapter2 = ArrayAdapter<String>(
                this.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
            spinner__table_value_type.setAdapter(adapter2)

            // -------------- Initialise the UI

            val btn__start_date = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.btn__start_date) as Button
            val btn__end_date = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.btn__end_date) as Button
            val rdo__proportional = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.rdo__proportional) as RadioButton
            val rdo__Totals = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.rdo__Totals) as RadioButton
            val switch__reverse_sort = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.switch__reverse_sort) as Switch
            val edittext__max_table_rows = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.edittext__max_table_rows) as EditText
            val btn__reset_all_options = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.btn__reset_all_options) as Button

            btn__start_date.text = (SimpleDateFormat("dd/MMM/yyyy").format(displayOptions.startDate))
            btn__end_date.text = (SimpleDateFormat("dd/MMM/yyyy").format(displayOptions.endDate))

            if (displayOptions.listStatisticsFormat == DisplayOptions.enStatisticsFormat.Proportional) {
                rdo__proportional.isChecked = true
            } else {
                rdo__Totals.isChecked = true
            }

            switch__reverse_sort.isChecked = displayOptions.listReverseSort

            edittext__max_table_rows.setText(displayOptions.tableMaxNumberOfRows.toString())


            // -------------- Build the dialog

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(vw)
                // Add action buttons
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        // TODO: Use the new choices
                        MainViewModel.UpdateDisplayOptionsChanged()
                        getDialog()?.dismiss()
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })

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
}