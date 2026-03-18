package com.ud.mygolfclub.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ud.mygolfclub.data.entity.Reservation
import com.ud.mygolfclub.ui.session.SessionManager
import com.ud.mygolfclub.ui.viewmodel.HomeVM
import com.ud.mygolfclub.ui.viewmodel.ReservationState
import com.ud.mygolfclub.ui.theme.GolfGreen
import com.ud.mygolfclub.ui.theme.LightGreen
import com.ud.mygolfclub.ui.theme.ActiveColor
import com.ud.mygolfclub.ui.theme.FinalizedColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    vm: HomeVM = viewModel()
){
    val context = LocalContext.current

    // Load Session once time
    LaunchedEffect(Unit) { vm.loadSession(context) }

    val reservations by vm.allReservations.observeAsState(emptyList())
    val reservationState by vm.reservationState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showNewReservation by remember { mutableStateOf(false) }
    var editingReservation by remember { mutableStateOf<Reservation?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }

    // Filter local for name
    val displayed = remember(reservations, searchQuery) {
        if (searchQuery.isBlank()) reservations
        else reservations.filter {
            it.clientName.contains(searchQuery, ignoreCase = true)
        }
    }

    // Statistics
    val today = remember {
        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            .format(java.util.Date())
    }
    val todayCount = reservations.count { it.date == today }
    val occupiedCourts = reservations.filter { it.status == "Activa" }
        .map { it.court }.distinct().size
    val activeCount = reservations.count { it.status == "Activa" }
    val finalizedCount = reservations.count { it.status == "Finalizada" }

    LaunchedEffect (reservationState) {
        when (val s = reservationState) {
            is ReservationState.Success -> {
                Toast.makeText(context, "Reserva guardada ✓", Toast.LENGTH_SHORT).show()
                showNewReservation = false
                editingReservation = null
                vm.resetState()
            }
            is ReservationState.Error -> {
                Toast.makeText(context, s.message, Toast.LENGTH_LONG).show()
                vm.resetState()
            }
            else -> {}
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Golf Club", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GolfGreen),
                actions = {
                    IconButton (onClick = {
                        SessionManager.clearSession(context)
                        onLogout()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Salir",
                            tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton (
                    onClick = { showNewReservation = true },
                    containerColor = GolfGreen
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva reserva",
                        tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column (
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF1F8E9))
        ) {
            TabRow (selectedTabIndex = selectedTab, containerColor = GolfGreen) {
                Tab (selected = selectedTab == 0, onClick = { selectedTab = 0 },
                    text = { Text("Resumen", color = Color.White) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                    text = { Text("Reservas", color = Color.White) })
            }

            when (selectedTab) {
                0 -> SummaryTab(todayCount, occupiedCourts, activeCount, finalizedCount,
                    reservations, onNewReservation = { showNewReservation = true })
                1 -> ReservationsTab(
                    reservations = displayed,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    onEdit = { editingReservation = it },
                    onDelete = { vm.deleteReservation(it.id) }
                )
            }
        }
    }
    // Dialog: New reserve
    if (showNewReservation) {
        ReservationFormDialog(
            title = "Nueva Reserva",
            clientName = vm.clientName,
            clientPhone = vm.clientPhone,
            reservation = null,
            isLoading = reservationState is ReservationState.Loading,
            onSave = { court, date, hour, status ->
                vm.createReservation(court, date, hour, 4)
            },
            onDismiss = { showNewReservation = false }
        )
    }

    // Dialog: Editar reserva
    editingReservation?.let { res ->
        ReservationFormDialog(
            title = "Editar Reserva",
            clientName = res.clientName,
            clientPhone = res.clientPhone,
            reservation = res,
            isLoading = reservationState is ReservationState.Loading,
            onSave = { court, date, hour, status ->
                vm.updateReservation(res.copy(court = court, date = date,
                    hour = hour, status = status))
                editingReservation = null
            },
            onDismiss = { editingReservation = null }
        )
    }
}

// ─── Pestaña Resumen ───────
@Composable
private fun SummaryTab(
    todayCount: Int, occupiedCourts: Int,
    activeCount: Int, finalizedCount: Int,
    reservations: List<Reservation>,
    onNewReservation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row (horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Reservas Hoy", todayCount.toString(), GolfGreen, Modifier.weight(1f))
            StatCard("Canchas Ocupadas", occupiedCourts.toString(), Color(0xFF1565C0),
                Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Reservas Activas", activeCount.toString(), ActiveColor, Modifier.weight(1f))
            StatCard("Finalizadas", finalizedCount.toString(), FinalizedColor, Modifier.weight(1f))
        }

        Text("Próximas Reservas", fontWeight = FontWeight.Bold,
            color = GolfGreen, fontSize = 16.sp)

        val upcoming = reservations.filter { it.status == "Activa" }.take(5)
        if (upcoming.isEmpty()) {
            Text("No hay reservas activas", color = Color.Gray)
        } else {
            upcoming.forEach { res ->
                Text("• ${res.clientName} - ${res.hour} - Cancha ${res.court}",
                    color = Color(0xFF424242))
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = onNewReservation,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = GolfGreen)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Nueva Reserva", color = Color.White)
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(label, fontSize = 12.sp, color = Color.White)
        }
    }
}

// Tab List Reservations
@Composable
private fun ReservationsTab(
    reservations: List<Reservation>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onEdit: (Reservation) -> Unit,
    onDelete: (Reservation) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Buscar reserva...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GolfGreen, focusedLabelColor = GolfGreen)
        )
        Spacer(Modifier.height(8.dp))
        if (reservations.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Sin reservas", color = Color.Gray)
            }
        } else {
            LazyColumn (verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(reservations, key = { it.id }) { res ->
                    ReservationCard(res, onEdit = { onEdit(res) }, onDelete = { onDelete(res) })
                }
            }
        }
    }
}

