package com.ud.mygolfclub.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservations",
    foreignKeys = [ForeignKey(
        entity = Client::class,
        parentColumns = ["idClient"],
        childColumns = ["clientId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val clientId: String,
    val clientName: String,
    val clientPhone: String,
    val court: String,
    val date: String,
    val hour: String,
    val status: String = "Activa"
)