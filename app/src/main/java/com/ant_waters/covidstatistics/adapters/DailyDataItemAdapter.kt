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

import java.util.*

class DailyDataItemAdapter(private val context: Fragment,
                           private val dailyCovidsByDate : List<Pair<Date, MutableList<DailyCovid>>>)
    : RecyclerView.Adapter<DailyDataItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val dateView: TextView = view.findViewById(R.id.item_date)
        val countryView: TextView = view.findViewById(R.id.item_country)
        val casesView: TextView = view.findViewById(R.id.item_cases)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_data_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        //if (dailyCovidsByDate == null) return

        val kvp = dailyCovidsByDate[position]
        holder.dateView.text =  kvp.first.toString()
        val dc: DailyCovid = kvp.second[0]
        holder.countryView.text =  dc.country.name
        holder.casesView.text =  dc.covidCases.toString()
    }

    override fun getItemCount() = dailyCovidsByDate.count()


}