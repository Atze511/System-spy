package com.example.systemmonitor.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.systemmonitor.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        
        // Beobachte LiveData
        dashboardViewModel.lastLocation.observe(viewLifecycleOwner) { location ->
            binding.tvLastLocation.text = location ?: "Keine Standortdaten"
        }
        
        dashboardViewModel.connectionStatus.observe(viewLifecycleOwner) { status ->
            binding.tvStatus.text = status
        }
        
        // Lade Daten
        dashboardViewModel.loadData()
        
        // Refresh Button
        binding.btnRefresh.setOnClickListener {
            dashboardViewModel.loadData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}