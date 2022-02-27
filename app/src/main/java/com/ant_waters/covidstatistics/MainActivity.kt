package com.ant_waters.covidstatistics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.ant_waters.covidstatistics.databinding.ActivityMainBinding
import com.ant_waters.covidstatistics.ui.display__options.DisplayOptionsFragment
import android.text.util.Linkify
import android.view.Gravity

import android.widget.TextView
import androidx.room.ColumnInfo
import com.ant_waters.covidstatistics.Utils.Utils
import com.ant_waters.covidstatistics.database.continent
import com.ant_waters.covidstatistics.database.country
import com.ant_waters.covidstatistics.model.Country2
import com.ant_waters.covidstatistics.model.DataManager
import com.ant_waters.covidstatistics.ui.edit__countries.EditCountriesFragment

/* TODO Items
* ) floating button on home page
* ) Offload strings to resources
*) Search TODO items
*  */

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
//    val mainViewModel: MainViewModel by viewModels()
//    mainViewModel.getUsers().observe(this, Observer<List<User>>{ users ->
//        // update UI
//    })


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_data_table      //, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        config()
    }

    private fun config() {
        Log.i(MainViewModel.LOG_TAG, "config: Starting")

        mainViewModel.init(this)

        Log.i(MainViewModel.LOG_TAG, "config: Finished")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.edit_countries -> {
                editCountries()
                true
            }
            R.id.display_options -> {
                showDisplayOptions()
                true
            }
            R.id.about -> {
                showAbout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showDisplayOptions()
    {
        try {
            MainViewModel.DisplayOptionsBeingEdited = MainViewModel.DisplayOptions
            val dof = DisplayOptionsFragment()

            dof.show(this.supportFragmentManager, "displayoptions")
        }
        catch (ex:Exception) { LogAndShowError(ex)}
    }

    fun editCountries()
    {
        try {
            val dof = EditCountriesFragment()

            dof.show(this.supportFragmentManager, "editcountries")
        }
        catch (ex:Exception) { LogAndShowError(ex)}
    }

    fun showAbout()
    {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("About CovidStatistics")
            var msg = "Author: Ant Waters\n"
            msg += "GitHub: https://github.com/AntWat/CovidStatistics\n\n"
            msg += "CovidStatistics is an Android Kotlin demonstration project.\n"
            msg += "It includes the following features:\n"
            msg += "   *) Use of an SqLite database, using the 'Room' architecture.\n"
            msg += "      The database contains real COVID-19 data for the year 2020, downloaded from:\n"
            msg += "      https://www.ecdc.europa.eu/en/publications-data/download-todays-data-geographic-distribution-covid-19-cases-worldwide\n"
            msg += "   *) Use of the 'Navigation Drawer Activity' Android project template.\n"
            msg += "   *) Database CRUD operations.\n"
            msg += "   *) Display of list data including icons (country flags), using a Recycler View.\n"
            msg += "   *) Early display of partial data (just countries, without statistics)\n"
            msg += "   *) Showing a progress spinner while data is loading.\n"
            msg += "      Use of coroutines (multi-threading) to load data.\n"
            msg += "   *) Display of tabular data, including grid lines.\n"
            msg += "      The cells are defined using a custom layout, so any data and graphics can potentially be displayed.\n"
            msg += "      Random icons are included with the text as an example.\n"
            msg += "      The table has fixed row headers and column headers, and can be scrolled in both directions.\n"
            msg += "      The column widths are adjusted dynamically.\n"
            msg += "   *) Popup view of total data for a selected country.\n"
            msg += "   *) Use of a custom Alert dialog to select DisplayOptions, on a scrollable screen.\n"
            msg += "      This uses the UI elements: Radio buttons, lists, switch, buttons, Data Picker and Text Edit.\n"
            msg += "   *) Use of a Standard Alert dialog (this), including hyperlinks.\n"
            msg += "   *) Use of a ViewModel to preserve display when the screen is rotated.\n"
            msg += "   *) Observation of LiveData to update the display when data or display options change.\n"
            msg += "   *) General coding in Kotlin, including generics.\n"
            msg += "\n"
            msg += "Please note that this code does not follow all the Kotlin style guides, which can be found at:\n"
            msg += "https://developer.android.com/kotlin/style-guide\n"

            builder.setMessage(msg)

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            }

            var dlg = builder.create()
            dlg.show()
            Linkify.addLinks((dlg.findViewById(android.R.id.message) as TextView?)!!, Linkify.ALL)
        } catch (ex:Exception) { LogAndShowError(ex)}
    }

    // ---------------------------------------

    fun ShowError(errMsg: String) {
        Utils.ShowToast(this,
            "Error! ${errMsg}",
            Toast.LENGTH_LONG, Gravity.TOP, Color.RED)
    }

    fun LogAndShowError(ex:Exception)
    {
        val errMsg: String = ex.message.toString()
        Log.e(MainViewModel.LOG_TAG, errMsg)
        ShowError(errMsg)
    }

}