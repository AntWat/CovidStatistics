package com.ant_waters.covidstatistics.ui.display__options

import java.util.*


class DisplayOptions (
                      startDate: Date = Date(0), endDate: Date = Date(0),
                      listStatisticsFormat: enStatisticsFormat = enStatisticsFormat.Proportional,
                      listSortBy: enSortBy = enSortBy.CountryName,
                      listReverseSort: Boolean,
                      tableValueType: enTableValueType = enTableValueType.TotalCases,
                      tableMaxNumberOfRows: Int = 100
){
    enum class enStatisticsFormat {Proportional, Totals}
    enum class enSortBy {CountryName, ProportionalCases, ProportionalDeaths, TotalCases, TotalDeaths}
    enum class enTableValueType {ProportionalCases, ProportionalDeaths, TotalCases, TotalDeaths}
}