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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import com.example.skycast.presentation.home.HomeScreen
import com.example.skycast.presentation.theme.*

@Composable
fun MainWeatherCard(
    temperature: String,
    city: String,
    description: String,
    highLow: String,
    iconCode: String?,
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
            if (iconCode != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://openweathermap.org/img/wn/${iconCode}@4x.png")
                        .crossfade(true)
                        .build(),
                    contentDescription = description,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(100.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = description,
                    tint = SkyBlue,
                    modifier = Modifier.size(80.dp)
                )
            }
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
    iconCode: String?,
    temperature: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = SurfaceWhite
    val textColor = TextPrimary
    val timeColor = TextSecondary
    val iconTint = SkyBlue

    Card(
        modifier = modifier.width(72.dp),
        shape = AppShapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
            if (iconCode != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("https://openweathermap.org/img/wn/${iconCode}@2x.png")
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(28.dp)
                )
            }
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
            iconCode = "01d",
            temperature = "${18 }°",
        )
    }
}

// This is for the 5-day forecast list. It lays items out horizontally.
@Composable
fun DailyForecastItem(
    day: String,
    iconCode: String?,
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
                if (iconCode != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://openweathermap.org/img/wn/${iconCode}@2x.png")
                            .crossfade(true)
                            .build(),
                        contentDescription = status,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = status,
                        tint = SkyBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = status,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
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
//        if (actionText != null) {
//            TextButton(onClick = onActionClick) {
//                Text(
//                    text = actionText,
//                    color = SkyBlue,
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.Medium
//                )
//            }
//        }
    }
}