package com.example.travelproject.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelproject.database.Trip
import com.example.travelproject.database.TripDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TripViewModel(application: Application) : AndroidViewModel(application) {


    private val context = application.applicationContext

    // TripViewModel.kt
    class TripViewModel(application: Application) : AndroidViewModel(application) {

        private val db = TripDatabase.getDatabase(application.applicationContext)

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
    }
}





