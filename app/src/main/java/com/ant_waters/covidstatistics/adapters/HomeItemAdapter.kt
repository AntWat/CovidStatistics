package com.ant_waters.covidstatistics.adapters

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.ant_waters.covidstatistics.model.*
import java.text.DecimalFormat

import com.ant_waters.covidstatistics.*
import android.os.Bundle
import com.ant_waters.covidstatistics.ui.country_pop_up.CountryPopupFragment
import com.ant_waters.covidstatistics.ui.display__options.DisplayOptions

class HomeItemAdapter(private val context: Fragment,
                      private val countries: List<Country2>,
                      private val countryAggregates : CountryAggregates?
)
    : RecyclerView.Adapter<HomeItemAdapter.ItemViewHolder>(){

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val countryView: TextView = view.findViewById(R.id.item_country)
        val casesView: TextView = view.findViewById(R.id.item_cases)
        val deathsView: TextView = view.findViewById(R.id.item_deaths)
        val flagView: ImageView = view.findViewById(R.id.item_flag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        Log.i(MainViewModel.LOG_TAG, "HomeDataItemAdapter.onCreateViewHolder: Started")

        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_item, parent, false)

        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (MainViewModel.DataInitialised.value == enDataLoaded.CountriesOnly)
            displayCountry(holder, position)
        else  if (MainViewModel.DataInitialised.value==enDataLoaded.All)
            displayCountryAndData(holder, position)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val cpf = CountryPopupFragment()

            // Supply country as an argument.
            val args = Bundle()
            val c = countries[position]
            args.putString("geoId", c.geoId)
            cpf.setArguments(args)

            cpf.show(context.childFragmentManager, "countrypopup_from_home")
        })
    }

    fun displayCountry(holder: ItemViewHolder, position: Int) {
        if (countries == null) { return }

        val c = countries[position]
        holder.countryView.text = c.name

        holder.casesView.text = "Population: ${c.popData2019}"
        holder.deathsView.text = " "

        val res: Resources = context.resources
        val resourceId: Int = res.getIdentifier(c.geoId.lowercase(),
            "drawable", MainViewModel.MainPackageName)

        holder.flagView.setImageResource(resourceId)
    }

    fun displayCountryAndData(holder: ItemViewHolder, position: Int) {
        if (countryAggregates == null) { return }

        var aggList = countryAggregates.SortedByCountry
        when (MainViewModel.DisplayOptions.listSortBy) {
            DisplayOptions.enSortBy.TotalDeaths -> { aggList = countryAggregates.SortedByTotalDeaths };
            DisplayOptions.enSortBy.ProportionalDeaths -> { aggList = countryAggregates.SortedByProportionalDeaths };
            DisplayOptions.enSortBy.TotalCases -> { aggList = countryAggregates.SortedByTotalCases };
            DisplayOptions.enSortBy.ProportionalCases -> { aggList = countryAggregates.SortedByProportionalCases };
        }

        var index = position
        if (MainViewModel.DisplayOptions.listReverseSort) {index = aggList.size - position - 1}

        val kvp = aggList[index]
        holder.countryView.text = kvp.first.name
        val agg: CountryAggregate = kvp.second
        val df1 = DecimalFormat("#")
        val df2 = DecimalFormat("#.0")

        if  (MainViewModel.DisplayOptions.listStatisticsFormat == DisplayOptions.enStatisticsFormat.Proportional) {
            holder.casesView.text = getProportionalDisplayText("Cases: ", agg.proportionalCases, df1, df2)
            holder.deathsView.text = getProportionalDisplayText("Deaths: ", agg.proportionalDeaths, df1, df2)
        } else {
            holder.casesView.text = getTotalDisplayText("Cases: ", agg.totalCovidCases)
            holder.deathsView.text = getTotalDisplayText("Deaths: ", agg.totalCovidDeaths)
        }

        val res: Resources = context.resources
        val resourceId: Int = res.getIdentifier(agg.country.geoId.lowercase(),
            "drawable", MainViewModel.MainPackageName)

        holder.flagView.setImageResource(resourceId)
    }

    fun getProportionalDisplayText(prefix: String, stat : Double, df1: DecimalFormat, df2: DecimalFormat) : String
    {
        var df: DecimalFormat = df1
        if (stat < 1)
        {
            df = df2
        }
        return "${prefix}${df.format((stat))} per ${DataManager.PopulationScalerLabel}"
    }
    fun getTotalDisplayText(prefix: String, stat : Int) : String
    {
        return "${prefix}${stat}"
    }

    override fun getItemCount() =
        (if (MainViewModel.DataInitialised.value==enDataLoaded.CountriesOnly)
            countries.size
        else
            if (MainViewModel.DataInitialised.value==enDataLoaded.All)
                countryAggregates?.Aggregates?.count()?:0
            else
                0)

}