package com.ant_waters.covidstatistics.model

import java.time.LocalDate
import java.util.*

class DataManager {
    companion object {
        lateinit var DateStart: Date
        lateinit var DateEnd: Date

        private var _dailyCovidsByDate = listOf<Pair<Date, MutableList<DailyCovid>>>()
        public val DailyCovidsByDate get() = this._dailyCovidsByDate

        private lateinit var _countryAggregates: CountryAggregates
        public val CountryAggregates get() = this._countryAggregates

        val PopulationScaler = 100000      // cases are usally reported per 100k of population
        val PopulationScalerLabel = "100k"
    }

    public fun LoadData(): Boolean {
        // TODO: Load data from CSV or Database

        try {
            _dailyCovidsByDate = LoadTestData().toList()
            _countryAggregates = CountryAggregates(DateStart, DateEnd, _dailyCovidsByDate)
            return true
        } catch (ex: Exception) {
            // TODO: Errorhandling or logging
            return false
        }
    }

    private fun LoadTestData(): MutableMap<Date, MutableList<DailyCovid>> {
        val countries = LoadCountries()
        val dailyCovidsByDate = mutableMapOf<Date, MutableList<DailyCovid>>()

        // TODO: The date iteration here is dodgy, but the recommended version won't work on my test phone (Android 6/api 23)
        DateStart = Date(2019, 12, 31)
        DateEnd = Date(2020, 12, 14)

        iterateBetweenDatesJava7(DateStart, DateEnd,
            fun(d: Date) {
                val daylies = mutableListOf<DailyCovid>()
                for (c in countries) {
                    daylies.add(DailyCovid(c, d, 111, 222))
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

    private fun LoadCountries(): MutableList<Country> {
        val countries = mutableListOf<Country>()
        countries.add(Country("Afghanistan", "AF", "AFG", 38041757, "Asia"))
        countries.add(Country("Albania", "AL", "ALB", 2862427, "Europe"))
        countries.add(Country("Algeria", "DZ", "DZA", 43053054, "Africa"))
        countries.add(Country("Andorra", "AD", "AND", 76177, "Europe"))
        countries.add(Country("Angola", "AO", "AGO", 31825299, "Africa"))
        countries.add(Country("Anguilla", "AI", "AIA", 14872, "America"))
        countries.add(Country("Antigua_and_Barbuda", "AG", "ATG", 97115, "America"))
        countries.add(Country("Argentina", "AR", "ARG", 44780675, "America"))
        countries.add(Country("Armenia", "AM", "ARM", 2957728, "Europe"))
        countries.add(Country("Aruba", "AW", "ABW", 106310, "America"))
        countries.add(Country("Australia", "AU", "AUS", 25203200, "Oceania"))
        countries.add(Country("Austria", "AT", "AUT", 8858775, "Europe"))
        countries.add(Country("Azerbaijan", "AZ", "AZE", 10047719, "Europe"))
        countries.add(Country("Bahamas", "BS", "BHS", 389486, "America"))
        countries.add(Country("Bahrain", "BH", "BHR", 1641164, "Asia"))
        countries.add(Country("Bangladesh", "BD", "BGD", 163046173, "Asia"))
        countries.add(Country("Barbados", "BB", "BRB", 287021, "America"))
        countries.add(Country("Belarus", "BY", "BLR", 9452409, "Europe"))
        countries.add(Country("Belgium", "BE", "BEL", 11455519, "Europe"))
        countries.add(Country("Belize", "BZ", "BLZ", 390351, "America"))
        countries.add(Country("Benin", "BJ", "BEN", 11801151, "Africa"))
        countries.add(Country("Bermuda", "BM", "BMU", 62508, "America"))
        countries.add(Country("Bhutan", "BT", "BTN", 763094, "Asia"))
        countries.add(Country("Bolivia", "BO", "BOL", 11513102, "America"))
        countries.add(Country("Bonaire, Saint Eustatius and Saba", "BQ", "BES", 25983, "America"))
        countries.add(Country("Bosnia_and_Herzegovina", "BA", "BIH", 3300998, "Europe"))
        countries.add(Country("Botswana", "BW", "BWA", 2303703, "Africa"))
        countries.add(Country("Brazil", "BR", "BRA", 211049519, "America"))
        countries.add(Country("British_Virgin_Islands", "VG", "VGB", 30033, "America"))
        countries.add(Country("Brunei_Darussalam", "BN", "BRN", 433296, "Asia"))
        countries.add(Country("Bulgaria", "BG", "BGR", 7000039, "Europe"))
        countries.add(Country("Burkina_Faso", "BF", "BFA", 20321383, "Africa"))
        countries.add(Country("Burundi", "BI", "BDI", 11530577, "Africa"))
        countries.add(Country("Cambodia", "KH", "KHM", 16486542, "Asia"))
        countries.add(Country("Cameroon", "CM", "CMR", 25876387, "Africa"))
        countries.add(Country("Canada", "CA", "CAN", 37411038, "America"))
        countries.add(Country("Cape_Verde", "CV", "CPV", 549936, "Africa"))
        countries.add(
            Country(
                "Cases_on_an_international_conveyance_Japan",
                "JPG11668",
                "0",
                0,
                "Other"
            )
        )
        countries.add(Country("Cayman_Islands", "KY", "CYM", 64948, "America"))
        countries.add(Country("Central_African_Republic", "CF", "CAF", 4745179, "Africa"))
        countries.add(Country("Chad", "TD", "TCD", 15946882, "Africa"))
        countries.add(Country("Chile", "CL", "CHL", 18952035, "America"))
        countries.add(Country("China", "CN", "CHN", 1433783692, "Asia"))
        countries.add(Country("Colombia", "CO", "COL", 50339443, "America"))
        countries.add(Country("Comoros", "KM", "COM", 850891, "Africa"))
        countries.add(Country("Congo", "CG", "COG", 5380504, "Africa"))
        countries.add(Country("Costa_Rica", "CR", "CRI", 5047561, "America"))
        countries.add(Country("Cote_dIvoire", "CI", "CIV", 25716554, "Africa"))
        countries.add(Country("Croatia", "HR", "HRV", 4076246, "Europe"))
        countries.add(Country("Cuba", "CU", "CUB", 11333484, "America"))
        countries.add(Country("CuraÃ§ao", "CW", "CUW", 163423, "America"))
        countries.add(Country("Cyprus", "CY", "CYP", 875899, "Europe"))
        countries.add(Country("Czechia", "CZ", "CZE", 10649800, "Europe"))
        countries.add(Country("Democratic_Republic_of_the_Congo", "CD", "COD", 86790568, "Africa"))
        countries.add(Country("Denmark", "DK", "DNK", 5806081, "Europe"))
        countries.add(Country("Djibouti", "DJ", "DJI", 973557, "Africa"))
        countries.add(Country("Dominica", "DM", "DMA", 71808, "America"))
        countries.add(Country("Dominican_Republic", "DO", "DOM", 10738957, "America"))
        countries.add(Country("Ecuador", "EC", "ECU", 17373657, "America"))
        countries.add(Country("Egypt", "EG", "EGY", 100388076, "Africa"))
        countries.add(Country("El_Salvador", "SV", "SLV", 6453550, "America"))
        countries.add(Country("Equatorial_Guinea", "GQ", "GNQ", 1355982, "Africa"))
        countries.add(Country("Eritrea", "ER", "ERI", 3497117, "Africa"))
        countries.add(Country("Estonia", "EE", "EST", 1324820, "Europe"))
        countries.add(Country("Eswatini", "SZ", "SWZ", 1148133, "Africa"))
        countries.add(Country("Ethiopia", "ET", "ETH", 112078727, "Africa"))
        countries.add(Country("Falkland_Islands_(Malvinas)", "FK", "FLK", 3372, "America"))
        countries.add(Country("Faroe_Islands", "FO", "FRO", 48677, "Europe"))
        countries.add(Country("Fiji", "FJ", "FJI", 889955, "Oceania"))
        countries.add(Country("Finland", "FI", "FIN", 5517919, "Europe"))
        countries.add(Country("France", "FR", "FRA", 67012883, "Europe"))
        countries.add(Country("French_Polynesia", "PF", "PYF", 279285, "Oceania"))
        countries.add(Country("Gabon", "GA", "GAB", 2172578, "Africa"))
        countries.add(Country("Gambia", "GM", "GMB", 2347696, "Africa"))
        countries.add(Country("Georgia", "GE", "GEO", 3996762, "Europe"))
        countries.add(Country("Germany", "DE", "DEU", 83019213, "Europe"))
        countries.add(Country("Ghana", "GH", "GHA", 30417858, "Africa"))
        countries.add(Country("Gibraltar", "GI", "GIB", 33706, "Europe"))
        countries.add(Country("Greece", "EL", "GRC", 10724599, "Europe"))
        countries.add(Country("Greenland", "GL", "GRL", 56660, "America"))
        countries.add(Country("Grenada", "GD", "GRD", 112002, "America"))
        countries.add(Country("Guam", "GU", "GUM", 167295, "Oceania"))
        countries.add(Country("Guatemala", "GT", "GTM", 17581476, "America"))
        countries.add(Country("Guernsey", "GG", "GGY", 64468, "Europe"))
        countries.add(Country("Guinea", "GN", "GIN", 12771246, "Africa"))
        countries.add(Country("Guinea_Bissau", "GW", "GNB", 1920917, "Africa"))
        countries.add(Country("Guyana", "GY", "GUY", 782775, "America"))
        countries.add(Country("Haiti", "HT", "HTI", 11263079, "America"))
        countries.add(Country("Holy_See", "VA", "VAT", 815, "Europe"))
        countries.add(Country("Honduras", "HN", "HND", 9746115, "America"))
        countries.add(Country("Hungary", "HU", "HUN", 9772756, "Europe"))
        countries.add(Country("Iceland", "IS", "ISL", 356991, "Europe"))
        countries.add(Country("India", "IN", "IND", 1366417756, "Asia"))
        countries.add(Country("Indonesia", "ID", "IDN", 270625567, "Asia"))
        countries.add(Country("Iran", "IR", "IRN", 82913893, "Asia"))
        countries.add(Country("Iraq", "IQ", "IRQ", 39309789, "Asia"))
        countries.add(Country("Ireland", "IE", "IRL", 4904240, "Europe"))
        countries.add(Country("Isle_of_Man", "IM", "IMN", 84589, "Europe"))
        countries.add(Country("Israel", "IL", "ISR", 8519373, "Asia"))
        countries.add(Country("Italy", "IT", "ITA", 60359546, "Europe"))
        countries.add(Country("Jamaica", "JM", "JAM", 2948277, "America"))
        countries.add(Country("Japan", "JP", "JPN", 126860299, "Asia"))
        countries.add(Country("Jersey", "JE", "JEY", 107796, "Europe"))
        countries.add(Country("Jordan", "JO", "JOR", 10101697, "Asia"))
        countries.add(Country("Kazakhstan", "KZ", "KAZ", 18551428, "Asia"))
        countries.add(Country("Kenya", "KE", "KEN", 52573967, "Africa"))
        countries.add(Country("Kosovo", "XK", "XKX", 1798506, "Europe"))
        countries.add(Country("Kuwait", "KW", "KWT", 4207077, "Asia"))
        countries.add(Country("Kyrgyzstan", "KG", "KGZ", 6415851, "Asia"))
        countries.add(Country("Laos", "LA", "LAO", 7169456, "Asia"))
        countries.add(Country("Latvia", "LV", "LVA", 1919968, "Europe"))
        countries.add(Country("Lebanon", "LB", "LBN", 6855709, "Asia"))
        countries.add(Country("Lesotho", "LS", "LSO", 2125267, "Africa"))
        countries.add(Country("Liberia", "LR", "LBR", 4937374, "Africa"))
        countries.add(Country("Libya", "LY", "LBY", 6777453, "Africa"))
        countries.add(Country("Liechtenstein", "LI", "LIE", 38378, "Europe"))
        countries.add(Country("Lithuania", "LT", "LTU", 2794184, "Europe"))
        countries.add(Country("Luxembourg", "LU", "LUX", 613894, "Europe"))
        countries.add(Country("Madagascar", "MG", "MDG", 26969306, "Africa"))
        countries.add(Country("Malawi", "MW", "MWI", 18628749, "Africa"))
        countries.add(Country("Malaysia", "MY", "MYS", 31949789, "Asia"))
        countries.add(Country("Maldives", "MV", "MDV", 530957, "Asia"))
        countries.add(Country("Mali", "ML", "MLI", 19658023, "Africa"))
        countries.add(Country("Malta", "MT", "MLT", 493559, "Europe"))
        countries.add(Country("Marshall_Islands", "MH", "MHL", 58791, "Oceania"))
        countries.add(Country("Mauritania", "MR", "MRT", 4525698, "Africa"))
        countries.add(Country("Mauritius", "MU", "MUS", 1269670, "Africa"))
        countries.add(Country("Mexico", "MX", "MEX", 127575529, "America"))
        countries.add(Country("Moldova", "MD", "MDA", 4043258, "Europe"))
        countries.add(Country("Monaco", "MC", "MCO", 33085, "Europe"))
        countries.add(Country("Mongolia", "MN", "MNG", 3225166, "Asia"))
        countries.add(Country("Montenegro", "ME", "MNE", 622182, "Europe"))
        countries.add(Country("Montserrat", "MS", "MSF", 4991, "America"))
        countries.add(Country("Morocco", "MA", "MAR", 36471766, "Africa"))
        countries.add(Country("Mozambique", "MZ", "MOZ", 30366043, "Africa"))
        countries.add(Country("Myanmar", "MM", "MMR", 54045422, "Asia"))
        countries.add(Country("Namibia", "NA", "NAM", 2494524, "Africa"))
        countries.add(Country("Nepal", "NP", "NPL", 28608715, "Asia"))
        countries.add(Country("Netherlands", "NL", "NLD", 17282163, "Europe"))
        countries.add(Country("New_Caledonia", "NC", "NCL", 282757, "Oceania"))
        countries.add(Country("New_Zealand", "NZ", "NZL", 4783062, "Oceania"))
        countries.add(Country("Nicaragua", "NI", "NIC", 6545503, "America"))
        countries.add(Country("Niger", "NE", "NER", 23310719, "Africa"))
        countries.add(Country("Nigeria", "NG", "NGA", 200963603, "Africa"))
        countries.add(Country("North_Macedonia", "MK", "MKD", 2077132, "Europe"))
        countries.add(Country("Northern_Mariana_Islands", "MP", "MNP", 57213, "Oceania"))
        countries.add(Country("Norway", "NO", "NOR", 5328212, "Europe"))
        countries.add(Country("Oman", "OM", "OMN", 4974992, "Asia"))
        countries.add(Country("Pakistan", "PK", "PAK", 216565317, "Asia"))
        countries.add(Country("Palestine", "PS", "PSE", 4981422, "Asia"))
        countries.add(Country("Panama", "PA", "PAN", 4246440, "America"))
        countries.add(Country("Papua_New_Guinea", "PG", "PNG", 8776119, "Oceania"))
        countries.add(Country("Paraguay", "PY", "PRY", 7044639, "America"))
        countries.add(Country("Peru", "PE", "PER", 32510462, "America"))
        countries.add(Country("Philippines", "PH", "PHL", 108116622, "Asia"))
        countries.add(Country("Poland", "PL", "POL", 37972812, "Europe"))
        countries.add(Country("Portugal", "PT", "PRT", 10276617, "Europe"))
        countries.add(Country("Puerto_Rico", "PR", "PRI", 2933404, "America"))
        countries.add(Country("Qatar", "QA", "QAT", 2832071, "Asia"))
        countries.add(Country("Romania", "RO", "ROU", 19414458, "Europe"))
        countries.add(Country("Russia", "RU", "RUS", 145872260, "Europe"))
        countries.add(Country("Rwanda", "RW", "RWA", 12626938, "Africa"))
        countries.add(Country("Saint_Kitts_and_Nevis", "KN", "KNA", 52834, "America"))
        countries.add(Country("Saint_Lucia", "LC", "LCA", 182795, "America"))
        countries.add(Country("Saint_Vincent_and_the_Grenadines", "VC", "VCT", 110593, "America"))
        countries.add(Country("San_Marino", "SM", "SMR", 34453, "Europe"))
        countries.add(Country("Sao_Tome_and_Principe", "ST", "STP", 215048, "Africa"))
        countries.add(Country("Saudi_Arabia", "SA", "SAU", 34268529, "Asia"))
        countries.add(Country("Senegal", "SN", "SEN", 16296362, "Africa"))
        countries.add(Country("Serbia", "RS", "SRB", 6963764, "Europe"))
        countries.add(Country("Seychelles", "SC", "SYC", 97741, "Africa"))
        countries.add(Country("Sierra_Leone", "SL", "SLE", 7813207, "Africa"))
        countries.add(Country("Singapore", "SG", "SGP", 5804343, "Asia"))
        countries.add(Country("Sint_Maarten", "SX", "SXM", 42389, "America"))
        countries.add(Country("Slovakia", "SK", "SVK", 5450421, "Europe"))
        countries.add(Country("Slovenia", "SI", "SVN", 2080908, "Europe"))
        countries.add(Country("Solomon_Islands", "SB", "SLB", 669821, "Oceania"))
        countries.add(Country("Somalia", "SO", "SOM", 15442906, "Africa"))
        countries.add(Country("South_Africa", "ZA", "ZAF", 58558267, "Africa"))
        countries.add(Country("South_Korea", "KR", "KOR", 51225321, "Asia"))
        countries.add(Country("South_Sudan", "SS", "SSD", 11062114, "Africa"))
        countries.add(Country("Spain", "ES", "ESP", 46937060, "Europe"))
        countries.add(Country("Sri_Lanka", "LK", "LKA", 21323734, "Asia"))
        countries.add(Country("Sudan", "SD", "SDN", 42813237, "Africa"))
        countries.add(Country("Suriname", "SR", "SUR", 581363, "America"))
        countries.add(Country("Sweden", "SE", "SWE", 10230185, "Europe"))
        countries.add(Country("Switzerland", "CH", "CHE", 8544527, "Europe"))
        countries.add(Country("Syria", "SY", "SYR", 17070132, "Asia"))
        countries.add(Country("Taiwan", "TW", "CNG1925", 23773881, "Asia"))
        countries.add(Country("Tajikistan", "TJ", "TJK", 9321023, "Asia"))
        countries.add(Country("Thailand", "TH", "THA", 69625581, "Asia"))
        countries.add(Country("Timor_Leste", "TL", "TLS", 1293120, "Asia"))
        countries.add(Country("Togo", "TG", "TGO", 8082359, "Africa"))
        countries.add(Country("Trinidad_and_Tobago", "TT", "TTO", 1394969, "America"))
        countries.add(Country("Tunisia", "TN", "TUN", 11694721, "Africa"))
        countries.add(Country("Turkey", "TR", "TUR", 82003882, "Europe"))
        countries.add(Country("Turks_and_Caicos_islands", "TC", "TCA", 38194, "America"))
        countries.add(Country("Uganda", "UG", "UGA", 44269587, "Africa"))
        countries.add(Country("Ukraine", "UA", "UKR", 43993643, "Europe"))
        countries.add(Country("United_Arab_Emirates", "AE", "ARE", 9770526, "Asia"))
        countries.add(Country("United_Kingdom", "UK", "GBR", 66647112, "Europe"))
        countries.add(Country("United_Republic_of_Tanzania", "TZ", "TZA", 58005461, "Africa"))
        countries.add(Country("United_States_of_America", "US", "USA", 329064917, "America"))
        countries.add(Country("United_States_Virgin_Islands", "VI", "VIR", 104579, "America"))
        countries.add(Country("Uruguay", "UY", "URY", 3461731, "America"))
        countries.add(Country("Uzbekistan", "UZ", "UZB", 32981715, "Asia"))
        countries.add(Country("Vanuatu", "VU", "VUT", 299882, "Oceania"))
        countries.add(Country("Venezuela", "VE", "VEN", 28515829, "America"))
        countries.add(Country("Vietnam", "VN", "VNM", 96462108, "Asia"))
        countries.add(Country("Wallis_and_Futuna", "WF", "0", 0, "Oceania"))
        countries.add(Country("Western_Sahara", "EH", "ESH", 582458, "Africa"))
        countries.add(Country("Yemen", "YE", "YEM", 29161922, "Asia"))
        countries.add(Country("Zambia", "ZM", "ZMB", 17861034, "Africa"))
        countries.add(Country("Zimbabwe", "ZW", "ZWE", 14645473, "Africa"))

        return countries
    }
}