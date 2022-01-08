package com.ant_waters.covidstatistics

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ant_waters.covidstatistics.databinding.ActivityMainBinding
import com.ant_waters.covidstatistics.model.DataManager
import com.ant_waters.covidstatistics.ui.country_pop_up.CountryPopupFragment
import com.ant_waters.covidstatistics.ui.display__options.DisplayOptionsFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

import java.text.SimpleDateFormat

/* TODO Items
* ) Add options for country list and data table, 4 statistics, filters etc.
* ) Database CRUD
* ) Tidy up, sanitise, release as Kotlin demo, announce online.
*) Search TODO items
*
*         * ) Add warning icon to data table
*         * ) Add popup to display country data:
*        *   - name, flag, Continent, Population, 4 statistics for 2020
* */


enum class enDataLoaded { None, CountriesOnly, All}

class MainActivity : AppCompatActivity() {
    companion object {
        val LOG_TAG = "CovidStatistics_MainAct"
        var MainPackageName = ""

        val _dataInitialised = MutableLiveData<enDataLoaded>().apply {
            value = enDataLoaded.None
        }
        val DataInitialised: LiveData<enDataLoaded> = _dataInitialised


        // This live data flag uses date time so it can be updated to a new value at any time.
        val _displayOptionsChanged = MutableLiveData<SimpleDateFormat>().apply {
            value = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        }
        val DisplayOptionsChanged: LiveData<SimpleDateFormat> = _displayOptionsChanged

        public fun UpdateDisplayOptionsChanged()
        {
            _displayOptionsChanged.value = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        }
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var _dataManager = DataManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _dataInitialised.setValue(enDataLoaded.None)

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
        Log.i(LOG_TAG, "config: Starting")

        MainActivity.MainPackageName = this.packageName

        val context: Context = this
        GlobalScope.launch {
            val elapsedTime= measureTimeMillis {
                _dataManager.LoadData(context,
                    fun(state:enDataLoaded) { _dataInitialised.postValue(state)})
            }
            Log.i(LOG_TAG, "LoadData: Finished in ${elapsedTime} millisecs")
            //_dataInitialised.postValue(enDataLoaded.All)
        }

        Log.i(LOG_TAG, "config: Finished")
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
            R.id.action_settings -> {
                //newGame()
                true
            }
            R.id.display_options -> {
                showDisplayOptions()
                true
            }
            R.id.about -> {
                //showHelp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun showDisplayOptions()
    {
        val dof = DisplayOptionsFragment()

        // Supply country as an argument.
        val args = Bundle()
        args.putString("geoId", "BB")
        dof.setArguments(args)

        dof.show(this.supportFragmentManager, "displayoptions")

    }
}