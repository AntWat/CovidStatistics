package com.ant_waters.covidstatistics.adapters

import android.content.Context
import android.content.res.Resources
import android.icu.number.NumberFormatter
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ant_waters.covidstatistics.R

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.ant_waters.covidstatistics.MainActivity
import com.ant_waters.covidstatistics.enDataLoaded
import com.ant_waters.covidstatistics.model.*
import java.text.DecimalFormat

import java.util.*

class DailyDataItemAdapter(private val context: Fragment,
                           private val countries: List<Country>,
                           private val countryAggregates : CountryAggregates?
)
    : RecyclerView.Adapter<DailyDataItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val countryView: TextView = view.findViewById(R.id.item_country)
        val casesView: TextView = view.findViewById(R.id.item_cases)
        val deathsView: TextView = view.findViewById(R.id.item_deaths)
        val flagView: ImageView = view.findViewById(R.id.item_flag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        Log.i(MainActivity.LOG_TAG, "DailyDataItemAdapter.onCreateViewHolder: Started")

        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_data_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (MainActivity.DataInitialised.value == enDataLoaded.CountriesOnly)
            DisplayCountry(holder, position)
        else  if (MainActivity.DataInitialised.value==enDataLoaded.All)
            DisplayCountryAndData(holder, position)
    }

    fun DisplayCountry(holder: ItemViewHolder, position: Int) {
        if (countries == null) { return }

        val c = countries[position]
        holder.countryView.text = c.name

        holder.casesView.text = "Population: ${c.popData2019}"
        holder.deathsView.text = " "

        val res: Resources = context.resources
        val resourceId: Int = res.getIdentifier(c.geoId.lowercase(),
            "drawable", MainActivity.MainPackageName)

        holder.flagView.setImageResource(resourceId)
    }

    fun DisplayCountryAndData(holder: ItemViewHolder, position: Int) {
        if (countryAggregates == null) { return }

        val kvp = countryAggregates.Aggregates[position]
        holder.countryView.text = kvp.first.name
        val agg: CountryAggregate = kvp.second
        val df1 = DecimalFormat("#")
        val df2 = DecimalFormat("#.0")

        holder.casesView.text = getDisplayText("Cases: ", agg.proportionalCases, df1, df2)
        holder.deathsView.text = getDisplayText("Deaths: ", agg.proportionalDeaths, df1, df2)

        val res: Resources = context.resources
        val resourceId: Int = res.getIdentifier(agg.country.geoId.lowercase(),
            "drawable", MainActivity.MainPackageName)

        holder.flagView.setImageResource(resourceId)
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

    override fun getItemCount() =
        (if (MainActivity.DataInitialised.value==enDataLoaded.CountriesOnly)
            countries.size
        else
            if (MainActivity.DataInitialised.value==enDataLoaded.All)
                countryAggregates?.Aggregates?.count()?:0
            else
                0)

}