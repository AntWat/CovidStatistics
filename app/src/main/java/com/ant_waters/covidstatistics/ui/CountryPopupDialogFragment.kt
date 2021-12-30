package com.ant_waters.covidstatistics.ui

import android.app.AlertDialog
import android.app.Dialog
//import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
//import androidx.fragment.app.DialogFragment
import com.ant_waters.covidstatistics.R

class CountryPopupDialogFragment  : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("fire missiles?")
                .setPositiveButton("FIRE",
                    DialogInterface.OnClickListener { dialog, id ->
                        // FIRE ZE MISSILES!
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}