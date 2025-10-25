package com.example.systemmonitor.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.systemmonitor.network.RetrofitClient
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _lastLocation = MutableLiveData<String>()
    val lastLocation: LiveData<String> = _lastLocation

    private val _connectionStatus = MutableLiveData<String>()
    val connectionStatus: LiveData<String> = _connectionStatus

    fun loadData() {
        viewModelScope.launch {
            try {
                _connectionStatus.value = "Verbinde mit Server..."
                
                val apiService = RetrofitClient.getApiService()
                val response = apiService.getLocationData().execute()
                
                if (response.isSuccessful) {
                    _connectionStatus.value = "Verbunden ✓"
                    
                    val locations = response.body()?.data
                    if (!locations.isNullOrEmpty()) {
                        val latestLocation = locations.last()
                        _lastLocation.value = latestLocation.dataValue
                    } else {
                        _lastLocation.value = "Keine Standortdaten verfügbar"
                    }
                } else {
                    _connectionStatus.value = "Server Fehler: ${response.code()}"
                }
                
            } catch (e: Exception) {
                _connectionStatus.value = "Verbindungsfehler: ${e.message}"
            }
        }
    }
}