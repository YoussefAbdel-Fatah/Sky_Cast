package com.example.skycast.presentation.alerts

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import com.example.skycast.data.local.entity.AlertEntity
import com.example.skycast.presentation.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(viewModel: AlertsViewModel) {
    val context = LocalContext.current
    val alerts by viewModel.alertsList.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    // Task 2: State for the delete confirmation dialog
    var itemToDelete by remember { mutableStateOf<AlertEntity?>(null) }

    // Task 4: Permission Launcher for Notifications
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showAddDialog = true
        } else {
            Toast.makeText(context, "Notification permission is required to create alerts.", Toast.LENGTH_SHORT).show()
        }
    }

    // Task 2: The Delete Confirmation Dialog
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Alert") },
            text = { Text("Are you sure you want to remove this weather alert?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAlert(itemToDelete!!)
                    itemToDelete = null
                }) {
                    Text("Yes", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("No", color = SkyBlue) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Alerts", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Task 4: Check permission before showing the Add Dialog
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            showAddDialog = true
                        } else {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    } else {
                        showAddDialog = true // Below Android 13, permission is granted at install time
                    }
                },
                containerColor = SkyBlue
            ) {
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
                        onDelete = { itemToDelete = alert } // Trigger dialog instead of immediate deletion
                    )
                }
            }
        }

        if (showAddDialog) {
            AddAlertDialog(
                onDismiss = { showAddDialog = false },
                onSave = { startH, startM, endH, endM, isAlarm ->
                    viewModel.saveAlert(startH, startM, endH, endM, isAlarm)
                    showAddDialog = false
                }
            )
        }
    }
}

// Keep AlertItemCard exactly as it was...
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

    // Set default times to current time + 1 hour to ensure it's in the future by default
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.HOUR_OF_DAY, 1)

    var startHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var startMin by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }

    calendar.add(Calendar.HOUR_OF_DAY, 2) // Default end time 2 hours after start
    var endHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var endMin by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }

    var isAlarm by remember { mutableStateOf(false) }

    fun pickTime(isStart: Boolean) {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, h, m -> if (isStart) { startHour = h; startMin = m } else { endHour = h; endMin = m } },
            cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false
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
            TextButton(onClick = {
                // Task 5: Validate that start time is in the future
                val now = Calendar.getInstance()
                val currentHour = now.get(Calendar.HOUR_OF_DAY)
                val currentMin = now.get(Calendar.MINUTE)

                val startTotalMinutes = startHour * 60 + startMin
                val currentTotalMinutes = currentHour * 60 + currentMin
                val endTotalMinutes = endHour * 60 + endMin

                if (startTotalMinutes <= currentTotalMinutes || endTotalMinutes <= currentTotalMinutes) {
                    Toast.makeText(context, "The duration must be in the future!", Toast.LENGTH_SHORT).show()
                    return@TextButton
                }

                if (startTotalMinutes >= endTotalMinutes) {
                    Toast.makeText(context, "Start time must be before end time.", Toast.LENGTH_SHORT).show()
                    return@TextButton
                }

                // If endTotalMinutes < startTotalMinutes, it naturally implies it crosses midnight (which is perfectly valid!)

                onSave(startHour, startMin, endHour, endMin, isAlarm)
            }) { Text("Save", color = SkyBlue) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

private fun formatTime(hour: Int, min: Int): String {
    val amPm = if (hour >= 12) "PM" else "AM"
    val formattedHour = if (hour % 12 == 0) 12 else hour % 12
    val formattedMin = min.toString().padStart(2, '0')
    return "$formattedHour:$formattedMin $amPm"
}