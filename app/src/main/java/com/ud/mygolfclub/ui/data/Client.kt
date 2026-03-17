package com.ud.mygolfclub.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true)
    val idClient: String,
    val name: String,
    val phoneClient: String,
    val pwdClient: String
)
