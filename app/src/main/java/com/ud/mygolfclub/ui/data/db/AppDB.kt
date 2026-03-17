package com.ud.mygolfclub.ui.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ud.mygolfclub.ui.data.Client
import com.ud.mygolfclub.ui.data.Reservation
import com.ud.mygolfclub.ui.data.db.ReservationDAO

@Database(entities = [Reservation::class, Client::class], version = 1)
abstract class AppDB: RoomDatabase() {
    abstract fun ReservationDAO(): ReservationDAO
    abstract fun ClientDAO(): ClientDAO
}