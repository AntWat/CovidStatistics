package com.ant_waters.covidstatistics.model

import android.content.Context
import java.time.LocalDate
import java.util.*
import android.content.res.AssetManager
import com.ant_waters.covidstatistics.Utils.SimpleTable
import com.ant_waters.covidstatistics.Utils.readCsv
import java.io.InputStream
import java.text.SimpleDateFormat
import kotlin.math.ceil


class DataManager {
    companion object {
        lateinit var DateStart: Date
        lateinit var DateEnd: Date

        private var _dailyCovidsByDate = listOf<Pair<Date, MutableList<DailyCovid>>>()
        val DailyCovidsByDate get() = this._dailyCovidsByDate

        private lateinit var _countryAggregates: CountryAggregates
        val CountryAggregates get() = this._countryAggregates

        val PopulationScaler = 100000      // cases are usally reported per 100k of population
        val PopulationScalerLabel = "100k"
    }

    fun LoadData(context: Context): Boolean {
        // TODO: Load data from CSV or Database

        try {
            val countries = LoadCountries()

            _dailyCovidsByDate = LoadRealData(context, countries).toList()
            //_dailyCovidsByDate = LoadTestData(countries).toList()

            _countryAggregates = CountryAggregates(DateStart, DateEnd, _dailyCovidsByDate)
            return true
        } catch (ex: Exception) {
            // TODO: Errorhandling or logging
            return false
        }
    }

    private fun LoadRealData(context: Context, countries: MutableMap</*name*/String, Country>)
                                            : MutableMap<Date, MutableList<DailyCovid>> {
        try {
            val inputStream: InputStream = context.assets.open("ECPDC_CovidData1.csv")
            val csv = readCsv(inputStream)

            val dailyCovidsByDate = mutableMapOf<Date, MutableList<DailyCovid>>()

            for (iRow in 0..csv.Rows.count()-1) {
                val row = csv.Rows[iRow]
                val d = SimpleDateFormat("dd/MM/yyyy").parse(row[0])
                if (iRow == 0) {
                    DateStart = d
                } else if (iRow == csv.Rows.count()-1) {
                    DateEnd = d
                }

                val daylies = mutableListOf<DailyCovid>()

                for (i:Int in 1..row.count()-1) {
                    val c: Country? = countries[csv.Headers[i]]
                    if (c == null) { throw Exception("Header is not a recognised country: '${csv.Headers[i]}'") }
                    val numCases:Int = if (row[i] == "") 0 else row[i].toInt()
                    daylies.add(DailyCovid(c, d,
                        numCases,
                        /*TODO*/ceil(numCases * .01).toInt()))
                }
                dailyCovidsByDate.put(d, daylies)
            }

            return dailyCovidsByDate
        }
        catch (ex: Exception)
        {
            throw(ex)
        }
    }

    private fun LoadTestData(countries: MutableMap</*name*/String, Country>):
                                MutableMap<Date, MutableList<DailyCovid>> {
        val dailyCovidsByDate = mutableMapOf<Date, MutableList<DailyCovid>>()

        // TODO: The date iteration here is dodgy, but the recommended version won't work on my test phone (Android 6/api 23)
        DateStart = Date(2019, 12, 31)
        DateEnd = Date(2020, 12, 14)

        val casesPer100k = 10
        val deathsPer100k = 1

        iterateBetweenDatesJava7(DateStart, DateEnd,
            fun(d: Date) {
                val daylies = mutableListOf<DailyCovid>()
                for (kvp in countries) {
                    val c = kvp.value
                    daylies.add(DailyCovid(c, d,
                        casesPer100k * c.popData2019/DataManager.PopulationScaler,
                        deathsPer100k * c.popData2019/DataManager.PopulationScaler))
                }
                dailyCovidsByDate.put(d, daylies)
            })

        return dailyCovidsByDate
    }

