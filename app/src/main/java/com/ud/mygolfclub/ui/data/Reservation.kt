package com.ud.mygolfclub.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservations")
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userName: String,
    val court: String,
    val date: String,
    val hour: String,
    val status: String = "Activa"
)
