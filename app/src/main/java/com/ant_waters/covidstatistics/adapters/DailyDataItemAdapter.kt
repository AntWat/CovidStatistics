package com.ant_waters.covidstatistics.adapters

import android.content.Context
import android.content.res.Resources
import android.icu.number.NumberFormatter
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ant_waters.covidstatistics.R
import com.ant_waters.covidstatistics.model.DailyCovid

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.ant_waters.covidstatistics.MainActivity
import com.ant_waters.covidstatistics.model.CountryAggregate
import com.ant_waters.covidstatistics.model.CountryAggregates
import com.ant_waters.covidstatistics.model.DataManager
import java.text.DecimalFormat

import java.util.*

class DailyDataItemAdapter(private val context: Fragment,
                           private val countryAggregates : CountryAggregates
)
    : RecyclerView.Adapter<DailyDataItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val countryView: TextView = view.findViewById(R.id.item_country)
        val casesView: TextView = view.findViewById(R.id.item_cases)
        val deathsView: TextView = view.findViewById(R.id.item_deaths)
        val flagView: ImageView = view.findViewById(R.id.item_flag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_data_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        //if (dailyCovidsByDate == null) return

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

    override fun getItemCount() = countryAggregates.Aggregates.count()


}