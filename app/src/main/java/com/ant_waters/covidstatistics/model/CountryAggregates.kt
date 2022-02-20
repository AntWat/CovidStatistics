package com.ant_waters.covidstatistics.model

import java.util.*

/**
 * Aggregated (summed) data for each country, over a date range
 */
class CountryAggregates {
    lateinit var DateStart: Date
    lateinit var DateEnd: Date

    public fun AddCountry(c: Country2) {
        val agg = CountryAggregate(c, DateStart, DateEnd, 0, 0)
        Aggregates.add(Pair<Country2, CountryAggregate>(c, agg))
    }

    public fun RemoveCountry(c: Country2) {
        var kvp: Pair<Country2, CountryAggregate>? = null
        for (agg in Aggregates) {
            if (agg.first == c) {
                kvp=agg
                break
            }
        }
        if (kvp != null) { Aggregates.remove(kvp) }
    }

    var Aggregates = mutableListOf<Pair<Country2, CountryAggregate>>()

    val SortedByCountry: List<Pair<Country2, CountryAggregate>>
        get() {
            return Aggregates.sortedWith (
                compareBy {it.first.name}
            )
        }

    val SortedByProportionalCases: List<Pair<Country2, CountryAggregate>>
        get() {
            return Aggregates.sortedWith (
                compareByDescending {
                    (if (it.first.popData2019 > DataManager.MinPopulationForRanking)
                        it.second.proportionalCases else 0.0)
                }
            )
        }
    val SortedByProportionalDeaths: List<Pair<Country2, CountryAggregate>>
        get() {
            return Aggregates.sortedWith (
                compareByDescending {
                    (if (it.first.popData2019 > DataManager.MinPopulationForRanking)
                        it.second.proportionalDeaths else 0.0)
                }
            )
        }
    val SortedByTotalCases: List<Pair<Country2, CountryAggregate>>
        get() {
            return Aggregates.sortedWith (
                compareByDescending {
                    (if (it.first.popData2019 > DataManager.MinPopulationForRanking)
                        it.second.totalCovidCases.toDouble() else 0.0)
                }
            )
        }
    val SortedByTotalDeaths: List<Pair<Country2, CountryAggregate>>
        get() {
            return Aggregates.sortedWith (
                compareByDescending {
                    (if (it.first.popData2019 > DataManager.MinPopulationForRanking)
                        it.second.totalCovidDeaths.toDouble() else 0.0)
                }
            )
        }

    fun setData(dateStart: Date, dateEnd: Date,
                dailyCovidsByDate: List<Pair<Date, MutableList<DailyCovid>>>,
                countries: List<Country2>) {
        DateStart = dateStart
        DateEnd = dateEnd

        var countryCasesTotals = mutableMapOf<Country2, Int>()
        var countryDeathsTotals = mutableMapOf<Country2, Int>()

        calculateTotals(
            dailyCovidsByDate,
            dateStart,
            dateEnd,
            countryCasesTotals,
            countryDeathsTotals
        )

        for (cct in countryCasesTotals)
        {
            val c:Country2 = cct.key
            Aggregates.add(Pair<Country2, CountryAggregate>(c, CountryAggregate(c, dateStart, dateEnd,
                                    countryCasesTotals[c]!!, countryDeathsTotals[c]!!)))
        }

        // Add in 0 aggregates for countries that didn't have any data (e.g. dummy countries added for testing db routines)
        // Otherwise they won't appear on the home page
        for (c in countries) {
            var found: Boolean = false
            for (agg in Aggregates) {
                if (agg.first == c) {
                    found = true
                    break
                }
            }
            if (!found) {
                Aggregates.add(Pair<Country2, CountryAggregate>(c,
                    CountryAggregate(c, DataManager.DateStart, DataManager.DateEnd, 0, 0)))
            }
        }
    }

    private fun calculateTotals(
        dailyCovidsByDate: List<Pair<Date, MutableList<DailyCovid>>>,
        dateStart: Date,
        dateEnd: Date,
        countryCasesTotals: MutableMap<Country2, Int>,
        countryDeathsTotals: MutableMap<Country2, Int>
    ) {
        for (dcbd in dailyCovidsByDate) {
            if ((dcbd.first >= dateStart) && (dcbd.first <= dateEnd)) {
                for (dc: DailyCovid in dcbd.second) {
                    val c: Country2 = dc.country
                    if (!countryCasesTotals.containsKey(c)) {
                        countryCasesTotals.put(c, 0)
                        countryDeathsTotals.put(c, 0)
                    }
                    countryCasesTotals[c] = countryCasesTotals[c]!! + dc.covidCases
                    countryDeathsTotals[c] = countryDeathsTotals[c]!! + dc.covidDeaths
                }
            }
        }
    }
}