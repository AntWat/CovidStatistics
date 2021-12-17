package com.ant_waters.covidstatistics.ui.data_table

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataTableViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is data table Fragment"
    }
    val text: LiveData<String> = _text
}