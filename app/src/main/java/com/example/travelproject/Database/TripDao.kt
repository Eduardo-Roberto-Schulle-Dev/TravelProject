package com.example.travelproject.database

import androidx.room.*

@Dao
interface TripDao {
    @Insert
    suspend fun insertTrip(trip: Trip)

    @Query("SELECT * FROM trip")
    suspend fun getAllTrips(): List<Trip>

    @Query("SELECT * FROM trip WHERE id = :id")
    suspend fun getTripById(id: Int): Trip?

   @Query("UPDATE trip SET sugestao = :suggestion WHERE id = :tripId")
    suspend fun updateSuggestion(tripId: Int, suggestion: String)


    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)
}
