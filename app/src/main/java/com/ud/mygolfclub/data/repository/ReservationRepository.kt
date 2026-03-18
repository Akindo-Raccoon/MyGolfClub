package com.ud.mygolfclub.data.repository

import androidx.lifecycle.LiveData
import com.ud.mygolfclub.data.dao.ReservationDAO
import com.ud.mygolfclub.data.entity.Reservation
//Good Practice - class abstract access to Multiple data sources
class ReservationRepository(private val reservationDao: ReservationDAO) {
    val allReservations: LiveData<List<Reservation>> = reservationDao.getAllReservations()

    fun searchByName(name: String): LiveData<List<Reservation>> =
        reservationDao.searchByName(name)

    suspend fun createReservation(reservation: Reservation): Result<Unit> = runCatching {
        val conflicts = reservationDao.countConflicts(
            reservation.court, reservation.date, reservation.hour
        )
        require(conflicts == 0) { "Esa cancha ya tiene una reserva activa en esa fecha y hora." }
        reservationDao.insertReservation(reservation)
    }

    suspend fun deleteReservation(id: Int) = reservationDao.deleteReservation(id)
    suspend fun updateReservation(reservation: Reservation) =
        reservationDao.updateReservation(reservation)

    suspend fun getUserReservations(clientId: String): List<Reservation> =
        reservationDao.getReservationsByUser(clientId)
}