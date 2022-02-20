package com.ant_waters.covidstatistics.ui.edit__countries

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.ant_waters.covidstatistics.MainViewModel
import com.ant_waters.covidstatistics.R
import com.ant_waters.covidstatistics.model.Continent2
import com.ant_waters.covidstatistics.model.Country2
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.ant_waters.covidstatistics.model.DataManager
import com.ant_waters.covidstatistics.ui.CustomArrayAdapterDisplayingName
import android.widget.TextView
import com.ant_waters.covidstatistics.Utils.Utils

class EditCountriesFragment  : DialogFragment()  {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return buildCountriesEditor(activity)
    }

    var country_items: MutableList<Country2> = mutableListOf<Country2>()
    var new_countries: MutableList<Country2> = mutableListOf<Country2>()
    var modified_countries: MutableList<Country2> = mutableListOf<Country2>()
    var deleted_countries: MutableList<Country2> = mutableListOf<Country2>()

    lateinit var continent_items: List<Continent2>

    lateinit var myView: View

    lateinit var linear_layout__edit_country: LinearLayout
    lateinit var linear_layout__add_country: LinearLayout

    lateinit var spinner__countryName: Spinner
    lateinit var btn__addCountry: Button
    lateinit var spinner__continentName: Spinner
    lateinit var textView_geoId: TextView
    lateinit var editText_territoryCode: EditText
    lateinit var editText_population: EditText
    lateinit var btn_cancel: Button
    lateinit var btn_delete: Button
    lateinit var btn_ok: Button

    lateinit var editText_addCountryName: EditText
    lateinit var editText_addCountryGeoId: EditText
    lateinit var btn__addCountryYes: Button
    lateinit var btn__addCountryNo: Button

    lateinit var currentCountry: Country2

    fun buildCountriesEditor(activity: FragmentActivity?) : Dialog
    {
        try {
            Log.i(MainViewModel.LOG_TAG, "buildCountriesEditor: Started")
            if (activity == null) {throw IllegalStateException("Activity cannot be null")}

            val builder = MaterialAlertDialogBuilder(activity)         // or use MaterialAlertDialogBuilder
            val inflater = requireActivity().layoutInflater;
            myView = inflater.inflate(R.layout.fragment_edit_countries, null)
            getWidgets()

            // -------------- Add the items to the countryName list

            for (c in DataManager.Countries) {
                country_items.add(c.copy())     // Copy the items, so we have control over whether or not to save changes
            }

            currentCountry = country_items[0]

            SetCountryListAdapter()
            spinner__countryName?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) { }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    SwitchCountry(country_items[position], false)
                }
            }

            // -------------- Add the items to the continentName list
            continent_items = DataManager.Continents.map { it }
            val continent_adapter = CustomArrayAdapterDisplayingName(
                this.requireContext(),
                android.R.layout.simple_spinner_dropdown_item, 0,
                continent_items
            )
            spinner__continentName.setAdapter(continent_adapter)

            // -------------- Set button events
            btn__addCountry.setOnClickListener { btn__addCountry_click() }
            btn_cancel.setOnClickListener { btn_cancel_click() }
            btn_delete.setOnClickListener { btn_delete_click() }
            btn_ok.setOnClickListener { btn_ok_click() }
            btn__addCountryYes.setOnClickListener { btn__addCountryYes_click() }
            btn__addCountryNo.setOnClickListener { btn__addCountryNo_click() }


            // -------------- Initialise the UI
