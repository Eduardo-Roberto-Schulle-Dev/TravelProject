package com.example.travelproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atividadefinal.Database.AppDatabase
import com.example.travelproject.database.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TripViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application.applicationContext)

    fun salvarViagem(trip: Trip, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    db.tripDao().insertTrip(trip)
                }
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun buscarViagens(onResult: (List<Trip>) -> Unit) {
        viewModelScope.launch {
            val trips = withContext(Dispatchers.IO) {
                db.tripDao().getAllTrips()
            }
            onResult(trips)
        }
    }

    fun deletarViagem(trip: Trip, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    db.tripDao().deleteTrip(trip)
                }
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun atualizarViagem(trip: Trip, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    db.tripDao().updateTrip(trip)
                }
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}
