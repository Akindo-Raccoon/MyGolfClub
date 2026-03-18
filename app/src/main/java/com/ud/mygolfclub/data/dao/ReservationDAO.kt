package com.ud.mygolfclub.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ud.mygolfclub.data.entity.Reservation

@Dao
interface ReservationDAO {
    // Add new Reservation
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReservation(reservation: Reservation)

    // Search Reserves by IdUser
    @Query("SELECT * FROM reservations WHERE clientId = :user")
    suspend fun getReservationsByUser(user: String): List<Reservation>

    // Search Reserves by IdUser
    @Query("SELECT * FROM reservations WHERE clientName LIKE '%' || :name || '%'")
    fun searchByName(name: String): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservations")
    fun getAllReservations(): LiveData<List<Reservation>>

    @Query("DELETE FROM reservations WHERE id = :id")
    suspend fun deleteReservation(id: Int)

    @Update
    suspend fun updateReservation(reservation: Reservation)

    @Query("""
        SELECT COUNT(*) FROM reservations 
        WHERE court = :court AND date = :date AND hour = :hour AND status = 'Activa'
    """)
    suspend fun countConflicts(court: String, date: String, hour: String): Int
}