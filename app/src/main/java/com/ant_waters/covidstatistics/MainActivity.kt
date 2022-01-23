package com.ant_waters.covidstatistics

import android.content.Context
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
import androidx.fragment.app.viewModels
import com.ant_waters.covidstatistics.databinding.ActivityMainBinding
import com.ant_waters.covidstatistics.model.DataManager
import com.ant_waters.covidstatistics.ui.display__options.DisplayOptionsFragment
import com.ant_waters.covidstatistics.ui.display__options.JunkDialogFragment

/* TODO Items
* ) Database CRUD
* ) Settings, About, slideshow, floating button on home page
* ) Check, and apply, Android/Kotlin standard for capitalization of variables, methods, classes, layouts etc.
* ) Tidy up, sanitise, release as Kotlin demo, announce online.
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
                R.id.nav_home, R.id.nav_data_table, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        config()
    }

    private fun config()
    {
        Log.i(MainViewModel.LOG_TAG, "config: Starting")

        mainViewModel.Init(this)

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
//            R.id.action_settings -> {
//                val jdf = JunkDialogFragment()
//
//                jdf.show(this.supportFragmentManager, "JunkDialogFragment")
//                true
//            }
            R.id.display_options -> {
                showDisplayOptions()
                true
            }
            R.id.about -> {
                showAbout()
//                Toast.makeText(getApplicationContext(),
//                    "Number of CountryAggregates: ${DataManager?.CountryAggregates?.Aggregates?.size?:0}",Toast.LENGTH_SHORT).show();
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showDisplayOptions()
    {
        MainViewModel.DisplayOptionsBeingEdited = MainViewModel.DisplayOptions
        val dof = DisplayOptionsFragment()

        dof.show(this.supportFragmentManager, "displayoptions")
    }

    fun showAbout()
    {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("About CovidStatistics")
        var msg = "Author: Ant Waters\n"
        msg += "GitHub: https://github.com/AntWat/CovidStatistics\n\n"
        msg += "CovidStatistics is an Android Kotlin demonstration project\n"
        msg += "It includes the following features:\n"
        msg += "   *) Use of an SqLite database, using the 'Room' architecture.\n"
        msg += "      The database contains real COVID-19 data for the year 2020, downloaded from:\n"
        msg += "      https://www.ecdc.europa.eu/en/publications-data/download-todays-data-geographic-distribution-covid-19-cases-worldwide\n"
        msg += "   *) Use of the 'Navigation Drawer Activity' Android project template.\n"
        msg += "   *) TODO: Database CUD.\n"
        msg += "   *) Dislay of list data inluding icons (country flags), using a Recycler View.\n"
        msg += "   *) Display of tabular data, including grid lines.\n"
        msg += "      The cells are defined using a custom layout, so any data and graphics can potentially be displayed.\n"
        msg += "      Random icons are included with the text as an example.\n"
        msg += "      The table has fixed row headers and column headers, and can be scrolled in both directions.\n"
        msg += "   *) Popup view of total data for a selected country.\n"
        msg += "   *) Use of a custom Alert dialog to input DisplayOptions.\n"
        msg += "      This uses the UI elements: Radio buttons, lists, switch, buttons, Data Picker and Text Edit.\n"
        msg += "   *) Use of a Standard Alert dialog (this).\n"
        msg += "   *) Use of a ViewModel to preserve display when the screen is rotated.\n"
        msg += "   *) Observation of LiveData to update the display when data or display options change.\n"
        msg += "   *) General coding in Kotlin, including generics.\n"
        msg += "   *) TODO: Database CUD.\n"

        builder.setMessage(msg)
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(applicationContext,
                android.R.string.yes, Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            Toast.makeText(applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT).show()
        }

        builder.setNeutralButton("Maybe") { dialog, which ->
            Toast.makeText(applicationContext,
                "Maybe", Toast.LENGTH_SHORT).show()
        }
        builder.show()

    }

}