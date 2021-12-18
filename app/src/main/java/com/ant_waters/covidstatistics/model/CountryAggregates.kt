package com.ant_waters.covidstatistics.model

import com.ant_waters.covidstatistics.database.country
import java.util.*

class CountryAggregates {
    lateinit var DateStart: Date
    lateinit var DateEnd: Date

    var Aggregates = mutableListOf<Pair<Country, CountryAggregate>>()

    val SortedByProportionalCases: List<Pair<Country, CountryAggregate>>
        get() {
            return Aggregates.sortedWith (
                compareByDescending {
                    (if (it.first.popData2019 > DataManager.MinPopulationForRanking)
                        it.second.proportionalCases else 0.0)
                }
            )
        }
    val SortedByProportionalDeaths: List<Pair<Country, CountryAggregate>>
        get() {
            return Aggregates.sortedWith (
                compareByDescending {
                    (if (it.first.popData2019 > DataManager.MinPopulationForRanking)
                        it.second.proportionalDeaths else 0.0)
                }
            )
        }
    val SortedByTotalCases: List<Pair<Country, CountryAggregate>>
        get() {
            return Aggregates.sortedWith (
                compareByDescending {
                    (if (it.first.popData2019 > DataManager.MinPopulationForRanking)
                        it.second.totalCovidCases.toDouble() else 0.0)
                }
            )
        }
    val SortedByTotalDeaths: List<Pair<Country, CountryAggregate>>
        get() {
            return Aggregates.sortedWith (
                compareByDescending {
                    (if (it.first.popData2019 > DataManager.MinPopulationForRanking)
                        it.second.totalCovidDeaths.toDouble() else 0.0)
                }
            )
        }

    fun SetData(dateStart: Date, dateEnd: Date,
                dailyCovidsByDate: List<Pair<Date, MutableList<DailyCovid>>>) {
        DateStart = dateStart
        DateEnd = dateEnd

        var countryCasesTotals = mutableMapOf<Country, Int>()
        var countryDeathsTotals = mutableMapOf<Country, Int>()

        CalculateTotals(
            dailyCovidsByDate,
            dateStart,
            dateEnd,
            countryCasesTotals,
            countryDeathsTotals
        )

        for (cct in countryCasesTotals)
        {
            val c:Country = cct.key
            Aggregates.add(Pair<Country, CountryAggregate>(c, CountryAggregate(c, dateStart, dateEnd,
                                    countryCasesTotals[c]!!, countryDeathsTotals[c]!!)))
        }
    }

    private fun CalculateTotals(
        dailyCovidsByDate: List<Pair<Date, MutableList<DailyCovid>>>,
        dateStart: Date,
        dateEnd: Date,
        countryCasesTotals: MutableMap<Country, Int>,
        countryDeathsTotals: MutableMap<Country, Int>
    ) {
        for (dcbd in dailyCovidsByDate) {
            if ((dcbd.first >= dateStart) && (dcbd.first <= dateEnd)) {
                for (dc: DailyCovid in dcbd.second) {
                    val c: Country = dc.country
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