package com.example.skycast.presentation.alerts

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.data.local.entity.AlertEntity
import com.example.skycast.presentation.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(viewModel: AlertsViewModel) {
    val alerts by viewModel.alertsList.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Alerts", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }, containerColor = SkyBlue) {
                Icon(Icons.Default.Add, contentDescription = "Add Alert", tint = androidx.compose.ui.graphics.Color.White)
            }
        },
        containerColor = BackgroundLight
    ) { paddingValues ->

        if (alerts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No active alerts.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(alerts) { alert ->
                    AlertItemCard(
                        alert = alert,
                        onToggle = { isEnabled -> viewModel.toggleAlert(alert, isEnabled) },
                        onDelete = { viewModel.deleteAlert(alert) }
                    )
                }
            }
        }

        if (showDialog) {
            AddAlertDialog(
                onDismiss = { showDialog = false },
                onSave = { startH, startM, endH, endM, isAlarm ->
                    viewModel.saveAlert(startH, startM, endH, endM, isAlarm)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AlertItemCard(alert: AlertEntity, onToggle: (Boolean) -> Unit, onDelete: () -> Unit) {
    Card(
        shape = AppShapes.medium,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "${formatTime(alert.startHour, alert.startMinute)} - ${formatTime(alert.endHour, alert.endMinute)}",
                    fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary
                )
                Text(
                    text = if (alert.isAlarm) "Alarm Sound" else "Notification Only",
                    color = TextSecondary, fontSize = 14.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = alert.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(checkedThumbColor = SkyBlue, checkedTrackColor = SkyBlueLight)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AddAlertDialog(onDismiss: () -> Unit, onSave: (Int, Int, Int, Int, Boolean) -> Unit) {
    val context = LocalContext.current
    var startHour by remember { mutableStateOf(8) }
    var startMin by remember { mutableStateOf(0) }
    var endHour by remember { mutableStateOf(20) }
    var endMin by remember { mutableStateOf(0) }
    var isAlarm by remember { mutableStateOf(false) }

    fun pickTime(isStart: Boolean) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, h, m -> if (isStart) { startHour = h; startMin = m } else { endHour = h; endMin = m } },
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Alert Duration") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("From:")
                    TextButton(onClick = { pickTime(true) }) { Text(formatTime(startHour, startMin), color = SkyBlue, fontWeight = FontWeight.Bold) }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("To:")
                    TextButton(onClick = { pickTime(false) }) { Text(formatTime(endHour, endMin), color = SkyBlue, fontWeight = FontWeight.Bold) }
                }
                HorizontalDivider()
                Text("Alert Type", fontWeight = FontWeight.Medium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = !isAlarm, onClick = { isAlarm = false })
                    Text("Notification")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = isAlarm, onClick = { isAlarm = true })
                    Text("Alarm Sound")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(startHour, startMin, endHour, endMin, isAlarm) }) { Text("Save", color = SkyBlue) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// Helper function to format 24h time into readable 12h format
private fun formatTime(hour: Int, min: Int): String {
    val amPm = if (hour >= 12) "PM" else "AM"
    val formattedHour = if (hour % 12 == 0) 12 else hour % 12
    val formattedMin = min.toString().padStart(2, '0')
    return "$formattedHour:$formattedMin $amPm"
}