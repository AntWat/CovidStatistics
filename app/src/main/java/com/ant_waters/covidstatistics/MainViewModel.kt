package com.ant_waters.covidstatistics

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ant_waters.covidstatistics.model.Country2
import com.ant_waters.covidstatistics.model.DataManager
import com.ant_waters.covidstatistics.ui.display__options.DisplayOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.measureTimeMillis

enum class enDataLoaded { None, CountriesOnly, All}

// ViewModel for MainActivity, added so that data will persist across screen rotations.
// In particular, we don't want to have to reload from the database if the screen is rotated!
public class MainViewModel : ViewModel() {
    companion object {
        val LOG_TAG = "CovidStatistics"
        var MainPackageName = ""

        // ------------------------
        val _dataInitialised = MutableLiveData<enDataLoaded>().apply {
            value = enDataLoaded.None
        }
        val DataInitialised: LiveData<enDataLoaded> = _dataInitialised
        public fun UpdateDataInitialised(newVal: enDataLoaded)
        {
            _dataInitialised.postValue(newVal)
        }

        // ------------------------
        private var _displayOptions: DisplayOptions = DisplayOptions()
        val DisplayOptions get() = this._displayOptions

        public lateinit var DisplayOptionsBeingEdited: DisplayOptions
        // Used as a messy way to pass to the editor, and persist if the editor is showing when the screen is rotated

        // ------------------------
        // This live data flag uses date time so it can be updated to a new value at any time.
        val _displayOptionsChanged = MutableLiveData<SimpleDateFormat>().apply {
            value = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        }
        val DisplayOptionsChanged: LiveData<SimpleDateFormat> = _displayOptionsChanged

        public fun UpdateDisplayOptionsChanged()
        {
            //_displayOptionsChanged.value = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            _displayOptionsChanged.postValue(SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"))
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // This is private because the loaded data is static, and is accessed via the DataManager Companion Object
    private var _dataManager = DataManager()

    private var _isInitialised = false
    public fun Init(context: Context)
    {
        if (_isInitialised) {return}

        Log.i(MainViewModel.LOG_TAG, "Starting MainViewModel.Init")

        MainViewModel.MainPackageName = context.packageName

        GlobalScope.launch {
            val elapsedTime= measureTimeMillis {
                _dataManager.LoadData(context,
                    fun(state:enDataLoaded) { UpdateDataInitialised(state) })
            }
            Log.i(MainViewModel.LOG_TAG, "LoadData: Finished in ${elapsedTime} millisecs")
        }
        _isInitialised = true
    }


}