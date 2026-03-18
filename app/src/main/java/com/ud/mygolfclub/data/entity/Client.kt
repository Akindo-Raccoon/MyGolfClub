package com.ud.mygolfclub.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey()
    val idClient: String,
    val name: String,
    val phoneClient: String,
    val pwdClient: String
)