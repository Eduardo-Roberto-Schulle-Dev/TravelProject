package com.example.travelproject.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip")
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val destino: String,
    val dataInicio: String,
    val dataFinal: String,
    val orcamento: Double
)
