package com.ant_waters.covidstatistics.ui.display__options

import java.util.*


public class DisplayOptions (
                      var startDate: Date = Date(0), var endDate: Date = Date(0),
                      var listStatisticsFormat: enStatisticsFormat = enStatisticsFormat.Proportional,
                      var listSortBy: enSortBy = enSortBy.CountryName,
                      var listReverseSort: Boolean = false,
                      var tableValueType: enTableValueType = enTableValueType.TotalCases,
                      var tableMaxNumberOfRows: Int = 100
){
    enum class enStatisticsFormat {Proportional, Totals}
    enum class enSortBy {CountryName, ProportionalCases, ProportionalDeaths, TotalCases, TotalDeaths}
    enum class enTableValueType {ProportionalCases, ProportionalDeaths, TotalCases, TotalDeaths}
}