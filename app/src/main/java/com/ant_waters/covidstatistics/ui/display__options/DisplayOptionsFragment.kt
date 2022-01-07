package com.ant_waters.covidstatistics.ui.display__options

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ant_waters.covidstatistics.MainActivity
import com.ant_waters.covidstatistics.R
import com.ant_waters.covidstatistics.model.DataManager
import java.text.DecimalFormat

class DisplayOptionsFragment : DialogFragment()  {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val args = getArguments()

        var sCountry = " "
//        var sContinent = " "
//        var sPopulation = " "
//        var sProportionalCases = " "
//        var sProportionalDeaths = " "
//        var sCases = " "
//        var sDeaths = " "

        val df1 = DecimalFormat("#")
        val df2 = DecimalFormat("#.0")

        val geoId: String = args?.getString("geoId")?:""

        for (c in DataManager.Countries) {
            if (c.geoId == geoId) {
                sCountry = c.name
//                sContinent = "${c.countryCode}, ${c.geoId}, ${c.continent}"
//                sPopulation = "Population: ${c.popData2019}"
                break
            }
        }

//        for (pr in DataManager.CountryAggregates.Aggregates) {
//            if (pr.first.geoId == geoId) {
//                val agg = pr.second
//                sProportionalCases = getDisplayText("Cases: ", agg.proportionalCases, df1, df2)
//                sProportionalDeaths = getDisplayText("Deaths: ", agg.proportionalDeaths, df1, df2)
//
//                sCases = "Total Cases: ${agg.totalCovidCases}"
//                sDeaths = "Total Deaths: ${agg.totalCovidDeaths}"
//                break
//            }
//        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val vw = inflater.inflate(R.layout.fragment_display_options, null)

            // -------------- Add the items to the list (spinner)
            val spinner = vw.findViewById<Spinner>(R.id.sortby_list)
            val items = arrayOf("Country Name", "Proportional Deaths", "Proprotional Cases", "Total Deaths", "Total Cases")
            val adapter = ArrayAdapter<String>(
                this.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
            spinner.setAdapter(adapter)


                //val imgFlag = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.flag2) as ImageView
//            val txtCountry = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.countryText) as TextView
//            val txtContinent = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.continentText) as TextView
//            val txtPopulation = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.populationText) as TextView
//            val txtProportionalCases = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.proportional_casesText) as TextView
//            val txtProportionalDeaths = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.proportional_deathsText) as TextView
//            val txtCases = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.casesText) as TextView
//            val txtDeaths = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.deathsText) as TextView

//            txtCountry.text = sCountry
//            txtContinent.text = sContinent
//            txtPopulation.text = sPopulation
//            txtProportionalCases.text = sProportionalCases
//            txtProportionalDeaths.text = sProportionalDeaths
//            txtCases.text = sCases
//            txtDeaths.text = sDeaths

            val res: Resources = vw.resources
            val resourceId: Int = res.getIdentifier(geoId.lowercase(),
                "drawable", MainActivity.MainPackageName)

//            imgFlag.setImageResource(resourceId)


            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(vw)
                // Add action buttons
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        // TODO: Use the new choices
                        getDialog()?.dismiss()
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun getDisplayText(prefix: String, stat : Double, df1: DecimalFormat, df2: DecimalFormat) : String
    {
        var df: DecimalFormat = df1
        if (stat < 1)
        {
            df = df2
        }
        return "${prefix}${df.format((stat))} per ${DataManager.PopulationScalerLabel}"
    }

    override fun onStart() {
        super.onStart()
        dialog?.getWindow()?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}