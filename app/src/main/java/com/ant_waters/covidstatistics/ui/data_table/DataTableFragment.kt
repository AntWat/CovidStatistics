package com.ant_waters.covidstatistics.ui.data_table

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ant_waters.covidstatistics.databinding.FragmentDataTableBinding

class DataTableFragment : Fragment() {

    private lateinit var dataTableViewModel: DataTableViewModel
    private var _binding: FragmentDataTableBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataTableViewModel =
            ViewModelProvider(this).get(DataTableViewModel::class.java)

        _binding = FragmentDataTableBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDataTable
        dataTableViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}