//            showDisplayOptions()

            RefreshCountry(true)

            // -------------- Build the dialog
            builder.setView(myView)
            // Add action buttons: // .setPositiveButton, .setNeutralButton, .setNegativeButton
            // Note: I am not using these because they always close the dialog when the button is pressed,
            // so there is no way to validate the choices first.  I am using custom buttons instead.

            //builder.setCanceledOnTouchOutside(false);     // This also doesn't seem to work as expected!

            return builder.create()
        }
        catch (ex: Exception)
        {
            //Log.e(MainViewModel.LOG_TAG, ex.message?:"")
            throw(ex)
        }
    }

    fun getWidgets()
    {
        linear_layout__edit_country = myView.findViewById<LinearLayout>(R.id.linear_layout__edit_country)
        linear_layout__add_country = myView.findViewById<LinearLayout>(R.id.linear_layout__add_country)

        spinner__countryName = myView.findViewById<Spinner>(R.id.spinner__countryName)
        btn__addCountry = myView.findViewById<Button>(R.id.btn__addCountry)
        spinner__continentName = myView.findViewById<Spinner>(R.id.spinner__continentName)
        textView_geoId = myView.findViewById<TextView>(R.id.textView_geoId)
        editText_territoryCode = myView.findViewById<EditText>(R.id.editText_territoryCode)
        editText_population = myView.findViewById<EditText>(R.id.editText_population)
        btn_cancel = myView.findViewById<Button>(R.id.btn_cancel)
        btn_delete = myView.findViewById<Button>(R.id.btn_delete)
        btn_ok = myView.findViewById<Button>(R.id.btn_ok)

        editText_addCountryName = myView.findViewById<EditText>(R.id.editText_addCountryName)
        editText_addCountryGeoId = myView.findViewById<EditText>(R.id.editText_addCountryGeoId)
        btn__addCountryYes = myView.findViewById<Button>(R.id.btn__addCountryYes)
        btn__addCountryNo = myView.findViewById<Button>(R.id.btn__addCountryNo)
    }

    fun SwitchCountry(c: Country2, setSpinner__countryName: Boolean) {
        try {
            if (c == currentCountry) { return }

            // Apply any changes to the previously selected country
            val errMsg = updateCurrentCountry()
            if (errMsg.length > 0) {
                Log.e(MainViewModel.LOG_TAG, errMsg)
                ShowError(errMsg)

                spinner__countryName.setSelection(country_items.indexOf(currentCountry))
            } else {
                // Select and display the new one
                currentCountry = c
                RefreshCountry(setSpinner__countryName)
            }
        }
        catch (ex:Exception) { LogAndShowError(ex)}
    }

    fun RefreshCountry(setSpinner__countryName: Boolean)
    {
        if (setSpinner__countryName) {
            spinner__countryName.setSelection(country_items.indexOf(currentCountry)) }
        spinner__continentName.setSelection(continent_items.indexOf(currentCountry.continent))
        textView_geoId.setText(currentCountry.geoId)
        editText_territoryCode.setText(currentCountry.countryCode)
        editText_population.setText(currentCountry.popData2019.toString())
    }

    fun updateCurrentCountry() : String
    {
        // --------------- Validate
        var population = 0

//        var geoId = textView_geoId.text.toString()
//        if (geoId.length != 2) { return "geoId must be 2 characters!" }

        var countryCode = editText_territoryCode.text.toString()
        if (countryCode.length != 3) { return "countryCode must be 3 characters!" }

        try { population = editText_population.text.toString().toInt() }
        catch (ex: Exception) { return "Population is not an integer!" }

        // --------------- Update
        val c = spinner__continentName.selectedItem
        updatePropertyIfChanged<Continent2?>(currentCountry.continent,
            spinner__continentName.selectedItem as Continent2, fun(v) { currentCountry.continent = v })

//        updatePropertyIfChanged<String>(currentCountry.geoId,
//            geoId, fun(v) { currentCountry.geoId = v })

        updatePropertyIfChanged<String>(currentCountry.countryCode,
            countryCode, fun(v) { currentCountry.countryCode = v })

        updatePropertyIfChanged<Int>(currentCountry.popData2019,
            population, fun(v) { currentCountry.popData2019 = v })

        return ""
    }

    fun <T>updatePropertyIfChanged(oldVal: T, newVal: T, updatefunc: (T)->Unit ) {
        if (oldVal != newVal)
        {
            updatefunc(newVal)
            if (!new_countries.contains(currentCountry)
                && !modified_countries.contains(currentCountry)) { modified_countries.add(currentCountry)}
        }
    }

    // ---------------------------------------

    fun ShowError(errMsg: String) {
        Utils.ShowToast(getActivity()?.getApplicationContext(),
            "Error! ${errMsg}",
            Toast.LENGTH_LONG, Gravity.TOP, Color.RED)
    }

    fun LogAndShowError(ex:Exception)
    {
        val errMsg: String = ex.message.toString()
        Log.e(MainViewModel.LOG_TAG, errMsg)
        ShowError(errMsg)
    }

    // ---------------------------------------

    fun btn__addCountry_click() {
        try {
            val errMsg = updateCurrentCountry()
            if (errMsg.length > 0) {
                ShowError(errMsg)
                return      // Errors need to be sorted out first
            }

            linear_layout__edit_country.visibility = GONE
            linear_layout__add_country.visibility = VISIBLE
        }
        catch (ex:Exception) { LogAndShowError(ex)}
    }

    fun btn_cancel_click() {
        try {
            // TODO: Confirm lose changes
            // TODO: Also, deal with loss of changes if the users just clicks outside this window (see setCanceledOnTouchOutside)
            getDialog()?.dismiss()
        }
        catch (ex:Exception) { LogAndShowError(ex)}
    }

    fun btn_delete_click() {
        try {
            var index = spinner__countryName.selectedItemId.toInt() -1  // Next item to select
            if (index < 0) { index = 0 }

            country_items.remove(currentCountry)
            if (modified_countries.contains(currentCountry)) { modified_countries.remove(currentCountry) }
            if (new_countries.contains(currentCountry))
            {
                new_countries.remove(currentCountry)
            } else {
                deleted_countries.add(currentCountry)
            }

            currentCountry = country_items[index]
            SetCountryListAdapter()
            RefreshCountry(true)
        }
        catch (ex:Exception) { LogAndShowError(ex)}
    }

    fun btn_ok_click() {
        try {
            val errMsg = updateCurrentCountry()
            if (errMsg.length > 0) {
                ShowError(errMsg)
                return      // Errors need to be sorted out first
            }

            // TODO: Confirm Save

            // ----------- Update the global lists (instead pf re-loading the database, which takes unneccessarily long
            for (newC in new_countries) {
                DataManager.AddCountry(newC)
            }

            for (modC in modified_countries) {
                val c = DataManager.CountriesByName[modC.name]
                if (c == null) { break }
                c!!.continent = modC.continent
                //c!!.geoId = modC.geoId
                c!!.countryCode = modC.countryCode
                c!!.popData2019 = modC.popData2019
            }

            for (newC in deleted_countries) {
                DataManager.DeleteCountry(newC)
            }

            MainViewModel.updateDisplayOptionsChanged()

            // ----------- Update the database, so changes persist if the app is restarted
            MainViewModel.UpdateDatabase(new_countries, null,
                modified_countries, null,
                deleted_countries, null,
                this.requireContext(),
                fun() { getDialog()?.dismiss() },
                fun(errMsg) { ShowError(errMsg)})
        }
        catch (ex:Exception) { LogAndShowError(ex)}
    }

    fun btn__addCountryYes_click() {
        try {
            var countryName = editText_addCountryName.text.toString()
            var geoId = editText_addCountryGeoId.text.toString()

            if ((countryName.length > 0) && (geoId.length > 0)) {
                currentCountry = Country2(countryName, geoId, "XXX", 0, "Asia")
                country_items.add(0, currentCountry)
                new_countries.add(currentCountry)
                SetCountryListAdapter()
                RefreshCountry(true)
                linear_layout__edit_country.visibility = VISIBLE
                linear_layout__add_country.visibility = GONE
            }
        }
        catch (ex:Exception) { LogAndShowError(ex)}
    }

    fun btn__addCountryNo_click() {
        try {
            linear_layout__edit_country.visibility = VISIBLE
            linear_layout__add_country.visibility = GONE
        }
        catch (ex:Exception) { LogAndShowError(ex)}
    }

    fun SetCountryListAdapter() {
        val country_adapter = CustomArrayAdapterDisplayingName(
            this.requireContext(),
            android.R.layout.simple_spinner_dropdown_item, 0,
            country_items
        )
        spinner__countryName.setAdapter(country_adapter)
    }
}