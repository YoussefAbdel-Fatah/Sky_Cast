package com.example.skycast.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skycast.presentation.home.HomeScreen
import com.example.skycast.presentation.theme.*

@Composable
fun MainWeatherCard(
    temperature: String,
    city: String,
    description: String,
    highLow: String,
    icon: Painter,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = temperature,
                    style = AppTypography.displayLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = city,
                    style = AppTypography.headlineMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = AppTypography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = highLow,
                    style = AppTypography.labelSmall.copy(fontSize = 12.sp)
                )
            }
            Icon(
                painter = icon,
                contentDescription = description,
                tint = SkyBlue,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun WeatherDetailsCard(
    icon: Painter,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.medium,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                painter = icon,
                contentDescription = label,
                tint = SkyBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label.uppercase(),
                style = AppTypography.labelSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            )
        }
    }
}

// Notice how the "12 PM" item is highlighted in blue? We will pass an isActive boolean to handle that color change dynamically.
@Composable
fun HourlyForecastItem(
    time: String,
    icon: Painter,
    temperature: String,
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isActive) SkyBlue else SurfaceWhite
    val textColor = if (isActive) SurfaceWhite else TextPrimary
    val timeColor = if (isActive) SurfaceWhite else TextSecondary
    val iconTint = if (isActive) SurfaceWhite else SkyBlue

    Card(
        modifier = modifier.width(72.dp),
        shape = AppShapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = time,
                fontSize = 12.sp,
                color = timeColor,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Icon(
                painter = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = temperature,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherPreview() {
    // 1. Wrap the UI in your custom theme
    WeatherAppTheme {
        // 2. Call the composable you want to see
        HourlyForecastItem(
            time = "${10 } AM",
            icon = painterResource(id = android.R.drawable.ic_menu_gallery),
            temperature = "${18 }°",
            isActive = false // Highlights the 3rd item
        )
    }
}

// This is for the 5-day forecast list. It lays items out horizontally.
@Composable
fun DailyForecastItem(
    day: String,
    icon: Painter,
    status: String,
    highTemp: String,
    lowTemp: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = AppShapes.medium,
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = day,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1.2f)
            ) {
                Icon(
                    painter = icon,
                    contentDescription = status,
                    tint = SkyBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = status,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(0.8f)
            ) {
                Text(
                    text = highTemp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lowTemp,
                    fontSize = 16.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

// A simple reusable row for titles like "Hourly Forecast" and "5-Day Forecast".
@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AppTypography.headlineMedium
        )
        if (actionText != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    color = SkyBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}