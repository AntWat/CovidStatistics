package com.ant_waters.covidstatistics.adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ant_waters.covidstatistics.R
import com.ant_waters.covidstatistics.model.DailyCovid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ant_waters.covidstatistics.model.CountryAggregate
import com.ant_waters.covidstatistics.model.CountryAggregates
import com.ant_waters.covidstatistics.model.DataManager

import java.util.*

class DailyDataItemAdapter(private val context: Fragment,
                           private val countryAggregates : CountryAggregates
)
    : RecyclerView.Adapter<DailyDataItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val countryView: TextView = view.findViewById(R.id.item_country)
        val casesView: TextView = view.findViewById(R.id.item_cases)
        val deathsView: TextView = view.findViewById(R.id.item_deaths)
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
        holder.casesView.text = "Cases: ${agg.proportionalCases.toString()} per ${DataManager.PopulationScalerLabel}"
        holder.deathsView.text = "Deaths: ${agg.proportionalDeaths.toString()} per ${DataManager.PopulationScalerLabel}"
    }

    override fun getItemCount() = countryAggregates.Aggregates.count()


}