// File: app/src/main/java/com/group10/carservicebook/ui/ServiceViewModel.kt
package com.group10.carservicebook.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.group10.carservicebook.data.AppDatabase
import com.group10.carservicebook.data.PrefsManager
import com.group10.carservicebook.data.ServiceRecord
import kotlinx.coroutines.launch
import kotlin.math.max

class ServiceViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).serviceDao()
    private val prefs = PrefsManager(application)

    val allServices: LiveData<List<ServiceRecord>> = dao.getAllServices().asLiveData()

    private val _currentOdometer = MutableLiveData(prefs.getCurrentOdometer())
    val currentOdometer: LiveData<Int> = _currentOdometer

    private val _serviceInterval = MutableLiveData(prefs.getServiceInterval())
    val serviceInterval: LiveData<Int> = _serviceInterval

    private val _lastServiceMileage = MutableLiveData(0)
    val lastServiceMileage: LiveData<Int> = _lastServiceMileage

    val serviceToEdit = MutableLiveData<ServiceRecord?>(null)

    init {
        refreshDashboardData()
    }

    fun clearServiceToEdit() {
        serviceToEdit.value = null
    }

    fun setServiceToEdit(record: ServiceRecord) {
        serviceToEdit.value = record
    }

    fun refreshDashboardData() {
        viewModelScope.launch {
            val lastRecord = dao.getLastService()
            val lastMileage = lastRecord?.mileageAtService ?: 0
            _lastServiceMileage.value = lastMileage
            _currentOdometer.value = prefs.getCurrentOdometer()
            _serviceInterval.value = prefs.getServiceInterval()
        }
    }

    fun addService(record: ServiceRecord) {
        viewModelScope.launch {
            dao.insertService(record)
            if (record.mileageAtService > prefs.getCurrentOdometer()) {
                updateOdometer(record.mileageAtService)
            }
            refreshDashboardData()
        }
    }

    fun updateService(record: ServiceRecord) {
        viewModelScope.launch {
            dao.updateService(record)
            refreshDashboardData()
        }
    }

    fun deleteService(record: ServiceRecord) {
        viewModelScope.launch {
            dao.deleteService(record)
            refreshDashboardData()
        }
    }

    fun updateOdometer(newKm: Int) {
        prefs.saveCurrentOdometer(newKm)
        _currentOdometer.value = newKm
    }

    fun updateInterval(newInterval: Int) {
        prefs.saveServiceInterval(newInterval)
        _serviceInterval.value = newInterval
    }

    fun calculateRemainingKm(): Int {
        val last = _lastServiceMileage.value ?: 0
        val interval = _serviceInterval.value ?: 5000
        val current = _currentOdometer.value ?: 0
        val nextDue = last + interval
        return max(0, nextDue - current)
    }

    // MODIFIED: Logic inverted to act as a "Health Bar" (100% -> 0%)
    fun calculateProgress(): Int {
        val last = _lastServiceMileage.value ?: 0
        val interval = _serviceInterval.value ?: 5000
        val current = _currentOdometer.value ?: 0

        if (interval == 0) return 100 // Default to full health if no interval set

        val drivenSinceService = current - last
        // Calculate how much we have used
        val percentUsed = (drivenSinceService.toFloat() / interval.toFloat()) * 100

        // Return Remaining % (100 - Used)
        val percentRemaining = 100 - percentUsed.toInt()

        return percentRemaining.coerceIn(0, 100)
    }
}