# CovidStatistics

CovidStatistics is an Android Kotlin demonstration project.
It includes the following features:
* Use of an SqLite database, using the 'Room' architecture.

  The database is included as an asset file and loaded on startup. 
  It contains real COVID-19 data for the year 2020, downloaded from:
  https://www.ecdc.europa.eu/en/publications-data/download-todays-data-geographic-distribution-covid-19-cases-worldwide
* Use of the 'Navigation Drawer Activity' Android project template.
* Database CRUD operations.
* Display of list data including icons (country flags), using a Recycler View.
* Early display of partial data (just countries, without statistics)
* Showing a progress spinner while data is loading.
  Use of coroutines (multi-threading) to load data.
* Display of tabular data, including grid lines.
  The cells are defined using a custom layout, so any data and graphics can potentially be displayed.
  Random icons are included with the text as an example.
  The table has fixed row headers and column headers, and can be scrolled in both directions.
  The column widths are adjusted dynamically.
* Popup view of total data for a selected country.
* Use of a custom Alert dialog to select DisplayOptions, on a scrollable screen.
  This uses the UI elements: Radio buttons, lists, switch, buttons, Data Picker and Text Edit.
* Use of a Standard Alert dialog, including hyperlinks.
* Use of a ViewModel to preserve display when the screen is rotated.
* Observation of LiveData to update the display when data or display options change.
* General coding in Kotlin, including generics.

Please note that this code does not follow all the Kotlin style guides, which can be found at:
https://developer.android.com/kotlin/style-guide

## Screen shots

### The home screen:

<p align="center">
  <img src="https://user-images.githubusercontent.com/12671573/155895517-d1f2d9f7-422c-4824-aae8-3005068ed718.png" width="350" />
</p>

---
### The data table screen:

<p align="center">
<img src="https://user-images.githubusercontent.com/12671573/155895683-df1bc665-be02-497a-9371-8ba4255ae50c.png" width="350" />
</p>

---
### A popup:

<p align="center">
<img src="https://user-images.githubusercontent.com/12671573/155895733-770ab999-9d16-40ba-ae0d-13b79894a25b.png" width="350" />
</p>

---
### A data editor

<p align="center">
<img src="https://user-images.githubusercontent.com/12671573/155895745-2f08c348-e42d-4ac5-9560-3ceafb8c8454.png" width="350" />
</p>

---
### An options editor

<p align="center">
<img src="https://user-images.githubusercontent.com/12671573/155895765-238b8735-3eb6-4feb-890a-87c7de462ad0.png" width="350" />
</p>

---
### The About box

<p align="center">
<img src="https://user-images.githubusercontent.com/12671573/155895781-682bfab0-f159-4658-8cad-0377e9774d2c.png" width="350" />
</p>



