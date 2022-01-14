package com.ant_waters.covidstatistics.ui.display__options

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.ant_waters.covidstatistics.MainViewModel
import com.ant_waters.covidstatistics.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class JunkDialogFragment()  : DialogFragment()  {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return buildDisplay(activity)
    }

    lateinit var myView: View

    fun buildDisplay(activity: FragmentActivity?) : Dialog
    {
        if (activity == null) {throw IllegalStateException("Activity cannot be null")}

        val builder = MaterialAlertDialogBuilder(activity)         // or use MaterialAlertDialogBuilder
        val inflater = requireActivity().layoutInflater;
        myView = inflater.inflate(R.layout.junk_dialog_fragment, null)

        builder.setView(myView)

        return builder.create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.getWindow()?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }


}