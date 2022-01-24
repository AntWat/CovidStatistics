package com.ant_waters.covidstatistics.model

import java.util.*

/**
 * Hard coded test data, not used anymore
  */
class HardcodedTestData {
}

public fun LoadTestData(countries: MutableMap</*name*/String, Country2>):
        MutableMap<Date, MutableList<DailyCovid>> {
    val dailyCovidsByDate = mutableMapOf<Date, MutableList<DailyCovid>>()

    // TODO: The date iteration here is dodgy, but the recommended version won't work on my test phone (Android 6/api 23)
    DataManager.DateStart = Date(2019, 12, 31)
    DataManager.DateEnd = Date(2020, 12, 14)

    val casesPer100k = 10
    val deathsPer100k = 1

    iterateBetweenDatesJava7(
        DataManager.DateStart, DataManager.DateEnd,
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


public fun LoadCountries_HardCoded(): MutableMap</*name*/String, Country2> {
    val countries = mutableMapOf</*name*/String, Country2>()
    countries.put("Afghanistan", Country2("Afghanistan", "AF", "AFG", 38041757, "Asia"))
    countries.put("Albania", Country2("Albania", "AL", "ALB", 2862427, "Europe"))
    countries.put("Algeria", Country2("Algeria", "DZ", "DZA", 43053054, "Africa"))
    countries.put("Andorra", Country2("Andorra", "AD", "AND", 76177, "Europe"))
    countries.put("Angola", Country2("Angola", "AO", "AGO", 31825299, "Africa"))
    countries.put("Anguilla", Country2("Anguilla", "AI", "AIA", 14872, "America"))
    countries.put("Antigua_and_Barbuda", Country2("Antigua_and_Barbuda", "AG", "ATG", 97115, "America"))
    countries.put("Argentina", Country2("Argentina", "AR", "ARG", 44780675, "America"))
    countries.put("Armenia", Country2("Armenia", "AM", "ARM", 2957728, "Europe"))
    countries.put("Aruba", Country2("Aruba", "AW", "ABW", 106310, "America"))
    countries.put("Australia", Country2("Australia", "AU", "AUS", 25203200, "Oceania"))
    countries.put("Austria", Country2("Austria", "AT", "AUT", 8858775, "Europe"))
    countries.put("Azerbaijan", Country2("Azerbaijan", "AZ", "AZE", 10047719, "Europe"))
    countries.put("Bahamas", Country2("Bahamas", "BS", "BHS", 389486, "America"))
    countries.put("Bahrain", Country2("Bahrain", "BH", "BHR", 1641164, "Asia"))
    countries.put("Bangladesh", Country2("Bangladesh", "BD", "BGD", 163046173, "Asia"))
    countries.put("Barbados", Country2("Barbados", "BB", "BRB", 287021, "America"))
    countries.put("Belarus", Country2("Belarus", "BY", "BLR", 9452409, "Europe"))
    countries.put("Belgium", Country2("Belgium", "BE", "BEL", 11455519, "Europe"))
    countries.put("Belize", Country2("Belize", "BZ", "BLZ", 390351, "America"))
    countries.put("Benin", Country2("Benin", "BJ", "BEN", 11801151, "Africa"))
    countries.put("Bermuda", Country2("Bermuda", "BM", "BMU", 62508, "America"))
    countries.put("Bhutan", Country2("Bhutan", "BT", "BTN", 763094, "Asia"))
    countries.put("Bolivia", Country2("Bolivia", "BO", "BOL", 11513102, "America"))
    countries.put("Bonaire_Saint Eustatius and Saba", Country2("Bonaire_Saint Eustatius and Saba", "BQ", "BES", 25983, "America"))
    countries.put("Bosnia_and_Herzegovina", Country2("Bosnia_and_Herzegovina", "BA", "BIH", 3300998, "Europe"))
    countries.put("Botswana", Country2("Botswana", "BW", "BWA", 2303703, "Africa"))
    countries.put("Brazil", Country2("Brazil", "BR", "BRA", 211049519, "America"))
    countries.put("British_Virgin_Islands", Country2("British_Virgin_Islands", "VG", "VGB", 30033, "America"))
    countries.put("Brunei_Darussalam", Country2("Brunei_Darussalam", "BN", "BRN", 433296, "Asia"))
    countries.put("Bulgaria", Country2("Bulgaria", "BG", "BGR", 7000039, "Europe"))
    countries.put("Burkina_Faso", Country2("Burkina_Faso", "BF", "BFA", 20321383, "Africa"))
    countries.put("Burundi", Country2("Burundi", "BI", "BDI", 11530577, "Africa"))
    countries.put("Cambodia", Country2("Cambodia", "KH", "KHM", 16486542, "Asia"))
    countries.put("Cameroon", Country2("Cameroon", "CM", "CMR", 25876387, "Africa"))
    countries.put("Canada", Country2("Canada", "CA", "CAN", 37411038, "America"))
    countries.put("Cape_Verde", Country2("Cape_Verde", "CV", "CPV", 549936, "Africa"))
    countries.put("Cases_on_an_international_conveyance_Japan", Country2("Cases_on_an_international_conveyance_Japan","JPG11668","0",0,"Other")        )
    countries.put("Cayman_Islands", Country2("Cayman_Islands", "KY", "CYM", 64948, "America"))
    countries.put("Central_African_Republic", Country2("Central_African_Republic", "CF", "CAF", 4745179, "Africa"))
    countries.put("Chad", Country2("Chad", "TD", "TCD", 15946882, "Africa"))
    countries.put("Chile", Country2("Chile", "CL", "CHL", 18952035, "America"))
    countries.put("China", Country2("China", "CN", "CHN", 1433783692, "Asia"))
    countries.put("Colombia", Country2("Colombia", "CO", "COL", 50339443, "America"))
    countries.put("Comoros", Country2("Comoros", "KM", "COM", 850891, "Africa"))
    countries.put("Congo", Country2("Congo", "CG", "COG", 5380504, "Africa"))
    countries.put("Costa_Rica", Country2("Costa_Rica", "CR", "CRI", 5047561, "America"))
    countries.put("Cote_dIvoire", Country2("Cote_dIvoire", "CI", "CIV", 25716554, "Africa"))
    countries.put("Croatia", Country2("Croatia", "HR", "HRV", 4076246, "Europe"))
    countries.put("Cuba", Country2("Cuba", "CU", "CUB", 11333484, "America"))
    countries.put("CuraÃ§ao", Country2("CuraÃ§ao", "CW", "CUW", 163423, "America"))
    countries.put("Cyprus", Country2("Cyprus", "CY", "CYP", 875899, "Europe"))
    countries.put("Czechia", Country2("Czechia", "CZ", "CZE", 10649800, "Europe"))
    countries.put("Democratic_Republic_of_the_Congo", Country2("Democratic_Republic_of_the_Congo", "CD", "COD", 86790568, "Africa"))
    countries.put("Denmark", Country2("Denmark", "DK", "DNK", 5806081, "Europe"))
    countries.put("Djibouti", Country2("Djibouti", "DJ", "DJI", 973557, "Africa"))
    countries.put("Dominica", Country2("Dominica", "DM", "DMA", 71808, "America"))
    countries.put("Dominican_Republic", Country2("Dominican_Republic", "DO", "DOM", 10738957, "America"))
    countries.put("Ecuador", Country2("Ecuador", "EC", "ECU", 17373657, "America"))
    countries.put("Egypt", Country2("Egypt", "EG", "EGY", 100388076, "Africa"))
    countries.put("El_Salvador", Country2("El_Salvador", "SV", "SLV", 6453550, "America"))
    countries.put("Equatorial_Guinea", Country2("Equatorial_Guinea", "GQ", "GNQ", 1355982, "Africa"))
    countries.put("Eritrea", Country2("Eritrea", "ER", "ERI", 3497117, "Africa"))
    countries.put("Estonia", Country2("Estonia", "EE", "EST", 1324820, "Europe"))
    countries.put("Eswatini", Country2("Eswatini", "SZ", "SWZ", 1148133, "Africa"))
    countries.put("Ethiopia", Country2("Ethiopia", "ET", "ETH", 112078727, "Africa"))
    countries.put("Falkland_Islands_(Malvinas)", Country2("Falkland_Islands_(Malvinas)", "FK", "FLK", 3372, "America"))
    countries.put("Faroe_Islands", Country2("Faroe_Islands", "FO", "FRO", 48677, "Europe"))
    countries.put("Fiji", Country2("Fiji", "FJ", "FJI", 889955, "Oceania"))
    countries.put("Finland", Country2("Finland", "FI", "FIN", 5517919, "Europe"))
    countries.put("France", Country2("France", "FR", "FRA", 67012883, "Europe"))
    countries.put("French_Polynesia", Country2("French_Polynesia", "PF", "PYF", 279285, "Oceania"))
    countries.put("Gabon", Country2("Gabon", "GA", "GAB", 2172578, "Africa"))
    countries.put("Gambia", Country2("Gambia", "GM", "GMB", 2347696, "Africa"))
    countries.put("Georgia", Country2("Georgia", "GE", "GEO", 3996762, "Europe"))
    countries.put("Germany", Country2("Germany", "DE", "DEU", 83019213, "Europe"))
    countries.put("Ghana", Country2("Ghana", "GH", "GHA", 30417858, "Africa"))
    countries.put("Gibraltar", Country2("Gibraltar", "GI", "GIB", 33706, "Europe"))
    countries.put("Greece", Country2("Greece", "EL", "GRC", 10724599, "Europe"))
    countries.put("Greenland", Country2("Greenland", "GL", "GRL", 56660, "America"))
    countries.put("Grenada", Country2("Grenada", "GD", "GRD", 112002, "America"))
    countries.put("Guam", Country2("Guam", "GU", "GUM", 167295, "Oceania"))
    countries.put("Guatemala", Country2("Guatemala", "GT", "GTM", 17581476, "America"))
    countries.put("Guernsey", Country2("Guernsey", "GG", "GGY", 64468, "Europe"))
    countries.put("Guinea", Country2("Guinea", "GN", "GIN", 12771246, "Africa"))
    countries.put("Guinea_Bissau", Country2("Guinea_Bissau", "GW", "GNB", 1920917, "Africa"))
    countries.put("Guyana", Country2("Guyana", "GY", "GUY", 782775, "America"))
    countries.put("Haiti", Country2("Haiti", "HT", "HTI", 11263079, "America"))
    countries.put("Holy_See", Country2("Holy_See", "VA", "VAT", 815, "Europe"))
    countries.put("Honduras", Country2("Honduras", "HN", "HND", 9746115, "America"))
    countries.put("Hungary", Country2("Hungary", "HU", "HUN", 9772756, "Europe"))
    countries.put("Iceland", Country2("Iceland", "IS", "ISL", 356991, "Europe"))
    countries.put("India", Country2("India", "IN", "IND", 1366417756, "Asia"))
    countries.put("Indonesia", Country2("Indonesia", "ID", "IDN", 270625567, "Asia"))
    countries.put("Iran", Country2("Iran", "IR", "IRN", 82913893, "Asia"))
    countries.put("Iraq", Country2("Iraq", "IQ", "IRQ", 39309789, "Asia"))
    countries.put("Ireland", Country2("Ireland", "IE", "IRL", 4904240, "Europe"))
    countries.put("Isle_of_Man", Country2("Isle_of_Man", "IM", "IMN", 84589, "Europe"))
    countries.put("Israel", Country2("Israel", "IL", "ISR", 8519373, "Asia"))
    countries.put("Italy", Country2("Italy", "IT", "ITA", 60359546, "Europe"))
    countries.put("Jamaica", Country2("Jamaica", "JM", "JAM", 2948277, "America"))
    countries.put("Japan", Country2("Japan", "JP", "JPN", 126860299, "Asia"))
    countries.put("Jersey", Country2("Jersey", "JE", "JEY", 107796, "Europe"))
    countries.put("Jordan", Country2("Jordan", "JO", "JOR", 10101697, "Asia"))
    countries.put("Kazakhstan", Country2("Kazakhstan", "KZ", "KAZ", 18551428, "Asia"))
    countries.put("Kenya", Country2("Kenya", "KE", "KEN", 52573967, "Africa"))
    countries.put("Kosovo", Country2("Kosovo", "XK", "XKX", 1798506, "Europe"))
    countries.put("Kuwait", Country2("Kuwait", "KW", "KWT", 4207077, "Asia"))
    countries.put("Kyrgyzstan", Country2("Kyrgyzstan", "KG", "KGZ", 6415851, "Asia"))
    countries.put("Laos", Country2("Laos", "LA", "LAO", 7169456, "Asia"))
    countries.put("Latvia", Country2("Latvia", "LV", "LVA", 1919968, "Europe"))
    countries.put("Lebanon", Country2("Lebanon", "LB", "LBN", 6855709, "Asia"))
    countries.put("Lesotho", Country2("Lesotho", "LS", "LSO", 2125267, "Africa"))
    countries.put("Liberia", Country2("Liberia", "LR", "LBR", 4937374, "Africa"))
    countries.put("Libya", Country2("Libya", "LY", "LBY", 6777453, "Africa"))
    countries.put("Liechtenstein", Country2("Liechtenstein", "LI", "LIE", 38378, "Europe"))
    countries.put("Lithuania", Country2("Lithuania", "LT", "LTU", 2794184, "Europe"))
    countries.put("Luxembourg", Country2("Luxembourg", "LU", "LUX", 613894, "Europe"))
    countries.put("Madagascar", Country2("Madagascar", "MG", "MDG", 26969306, "Africa"))
    countries.put("Malawi", Country2("Malawi", "MW", "MWI", 18628749, "Africa"))
    countries.put("Malaysia", Country2("Malaysia", "MY", "MYS", 31949789, "Asia"))
    countries.put("Maldives", Country2("Maldives", "MV", "MDV", 530957, "Asia"))
    countries.put("Mali", Country2("Mali", "ML", "MLI", 19658023, "Africa"))
    countries.put("Malta", Country2("Malta", "MT", "MLT", 493559, "Europe"))
    countries.put("Marshall_Islands", Country2("Marshall_Islands", "MH", "MHL", 58791, "Oceania"))
    countries.put("Mauritania", Country2("Mauritania", "MR", "MRT", 4525698, "Africa"))
    countries.put("Mauritius", Country2("Mauritius", "MU", "MUS", 1269670, "Africa"))
    countries.put("Mexico", Country2("Mexico", "MX", "MEX", 127575529, "America"))
    countries.put("Moldova", Country2("Moldova", "MD", "MDA", 4043258, "Europe"))
    countries.put("Monaco", Country2("Monaco", "MC", "MCO", 33085, "Europe"))
    countries.put("Mongolia", Country2("Mongolia", "MN", "MNG", 3225166, "Asia"))
    countries.put("Montenegro", Country2("Montenegro", "ME", "MNE", 622182, "Europe"))
    countries.put("Montserrat", Country2("Montserrat", "MS", "MSF", 4991, "America"))
    countries.put("Morocco", Country2("Morocco", "MA", "MAR", 36471766, "Africa"))
    countries.put("Mozambique", Country2("Mozambique", "MZ", "MOZ", 30366043, "Africa"))
    countries.put("Myanmar", Country2("Myanmar", "MM", "MMR", 54045422, "Asia"))
    countries.put("Namibia", Country2("Namibia", "NA", "NAM", 2494524, "Africa"))
    countries.put("Nepal", Country2("Nepal", "NP", "NPL", 28608715, "Asia"))
    countries.put("Netherlands", Country2("Netherlands", "NL", "NLD", 17282163, "Europe"))
    countries.put("New_Caledonia", Country2("New_Caledonia", "NC", "NCL", 282757, "Oceania"))
    countries.put("New_Zealand", Country2("New_Zealand", "NZ", "NZL", 4783062, "Oceania"))
    countries.put("Nicaragua", Country2("Nicaragua", "NI", "NIC", 6545503, "America"))
    countries.put("Niger", Country2("Niger", "NE", "NER", 23310719, "Africa"))
    countries.put("Nigeria", Country2("Nigeria", "NG", "NGA", 200963603, "Africa"))
    countries.put("North_Macedonia", Country2("North_Macedonia", "MK", "MKD", 2077132, "Europe"))
    countries.put("Northern_Mariana_Islands", Country2("Northern_Mariana_Islands", "MP", "MNP", 57213, "Oceania"))
    countries.put("Norway", Country2("Norway", "NO", "NOR", 5328212, "Europe"))
    countries.put("Oman", Country2("Oman", "OM", "OMN", 4974992, "Asia"))
    countries.put("Pakistan", Country2("Pakistan", "PK", "PAK", 216565317, "Asia"))
    countries.put("Palestine", Country2("Palestine", "PS", "PSE", 4981422, "Asia"))
    countries.put("Panama", Country2("Panama", "PA", "PAN", 4246440, "America"))
    countries.put("Papua_New_Guinea", Country2("Papua_New_Guinea", "PG", "PNG", 8776119, "Oceania"))
    countries.put("Paraguay", Country2("Paraguay", "PY", "PRY", 7044639, "America"))
    countries.put("Peru", Country2("Peru", "PE", "PER", 32510462, "America"))
    countries.put("Philippines", Country2("Philippines", "PH", "PHL", 108116622, "Asia"))
    countries.put("Poland", Country2("Poland", "PL", "POL", 37972812, "Europe"))
    countries.put("Portugal", Country2("Portugal", "PT", "PRT", 10276617, "Europe"))
    countries.put("Puerto_Rico", Country2("Puerto_Rico", "PR", "PRI", 2933404, "America"))
    countries.put("Qatar", Country2("Qatar", "QA", "QAT", 2832071, "Asia"))
    countries.put("Romania", Country2("Romania", "RO", "ROU", 19414458, "Europe"))
    countries.put("Russia", Country2("Russia", "RU", "RUS", 145872260, "Europe"))
    countries.put("Rwanda", Country2("Rwanda", "RW", "RWA", 12626938, "Africa"))
    countries.put("Saint_Kitts_and_Nevis", Country2("Saint_Kitts_and_Nevis", "KN", "KNA", 52834, "America"))
    countries.put("Saint_Lucia", Country2("Saint_Lucia", "LC", "LCA", 182795, "America"))
    countries.put("Saint_Vincent_and_the_Grenadines", Country2("Saint_Vincent_and_the_Grenadines", "VC", "VCT", 110593, "America"))
    countries.put("San_Marino", Country2("San_Marino", "SM", "SMR", 34453, "Europe"))
    countries.put("Sao_Tome_and_Principe", Country2("Sao_Tome_and_Principe", "ST", "STP", 215048, "Africa"))
    countries.put("Saudi_Arabia", Country2("Saudi_Arabia", "SA", "SAU", 34268529, "Asia"))
    countries.put("Senegal", Country2("Senegal", "SN", "SEN", 16296362, "Africa"))
    countries.put("Serbia", Country2("Serbia", "RS", "SRB", 6963764, "Europe"))
    countries.put("Seychelles", Country2("Seychelles", "SC", "SYC", 97741, "Africa"))
    countries.put("Sierra_Leone", Country2("Sierra_Leone", "SL", "SLE", 7813207, "Africa"))
    countries.put("Singapore", Country2("Singapore", "SG", "SGP", 5804343, "Asia"))
    countries.put("Sint_Maarten", Country2("Sint_Maarten", "SX", "SXM", 42389, "America"))
    countries.put("Slovakia", Country2("Slovakia", "SK", "SVK", 5450421, "Europe"))
    countries.put("Slovenia", Country2("Slovenia", "SI", "SVN", 2080908, "Europe"))
    countries.put("Solomon_Islands", Country2("Solomon_Islands", "SB", "SLB", 669821, "Oceania"))
    countries.put("Somalia", Country2("Somalia", "SO", "SOM", 15442906, "Africa"))
    countries.put("South_Africa", Country2("South_Africa", "ZA", "ZAF", 58558267, "Africa"))
    countries.put("South_Korea", Country2("South_Korea", "KR", "KOR", 51225321, "Asia"))
    countries.put("South_Sudan", Country2("South_Sudan", "SS", "SSD", 11062114, "Africa"))
    countries.put("Spain", Country2("Spain", "ES", "ESP", 46937060, "Europe"))
    countries.put("Sri_Lanka", Country2("Sri_Lanka", "LK", "LKA", 21323734, "Asia"))
    countries.put("Sudan", Country2("Sudan", "SD", "SDN", 42813237, "Africa"))
    countries.put("Suriname", Country2("Suriname", "SR", "SUR", 581363, "America"))
    countries.put("Sweden", Country2("Sweden", "SE", "SWE", 10230185, "Europe"))
    countries.put("Switzerland", Country2("Switzerland", "CH", "CHE", 8544527, "Europe"))
    countries.put("Syria", Country2("Syria", "SY", "SYR", 17070132, "Asia"))
    countries.put("Taiwan", Country2("Taiwan", "TW", "CNG1925", 23773881, "Asia"))
    countries.put("Tajikistan", Country2("Tajikistan", "TJ", "TJK", 9321023, "Asia"))
    countries.put("Thailand", Country2("Thailand", "TH", "THA", 69625581, "Asia"))
    countries.put("Timor_Leste", Country2("Timor_Leste", "TL", "TLS", 1293120, "Asia"))
    countries.put("Togo", Country2("Togo", "TG", "TGO", 8082359, "Africa"))
    countries.put("Trinidad_and_Tobago", Country2("Trinidad_and_Tobago", "TT", "TTO", 1394969, "America"))
    countries.put("Tunisia", Country2("Tunisia", "TN", "TUN", 11694721, "Africa"))
    countries.put("Turkey", Country2("Turkey", "TR", "TUR", 82003882, "Europe"))
    countries.put("Turks_and_Caicos_islands", Country2("Turks_and_Caicos_islands", "TC", "TCA", 38194, "America"))
    countries.put("Uganda", Country2("Uganda", "UG", "UGA", 44269587, "Africa"))
    countries.put("Ukraine", Country2("Ukraine", "UA", "UKR", 43993643, "Europe"))
    countries.put("United_Arab_Emirates", Country2("United_Arab_Emirates", "AE", "ARE", 9770526, "Asia"))
    countries.put("United_Kingdom", Country2("United_Kingdom", "UK", "GBR", 66647112, "Europe"))
    countries.put("United_Republic_of_Tanzania", Country2("United_Republic_of_Tanzania", "TZ", "TZA", 58005461, "Africa"))
    countries.put("United_States_of_America", Country2("United_States_of_America", "US", "USA", 329064917, "America"))
    countries.put("United_States_Virgin_Islands", Country2("United_States_Virgin_Islands", "VI", "VIR", 104579, "America"))
    countries.put("Uruguay", Country2("Uruguay", "UY", "URY", 3461731, "America"))
    countries.put("Uzbekistan", Country2("Uzbekistan", "UZ", "UZB", 32981715, "Asia"))
    countries.put("Vanuatu", Country2("Vanuatu", "VU", "VUT", 299882, "Oceania"))
    countries.put("Venezuela", Country2("Venezuela", "VE", "VEN", 28515829, "America"))
    countries.put("Vietnam", Country2("Vietnam", "VN", "VNM", 96462108, "Asia"))
    countries.put("Wallis_and_Futuna", Country2("Wallis_and_Futuna", "WF", "0", 0, "Oceania"))
    countries.put("Western_Sahara", Country2("Western_Sahara", "EH", "ESH", 582458, "Africa"))
    countries.put("Yemen", Country2("Yemen", "YE", "YEM", 29161922, "Asia"))
    countries.put("Zambia", Country2("Zambia", "ZM", "ZMB", 17861034, "Africa"))
    countries.put("Zimbabwe", Country2("Zimbabwe", "ZW", "ZWE", 14645473, "Africa"))


    return countries
}
