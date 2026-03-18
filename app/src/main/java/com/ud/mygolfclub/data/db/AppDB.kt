package com.ud.mygolfclub.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ud.mygolfclub.data.entity.Client
import com.ud.mygolfclub.data.entity.Reservation
import com.ud.mygolfclub.data.dao.ClientDAO
import com.ud.mygolfclub.data.dao.ReservationDAO

// Main Access Point
@Database(entities = [Reservation::class, Client::class], version = 1, exportSchema = false)
abstract class AppDB: RoomDatabase() {
    abstract fun ClientDAO(): ClientDAO
    abstract fun ReservationDAO(): ReservationDAO
}