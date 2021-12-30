package com.ant_waters.covidstatistics

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

/* TODO Items
* ) Add warning icon to data table
* ) Add options for country list and data table, 4 statistics
* ) Add popup to display country data:
*   - name, flag, Continent, Population, 4 statistics for 2020
* ) Tidy up, santise, release as Kotlin demo, announce online.
* */


enum class enDataLoaded { None, CountriesOnly, All};

class MainActivity : AppCompatActivity() {
    companion object {
        val LOG_TAG = "CovidStatistics_MainAct"
        var MainPackageName = ""

        val _dataInitialised = MutableLiveData<enDataLoaded>().apply {
            value = enDataLoaded.None
        }
        val DataInitialised: LiveData<enDataLoaded> = _dataInitialised
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
}