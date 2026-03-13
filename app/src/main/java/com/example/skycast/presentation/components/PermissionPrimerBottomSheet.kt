package com.example.skycast.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.presentation.theme.SkyBlue
import com.example.skycast.presentation.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionPrimerBottomSheet(
    onGrantClicked: () -> Unit,
    onManualClicked: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onManualClicked,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                modifier = Modifier.size(64.dp),
                tint = SkyBlue
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Accurate Forecasts & Alerts",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "SkyCast uses your location to provide hyper-local weather conditions and extreme weather alerts directly to your device.",
                textAlign = TextAlign.Center,
                color = TextSecondary,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onGrantClicked,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkyBlue)
            ) {
                Text("Grant Location Permission", fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                onClick = onManualClicked,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enter City Manually", color = TextSecondary)
            }
        }
    }
}