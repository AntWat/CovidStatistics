package com.ant_waters.covidstatistics

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ant_waters.covidstatistics.model.Country
import com.ant_waters.covidstatistics.model.CountryAggregate
import com.ant_waters.covidstatistics.model.DataManager
import java.text.DecimalFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CountryPopupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CountryPopupFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val args = getArguments()

        var sCountry = " "
        var sContinent = " "
        var sPopulation = " "
        var sProportionalCases = " "
        var sProportionalDeaths = " "
        var sCases = " "
        var sDeaths = " "

        val df1 = DecimalFormat("#")
        val df2 = DecimalFormat("#.0")

        val geoId: String = args?.getString("geoId")?:""

        for (c in DataManager.Countries) {
            if (c.geoId == geoId) {
                sCountry = c.name
                sContinent = "${c.countryCode}, ${c.geoId}, ${c.continent}"
                sPopulation = "Population: ${c.popData2019}"
                break
            }
        }

        for (pr in DataManager.CountryAggregates.Aggregates) {
            if (pr.first.geoId == geoId) {
                val agg = pr.second
                sProportionalCases = getDisplayText("Cases: ", agg.proportionalCases, df1, df2)
                sProportionalDeaths = getDisplayText("Deaths: ", agg.proportionalDeaths, df1, df2)

                sCases = "Total Cases: ${agg.totalCovidCases}"
                sDeaths = "Total Deaths: ${agg.totalCovidDeaths}"
                break
            }
        }

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val vw = inflater.inflate(R.layout.fragment_country_popup, null)

            val imgFlag = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.flag2) as ImageView
            val txtCountry = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.countryText) as TextView
            val txtContinent = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.continentText) as TextView
            val txtPopulation = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.populationText) as TextView
            val txtProportionalCases = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.proportional_casesText) as TextView
            val txtProportionalDeaths = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.proportional_deathsText) as TextView
            val txtCases = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.casesText) as TextView
            val txtDeaths = vw.findViewById<View>(com.ant_waters.covidstatistics.R.id.deathsText) as TextView

            txtCountry.text = sCountry
            txtContinent.text = sContinent
            txtPopulation.text = sPopulation
            txtProportionalCases.text = sProportionalCases
            txtProportionalDeaths.text = sProportionalDeaths
            txtCases.text = sCases
            txtDeaths.text = sDeaths

            val res: Resources = vw.resources
            val resourceId: Int = res.getIdentifier(geoId.lowercase(),
                "drawable", MainActivity.MainPackageName)

            imgFlag.setImageResource(resourceId)


            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(vw)
//                // Add action buttons
//                .setPositiveButton("Sign in",
//                    DialogInterface.OnClickListener { dialog, id ->
//                        // sign in the user ...
//                    })
//                .setNegativeButton("Cancel",
//                    DialogInterface.OnClickListener { dialog, id ->
//                        getDialog()?.cancel()
//                    })
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

//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_country_popup, container, false)
//    }
//
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment CountryPopupFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            CountryPopupFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }

    override fun onStart() {
        super.onStart()
        dialog?.getWindow()?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}