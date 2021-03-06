package com.ant_waters.covidstatistics.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ant_waters.covidstatistics.MainViewModel
import com.ant_waters.covidstatistics.adapters.HomeItemAdapter
import com.ant_waters.covidstatistics.databinding.FragmentHomeBinding
import com.ant_waters.covidstatistics.enDataLoaded
import com.ant_waters.covidstatistics.model.DataManager

/**
 * Code for the fragment for the home page
 */
class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(MainViewModel.LOG_TAG, "HomeFragment.onCreateView: Started")

//        homeViewModel =
//            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recyclerView.setVisibility(View.GONE)
        binding.progressBar1.setVisibility(View.VISIBLE)

        val recyclerView = binding.recyclerView
        val homeItemAdapter = HomeItemAdapter(this, DataManager.Countries, DataManager.CountryAggregates)
        recyclerView.adapter = homeItemAdapter

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true)

        MainViewModel.DataInitialised.observe(viewLifecycleOwner, Observer {
            if (it == enDataLoaded.CountriesOnly) {
                Log.i(MainViewModel.LOG_TAG, "Observer: HomeFragment, Globals.DataInitialised: Started for countries")
                binding.recyclerView.setVisibility(View.VISIBLE)

                binding.recyclerView.adapter?.notifyItemRangeInserted(0,
                    DataManager.CountryAggregates?.Aggregates?.size?:0)
            } else if (it == enDataLoaded.All) {
                Log.i(MainViewModel.LOG_TAG, "Observer: HomeFragment, Globals.DataInitialised: Started for all data")
                binding.recyclerView.setVisibility(View.VISIBLE)
                binding.progressBar1.setVisibility(View.GONE)
                binding.recyclerView.adapter?.notifyItemRangeChanged(0,
                    DataManager.CountryAggregates?.Aggregates?.size?:0)
            }
        })

        MainViewModel.DisplayOptionsChanged.observe(viewLifecycleOwner, Observer {
            Log.i(MainViewModel.LOG_TAG, "Observer: HomeFragment, Globals.DisplayOptionsChanged: Started")
            homeItemAdapter.notifyDataSetChanged()
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}