    fun iterateBetweenDatesJava7(start: Date, end: Date?, processDate: (Date) -> Unit) {
        var current = start
        while (current.before(end)) {
            processDate(current)
            val calendar = Calendar.getInstance()
            calendar.time = current
            calendar.add(Calendar.DATE, 1)
            current = calendar.time
        }
    }

    private fun LoadCountries(): MutableMap</*name*/String, Country> {
        val countries = mutableMapOf</*name*/String, Country>()
        countries.put("Afghanistan", Country("Afghanistan", "AF", "AFG", 38041757, "Asia"))
        countries.put("Albania", Country("Albania", "AL", "ALB", 2862427, "Europe"))
        countries.put("Algeria", Country("Algeria", "DZ", "DZA", 43053054, "Africa"))
        countries.put("Andorra", Country("Andorra", "AD", "AND", 76177, "Europe"))
        countries.put("Angola", Country("Angola", "AO", "AGO", 31825299, "Africa"))
        countries.put("Anguilla", Country("Anguilla", "AI", "AIA", 14872, "America"))
        countries.put("Antigua_and_Barbuda", Country("Antigua_and_Barbuda", "AG", "ATG", 97115, "America"))
        countries.put("Argentina", Country("Argentina", "AR", "ARG", 44780675, "America"))
        countries.put("Armenia", Country("Armenia", "AM", "ARM", 2957728, "Europe"))
        countries.put("Aruba", Country("Aruba", "AW", "ABW", 106310, "America"))
        countries.put("Australia", Country("Australia", "AU", "AUS", 25203200, "Oceania"))
        countries.put("Austria", Country("Austria", "AT", "AUT", 8858775, "Europe"))
        countries.put("Azerbaijan", Country("Azerbaijan", "AZ", "AZE", 10047719, "Europe"))
        countries.put("Bahamas", Country("Bahamas", "BS", "BHS", 389486, "America"))
        countries.put("Bahrain", Country("Bahrain", "BH", "BHR", 1641164, "Asia"))
        countries.put("Bangladesh", Country("Bangladesh", "BD", "BGD", 163046173, "Asia"))
        countries.put("Barbados", Country("Barbados", "BB", "BRB", 287021, "America"))
        countries.put("Belarus", Country("Belarus", "BY", "BLR", 9452409, "Europe"))
        countries.put("Belgium", Country("Belgium", "BE", "BEL", 11455519, "Europe"))
        countries.put("Belize", Country("Belize", "BZ", "BLZ", 390351, "America"))
        countries.put("Benin", Country("Benin", "BJ", "BEN", 11801151, "Africa"))
        countries.put("Bermuda", Country("Bermuda", "BM", "BMU", 62508, "America"))
        countries.put("Bhutan", Country("Bhutan", "BT", "BTN", 763094, "Asia"))
        countries.put("Bolivia", Country("Bolivia", "BO", "BOL", 11513102, "America"))
        countries.put("Bonaire_Saint Eustatius and Saba", Country("Bonaire_Saint Eustatius and Saba", "BQ", "BES", 25983, "America"))
        countries.put("Bosnia_and_Herzegovina", Country("Bosnia_and_Herzegovina", "BA", "BIH", 3300998, "Europe"))
        countries.put("Botswana", Country("Botswana", "BW", "BWA", 2303703, "Africa"))
        countries.put("Brazil", Country("Brazil", "BR", "BRA", 211049519, "America"))
        countries.put("British_Virgin_Islands", Country("British_Virgin_Islands", "VG", "VGB", 30033, "America"))
        countries.put("Brunei_Darussalam", Country("Brunei_Darussalam", "BN", "BRN", 433296, "Asia"))
        countries.put("Bulgaria", Country("Bulgaria", "BG", "BGR", 7000039, "Europe"))
        countries.put("Burkina_Faso", Country("Burkina_Faso", "BF", "BFA", 20321383, "Africa"))
        countries.put("Burundi", Country("Burundi", "BI", "BDI", 11530577, "Africa"))
        countries.put("Cambodia", Country("Cambodia", "KH", "KHM", 16486542, "Asia"))
        countries.put("Cameroon", Country("Cameroon", "CM", "CMR", 25876387, "Africa"))
        countries.put("Canada", Country("Canada", "CA", "CAN", 37411038, "America"))
        countries.put("Cape_Verde", Country("Cape_Verde", "CV", "CPV", 549936, "Africa"))
        countries.put("Cases_on_an_international_conveyance_Japan", Country("Cases_on_an_international_conveyance_Japan","JPG11668","0",0,"Other")        )
        countries.put("Cayman_Islands", Country("Cayman_Islands", "KY", "CYM", 64948, "America"))
        countries.put("Central_African_Republic", Country("Central_African_Republic", "CF", "CAF", 4745179, "Africa"))
        countries.put("Chad", Country("Chad", "TD", "TCD", 15946882, "Africa"))
        countries.put("Chile", Country("Chile", "CL", "CHL", 18952035, "America"))
        countries.put("China", Country("China", "CN", "CHN", 1433783692, "Asia"))
        countries.put("Colombia", Country("Colombia", "CO", "COL", 50339443, "America"))
        countries.put("Comoros", Country("Comoros", "KM", "COM", 850891, "Africa"))
        countries.put("Congo", Country("Congo", "CG", "COG", 5380504, "Africa"))
        countries.put("Costa_Rica", Country("Costa_Rica", "CR", "CRI", 5047561, "America"))
        countries.put("Cote_dIvoire", Country("Cote_dIvoire", "CI", "CIV", 25716554, "Africa"))
        countries.put("Croatia", Country("Croatia", "HR", "HRV", 4076246, "Europe"))
        countries.put("Cuba", Country("Cuba", "CU", "CUB", 11333484, "America"))
        countries.put("CuraÃ§ao", Country("CuraÃ§ao", "CW", "CUW", 163423, "America"))
        countries.put("Cyprus", Country("Cyprus", "CY", "CYP", 875899, "Europe"))
        countries.put("Czechia", Country("Czechia", "CZ", "CZE", 10649800, "Europe"))
        countries.put("Democratic_Republic_of_the_Congo", Country("Democratic_Republic_of_the_Congo", "CD", "COD", 86790568, "Africa"))
        countries.put("Denmark", Country("Denmark", "DK", "DNK", 5806081, "Europe"))
        countries.put("Djibouti", Country("Djibouti", "DJ", "DJI", 973557, "Africa"))
        countries.put("Dominica", Country("Dominica", "DM", "DMA", 71808, "America"))
        countries.put("Dominican_Republic", Country("Dominican_Republic", "DO", "DOM", 10738957, "America"))
        countries.put("Ecuador", Country("Ecuador", "EC", "ECU", 17373657, "America"))
        countries.put("Egypt", Country("Egypt", "EG", "EGY", 100388076, "Africa"))
        countries.put("El_Salvador", Country("El_Salvador", "SV", "SLV", 6453550, "America"))
        countries.put("Equatorial_Guinea", Country("Equatorial_Guinea", "GQ", "GNQ", 1355982, "Africa"))
        countries.put("Eritrea", Country("Eritrea", "ER", "ERI", 3497117, "Africa"))
        countries.put("Estonia", Country("Estonia", "EE", "EST", 1324820, "Europe"))
        countries.put("Eswatini", Country("Eswatini", "SZ", "SWZ", 1148133, "Africa"))
        countries.put("Ethiopia", Country("Ethiopia", "ET", "ETH", 112078727, "Africa"))
        countries.put("Falkland_Islands_(Malvinas)", Country("Falkland_Islands_(Malvinas)", "FK", "FLK", 3372, "America"))
        countries.put("Faroe_Islands", Country("Faroe_Islands", "FO", "FRO", 48677, "Europe"))
        countries.put("Fiji", Country("Fiji", "FJ", "FJI", 889955, "Oceania"))
        countries.put("Finland", Country("Finland", "FI", "FIN", 5517919, "Europe"))
        countries.put("France", Country("France", "FR", "FRA", 67012883, "Europe"))
        countries.put("French_Polynesia", Country("French_Polynesia", "PF", "PYF", 279285, "Oceania"))
        countries.put("Gabon", Country("Gabon", "GA", "GAB", 2172578, "Africa"))
        countries.put("Gambia", Country("Gambia", "GM", "GMB", 2347696, "Africa"))
        countries.put("Georgia", Country("Georgia", "GE", "GEO", 3996762, "Europe"))
        countries.put("Germany", Country("Germany", "DE", "DEU", 83019213, "Europe"))
        countries.put("Ghana", Country("Ghana", "GH", "GHA", 30417858, "Africa"))
        countries.put("Gibraltar", Country("Gibraltar", "GI", "GIB", 33706, "Europe"))
        countries.put("Greece", Country("Greece", "EL", "GRC", 10724599, "Europe"))
        countries.put("Greenland", Country("Greenland", "GL", "GRL", 56660, "America"))
        countries.put("Grenada", Country("Grenada", "GD", "GRD", 112002, "America"))
        countries.put("Guam", Country("Guam", "GU", "GUM", 167295, "Oceania"))
        countries.put("Guatemala", Country("Guatemala", "GT", "GTM", 17581476, "America"))
        countries.put("Guernsey", Country("Guernsey", "GG", "GGY", 64468, "Europe"))
        countries.put("Guinea", Country("Guinea", "GN", "GIN", 12771246, "Africa"))
        countries.put("Guinea_Bissau", Country("Guinea_Bissau", "GW", "GNB", 1920917, "Africa"))
        countries.put("Guyana", Country("Guyana", "GY", "GUY", 782775, "America"))
        countries.put("Haiti", Country("Haiti", "HT", "HTI", 11263079, "America"))
        countries.put("Holy_See", Country("Holy_See", "VA", "VAT", 815, "Europe"))
        countries.put("Honduras", Country("Honduras", "HN", "HND", 9746115, "America"))
        countries.put("Hungary", Country("Hungary", "HU", "HUN", 9772756, "Europe"))
        countries.put("Iceland", Country("Iceland", "IS", "ISL", 356991, "Europe"))
        countries.put("India", Country("India", "IN", "IND", 1366417756, "Asia"))
        countries.put("Indonesia", Country("Indonesia", "ID", "IDN", 270625567, "Asia"))
        countries.put("Iran", Country("Iran", "IR", "IRN", 82913893, "Asia"))
        countries.put("Iraq", Country("Iraq", "IQ", "IRQ", 39309789, "Asia"))
        countries.put("Ireland", Country("Ireland", "IE", "IRL", 4904240, "Europe"))
        countries.put("Isle_of_Man", Country("Isle_of_Man", "IM", "IMN", 84589, "Europe"))
        countries.put("Israel", Country("Israel", "IL", "ISR", 8519373, "Asia"))
        countries.put("Italy", Country("Italy", "IT", "ITA", 60359546, "Europe"))
        countries.put("Jamaica", Country("Jamaica", "JM", "JAM", 2948277, "America"))
        countries.put("Japan", Country("Japan", "JP", "JPN", 126860299, "Asia"))
        countries.put("Jersey", Country("Jersey", "JE", "JEY", 107796, "Europe"))
        countries.put("Jordan", Country("Jordan", "JO", "JOR", 10101697, "Asia"))
        countries.put("Kazakhstan", Country("Kazakhstan", "KZ", "KAZ", 18551428, "Asia"))
        countries.put("Kenya", Country("Kenya", "KE", "KEN", 52573967, "Africa"))
        countries.put("Kosovo", Country("Kosovo", "XK", "XKX", 1798506, "Europe"))
        countries.put("Kuwait", Country("Kuwait", "KW", "KWT", 4207077, "Asia"))
        countries.put("Kyrgyzstan", Country("Kyrgyzstan", "KG", "KGZ", 6415851, "Asia"))
        countries.put("Laos", Country("Laos", "LA", "LAO", 7169456, "Asia"))
        countries.put("Latvia", Country("Latvia", "LV", "LVA", 1919968, "Europe"))
        countries.put("Lebanon", Country("Lebanon", "LB", "LBN", 6855709, "Asia"))
        countries.put("Lesotho", Country("Lesotho", "LS", "LSO", 2125267, "Africa"))
        countries.put("Liberia", Country("Liberia", "LR", "LBR", 4937374, "Africa"))
        countries.put("Libya", Country("Libya", "LY", "LBY", 6777453, "Africa"))
        countries.put("Liechtenstein", Country("Liechtenstein", "LI", "LIE", 38378, "Europe"))
        countries.put("Lithuania", Country("Lithuania", "LT", "LTU", 2794184, "Europe"))
        countries.put("Luxembourg", Country("Luxembourg", "LU", "LUX", 613894, "Europe"))
        countries.put("Madagascar", Country("Madagascar", "MG", "MDG", 26969306, "Africa"))
        countries.put("Malawi", Country("Malawi", "MW", "MWI", 18628749, "Africa"))
        countries.put("Malaysia", Country("Malaysia", "MY", "MYS", 31949789, "Asia"))
        countries.put("Maldives", Country("Maldives", "MV", "MDV", 530957, "Asia"))
        countries.put("Mali", Country("Mali", "ML", "MLI", 19658023, "Africa"))
        countries.put("Malta", Country("Malta", "MT", "MLT", 493559, "Europe"))
        countries.put("Marshall_Islands", Country("Marshall_Islands", "MH", "MHL", 58791, "Oceania"))
        countries.put("Mauritania", Country("Mauritania", "MR", "MRT", 4525698, "Africa"))
        countries.put("Mauritius", Country("Mauritius", "MU", "MUS", 1269670, "Africa"))
        countries.put("Mexico", Country("Mexico", "MX", "MEX", 127575529, "America"))
        countries.put("Moldova", Country("Moldova", "MD", "MDA", 4043258, "Europe"))
        countries.put("Monaco", Country("Monaco", "MC", "MCO", 33085, "Europe"))
        countries.put("Mongolia", Country("Mongolia", "MN", "MNG", 3225166, "Asia"))
        countries.put("Montenegro", Country("Montenegro", "ME", "MNE", 622182, "Europe"))
        countries.put("Montserrat", Country("Montserrat", "MS", "MSF", 4991, "America"))
        countries.put("Morocco", Country("Morocco", "MA", "MAR", 36471766, "Africa"))
        countries.put("Mozambique", Country("Mozambique", "MZ", "MOZ", 30366043, "Africa"))
        countries.put("Myanmar", Country("Myanmar", "MM", "MMR", 54045422, "Asia"))
        countries.put("Namibia", Country("Namibia", "NA", "NAM", 2494524, "Africa"))
        countries.put("Nepal", Country("Nepal", "NP", "NPL", 28608715, "Asia"))
        countries.put("Netherlands", Country("Netherlands", "NL", "NLD", 17282163, "Europe"))
        countries.put("New_Caledonia", Country("New_Caledonia", "NC", "NCL", 282757, "Oceania"))
        countries.put("New_Zealand", Country("New_Zealand", "NZ", "NZL", 4783062, "Oceania"))
        countries.put("Nicaragua", Country("Nicaragua", "NI", "NIC", 6545503, "America"))
        countries.put("Niger", Country("Niger", "NE", "NER", 23310719, "Africa"))
        countries.put("Nigeria", Country("Nigeria", "NG", "NGA", 200963603, "Africa"))
        countries.put("North_Macedonia", Country("North_Macedonia", "MK", "MKD", 2077132, "Europe"))
        countries.put("Northern_Mariana_Islands", Country("Northern_Mariana_Islands", "MP", "MNP", 57213, "Oceania"))
        countries.put("Norway", Country("Norway", "NO", "NOR", 5328212, "Europe"))
        countries.put("Oman", Country("Oman", "OM", "OMN", 4974992, "Asia"))
        countries.put("Pakistan", Country("Pakistan", "PK", "PAK", 216565317, "Asia"))
        countries.put("Palestine", Country("Palestine", "PS", "PSE", 4981422, "Asia"))
        countries.put("Panama", Country("Panama", "PA", "PAN", 4246440, "America"))
        countries.put("Papua_New_Guinea", Country("Papua_New_Guinea", "PG", "PNG", 8776119, "Oceania"))
        countries.put("Paraguay", Country("Paraguay", "PY", "PRY", 7044639, "America"))
        countries.put("Peru", Country("Peru", "PE", "PER", 32510462, "America"))
        countries.put("Philippines", Country("Philippines", "PH", "PHL", 108116622, "Asia"))
        countries.put("Poland", Country("Poland", "PL", "POL", 37972812, "Europe"))
        countries.put("Portugal", Country("Portugal", "PT", "PRT", 10276617, "Europe"))
        countries.put("Puerto_Rico", Country("Puerto_Rico", "PR", "PRI", 2933404, "America"))
        countries.put("Qatar", Country("Qatar", "QA", "QAT", 2832071, "Asia"))
        countries.put("Romania", Country("Romania", "RO", "ROU", 19414458, "Europe"))
        countries.put("Russia", Country("Russia", "RU", "RUS", 145872260, "Europe"))
        countries.put("Rwanda", Country("Rwanda", "RW", "RWA", 12626938, "Africa"))
        countries.put("Saint_Kitts_and_Nevis", Country("Saint_Kitts_and_Nevis", "KN", "KNA", 52834, "America"))
        countries.put("Saint_Lucia", Country("Saint_Lucia", "LC", "LCA", 182795, "America"))
        countries.put("Saint_Vincent_and_the_Grenadines", Country("Saint_Vincent_and_the_Grenadines", "VC", "VCT", 110593, "America"))
        countries.put("San_Marino", Country("San_Marino", "SM", "SMR", 34453, "Europe"))
        countries.put("Sao_Tome_and_Principe", Country("Sao_Tome_and_Principe", "ST", "STP", 215048, "Africa"))
        countries.put("Saudi_Arabia", Country("Saudi_Arabia", "SA", "SAU", 34268529, "Asia"))
        countries.put("Senegal", Country("Senegal", "SN", "SEN", 16296362, "Africa"))
        countries.put("Serbia", Country("Serbia", "RS", "SRB", 6963764, "Europe"))
        countries.put("Seychelles", Country("Seychelles", "SC", "SYC", 97741, "Africa"))
        countries.put("Sierra_Leone", Country("Sierra_Leone", "SL", "SLE", 7813207, "Africa"))
        countries.put("Singapore", Country("Singapore", "SG", "SGP", 5804343, "Asia"))
        countries.put("Sint_Maarten", Country("Sint_Maarten", "SX", "SXM", 42389, "America"))
        countries.put("Slovakia", Country("Slovakia", "SK", "SVK", 5450421, "Europe"))
        countries.put("Slovenia", Country("Slovenia", "SI", "SVN", 2080908, "Europe"))
        countries.put("Solomon_Islands", Country("Solomon_Islands", "SB", "SLB", 669821, "Oceania"))
        countries.put("Somalia", Country("Somalia", "SO", "SOM", 15442906, "Africa"))
        countries.put("South_Africa", Country("South_Africa", "ZA", "ZAF", 58558267, "Africa"))
        countries.put("South_Korea", Country("South_Korea", "KR", "KOR", 51225321, "Asia"))
        countries.put("South_Sudan", Country("South_Sudan", "SS", "SSD", 11062114, "Africa"))
        countries.put("Spain", Country("Spain", "ES", "ESP", 46937060, "Europe"))
        countries.put("Sri_Lanka", Country("Sri_Lanka", "LK", "LKA", 21323734, "Asia"))
        countries.put("Sudan", Country("Sudan", "SD", "SDN", 42813237, "Africa"))
        countries.put("Suriname", Country("Suriname", "SR", "SUR", 581363, "America"))
        countries.put("Sweden", Country("Sweden", "SE", "SWE", 10230185, "Europe"))
        countries.put("Switzerland", Country("Switzerland", "CH", "CHE", 8544527, "Europe"))
        countries.put("Syria", Country("Syria", "SY", "SYR", 17070132, "Asia"))
        countries.put("Taiwan", Country("Taiwan", "TW", "CNG1925", 23773881, "Asia"))
        countries.put("Tajikistan", Country("Tajikistan", "TJ", "TJK", 9321023, "Asia"))
        countries.put("Thailand", Country("Thailand", "TH", "THA", 69625581, "Asia"))
        countries.put("Timor_Leste", Country("Timor_Leste", "TL", "TLS", 1293120, "Asia"))
        countries.put("Togo", Country("Togo", "TG", "TGO", 8082359, "Africa"))
        countries.put("Trinidad_and_Tobago", Country("Trinidad_and_Tobago", "TT", "TTO", 1394969, "America"))
        countries.put("Tunisia", Country("Tunisia", "TN", "TUN", 11694721, "Africa"))
        countries.put("Turkey", Country("Turkey", "TR", "TUR", 82003882, "Europe"))
        countries.put("Turks_and_Caicos_islands", Country("Turks_and_Caicos_islands", "TC", "TCA", 38194, "America"))
        countries.put("Uganda", Country("Uganda", "UG", "UGA", 44269587, "Africa"))
        countries.put("Ukraine", Country("Ukraine", "UA", "UKR", 43993643, "Europe"))
        countries.put("United_Arab_Emirates", Country("United_Arab_Emirates", "AE", "ARE", 9770526, "Asia"))
        countries.put("United_Kingdom", Country("United_Kingdom", "UK", "GBR", 66647112, "Europe"))
        countries.put("United_Republic_of_Tanzania", Country("United_Republic_of_Tanzania", "TZ", "TZA", 58005461, "Africa"))
        countries.put("United_States_of_America", Country("United_States_of_America", "US", "USA", 329064917, "America"))
        countries.put("United_States_Virgin_Islands", Country("United_States_Virgin_Islands", "VI", "VIR", 104579, "America"))
        countries.put("Uruguay", Country("Uruguay", "UY", "URY", 3461731, "America"))
        countries.put("Uzbekistan", Country("Uzbekistan", "UZ", "UZB", 32981715, "Asia"))
        countries.put("Vanuatu", Country("Vanuatu", "VU", "VUT", 299882, "Oceania"))
        countries.put("Venezuela", Country("Venezuela", "VE", "VEN", 28515829, "America"))
        countries.put("Vietnam", Country("Vietnam", "VN", "VNM", 96462108, "Asia"))
        countries.put("Wallis_and_Futuna", Country("Wallis_and_Futuna", "WF", "0", 0, "Oceania"))
        countries.put("Western_Sahara", Country("Western_Sahara", "EH", "ESH", 582458, "Africa"))
        countries.put("Yemen", Country("Yemen", "YE", "YEM", 29161922, "Asia"))
        countries.put("Zambia", Country("Zambia", "ZM", "ZMB", 17861034, "Africa"))
        countries.put("Zimbabwe", Country("Zimbabwe", "ZW", "ZWE", 14645473, "Africa"))


        return countries
    }
}