@Composable
private fun ReservationCard(
    res: Reservation,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showConfirmDelete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(res.clientName, fontWeight = FontWeight.Bold, color = GolfGreen)
                Text("${res.date}  ${res.hour}  •  Cancha ${res.court}",
                    fontSize = 13.sp, color = Color(0xFF616161))
            }
            StatusChip(res.status)
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = GolfGreen)
            }
            IconButton(onClick = { showConfirmDelete = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar",
                    tint = Color(0xFFD32F2F))
            }
        }
    }
    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("Eliminar reserva") },
            text = { Text("¿Eliminar la reserva de ${res.clientName}?") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showConfirmDelete = false }) {
                    Text("Eliminar", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun StatusChip(status: String) {
    val bg = if (status == "Activa") ActiveColor else FinalizedColor
    Surface(shape = RoundedCornerShape(20.dp), color = bg) {
        Text(status, color = Color.White, fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
    }
}

// Dialog Form
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReservationFormDialog(
    title: String,
    clientName: String,
    clientPhone: String,
    reservation: Reservation?,
    isLoading: Boolean,
    onSave: (court: String, date: String, hour: String, status: String) -> Unit,
    onDismiss: () -> Unit
) {
    var court by remember { mutableStateOf(reservation?.court ?: "1") }
    var date by remember { mutableStateOf(reservation?.date ?: "") }
    var hour by remember { mutableStateOf(reservation?.hour ?: "10:00 AM") }
    var status by remember { mutableStateOf(reservation?.status ?: "Activa") }

    val courtOptions = listOf("1", "2", "3", "4", "5")
    val hourOptions = listOf("8:00 AM","9:00 AM","10:00 AM","11:00 AM",
        "12:00 PM","1:00 PM","2:00 PM","3:00 PM","4:00 PM")
    val statusOptions = listOf("Activa", "Finalizada")

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(8.dp)) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = GolfGreen)

                // Name and Phone pre-fill
                ReadOnlyField("Nombre del Cliente", clientName)
                ReadOnlyField("Teléfono", clientPhone)

                // Date
                var showDatePicker by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Fecha (dd/MM/yyyy)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = null,
                                tint = GolfGreen)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GolfGreen, focusedLabelColor = GolfGreen)
                )
                if (showDatePicker) {
                    val dp = rememberDatePickerState()
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                dp.selectedDateMillis?.let { millis ->
                                    val sdf = java.text.SimpleDateFormat(
                                        "dd/MM/yyyy", java.util.Locale.getDefault())
                                    date = sdf.format(java.util.Date(millis))
                                }
                                showDatePicker = false
                            }) { Text("OK", color = GolfGreen) }
                        }
                    ) { DatePicker(state = dp) }
                }

                // Hour (dropdown)
                DropdownField("Hora", hour, hourOptions) { hour = it }

                // Number Court (dropdown)
                DropdownField("Número de Cancha", court, courtOptions) { court = it }

                // StateCourt (dropdown)
                if (reservation != null) {
                    DropdownField("Estado", status, statusOptions) { status = it }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { onSave(court, date, hour, status) },
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = GolfGreen)
                    ) {
                        if (isLoading)
                            CircularProgressIndicator(color = Color.White,
                                modifier = Modifier.size(18.dp))
                        else Text("Guardar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GolfGreen,
            disabledBorderColor = Color(0xFFBDBDBD),
            disabledTextColor = Color(0xFF424242)
        ),
        enabled = false
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String, selected: String,
    options: List<String>, onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GolfGreen, focusedLabelColor = GolfGreen)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(opt); expanded = false })
            }
        }
    }
}
