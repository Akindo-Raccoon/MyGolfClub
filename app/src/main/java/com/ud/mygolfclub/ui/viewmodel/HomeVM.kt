package com.ud.mygolfclub.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ud.mygolfclub.data.db.GolfDB
import com.ud.mygolfclub.data.entity.Reservation
import com.ud.mygolfclub.data.repository.ReservationRepository
import com.ud.mygolfclub.ui.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ReservationState {
    object Idle : ReservationState()
    object Loading : ReservationState()
    object Success : ReservationState()
    data class Error(val message: String) : ReservationState()
}

class HomeVM (application: Application) : AndroidViewModel(application) {
    private val db = GolfDB.getDB(application)
    private val reservationRepo = ReservationRepository(db.ReservationDAO())

    // Data Client Logged
    var clientId: String = ""
        private set
    var clientName: String = ""
        private set
    var clientPhone: String = ""
        private set

    val allReservations: LiveData<List<Reservation>> = reservationRepo.allReservations

    private val _reservationState = MutableStateFlow<ReservationState>(ReservationState.Idle)
    val reservationState: StateFlow<ReservationState> = _reservationState

    private val _searchResults = MutableStateFlow<List<Reservation>>(emptyList())
    val searchResults: StateFlow<List<Reservation>> = _searchResults

    fun loadSession(context: android.content.Context) {
        clientId = SessionManager.getClientId(context)
        clientName = SessionManager.getClientName(context)
        clientPhone = SessionManager.getClientPhone(context)
    }

    fun createReservation(court: String, date: String, hour: String, players: Int) {
        if (court.isBlank() || date.isBlank() || hour.isBlank()) {
            _reservationState.value = ReservationState.Error("Completa todos los campos")
            return
        }
        viewModelScope.launch {
            _reservationState.value = ReservationState.Loading
            val reservation = Reservation(
                clientId = clientId,
                clientName = clientName,
                clientPhone = clientPhone,
                court = court,
                date = date,
                hour = hour,
                status = "Activa"
            )
            reservationRepo.createReservation(reservation).fold(
                onSuccess = { _reservationState.value = ReservationState.Success },
                onFailure = { _reservationState.value = ReservationState.Error(it.message ?: "Error") }
            )
        }
    }

    fun deleteReservation(id: Int) {
        viewModelScope.launch {
            reservationRepo.deleteReservation(id)
        }
    }

    fun updateReservation(reservation: Reservation) {
        viewModelScope.launch {
            reservationRepo.updateReservation(reservation)
        }
    }

    fun searchByName(name: String) {
        viewModelScope.launch {
            reservationRepo.searchByName(name).observeForever { list ->
                _searchResults.value = list
            }
        }
    }

    fun resetState() {
        _reservationState.value = ReservationState.Idle
    }
}