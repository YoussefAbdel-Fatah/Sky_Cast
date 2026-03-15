# SkyCast ☀️🌧️

A modern Android weather application built with **Kotlin** and **Jetpack Compose** that displays real-time weather data based on your current location. You can also pick locations from a map, save favorites, and set weather alerts for rain, snow, wind and more.

---

## 📱 Screens

### Home Screen
- Current temperature, date & time
- Humidity, wind speed, pressure, clouds
- City name with dynamic weather icon
- Weather description (e.g. "clear sky", "light rain")
- Hourly forecast for the current day
- 5-day daily forecast
- Pull-to-refresh with offline cache support
- Offline banner when network is unavailable

### Settings Screen
- **Location method:** GPS or pick from map
- **Temperature units:** Celsius, Fahrenheit, Kelvin
- **Wind speed units:** meter/sec, miles/hour
- **Language:** English, Arabic

### Weather Alerts Screen
- Set alerts with a start/end time duration
- Choose between notification or alarm sound
- Option to enable/disable or delete alerts
- Background checking via WorkManager

### Favorites Screen
- List of saved favorite locations
- FAB to add new favorites via map or search
- Tap a favorite to see full forecast details
- Swipe or long press to remove

---

## 🏗️ Architecture

The project follows **MVVM (Model-View-ViewModel)** with a clean layered architecture:

```
com.example.skycast
├── data
│   ├── local          # Room Database, DAOs, Entities, DataStore
│   ├── remote         # Retrofit API services, Response models
│   ├── repository     # Repository pattern (single source of truth)
│   ├── model          # Data models (ForecastItem, City, etc.)
│   ├── location       # GPS location tracker
│   └── worker         # WorkManager for background alerts
├── presentation
│   ├── home           # Home screen (ViewModel, Screen, UiState)
│   ├── settings       # Settings screen
│   ├── favorites      # Favorites list + Details screen
│   ├── alerts         # Weather alerts screen
│   ├── map            # Map screen for location picking
│   ├── navigation     # Compose Navigation graph
│   ├── components     # Reusable UI components
│   └── theme          # Colors, Typography, Shapes
└── utils              # Constants, Resource sealed class, NetworkObserver
```

### Key Design Patterns
- **Repository Pattern** — single source of truth for weather data with cache-then-network strategy
- **Sealed Classes** — `HomeUiState` (Loading / Success / Error) for type-safe UI state management, `Resource` for network responses
- **Flow** — reactive data streams from Room DAOs through Repository to ViewModel
- **StateFlow** — exposes UI state from ViewModels to Compose
- **SharedFlow** — one-time events (Snackbar messages, error toasts)

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose + Material 3 |
| Architecture | MVVM |
| Networking | Retrofit 2 + OkHttp + Gson |
| Local Database | Room (with KSP) |
| Preferences | DataStore Preferences |
| Async / Reactive | Kotlin Coroutines + Flow / StateFlow / SharedFlow |
| Background Work | WorkManager |
| Location | Google Play Services Location |
| Maps | OpenStreetMap (osmdroid) |
| Image Loading | Coil for Compose |
| Navigation | Jetpack Compose Navigation |
| Unit Testing | JUnit 4 + MockK + Turbine + Coroutines Test |
| Instrumented Testing | AndroidX Test + Room Testing |

---

## 🌐 API

This app uses the [OpenWeatherMap 5-Day / 3-Hour Forecast API](https://openweathermap.org/forecast5):

```
https://api.openweathermap.org/data/2.5/forecast
```

**Parameters used:**
- `lat`, `lon` — coordinates (GPS or map selection)
- `q` — city name (search)
- `appid` — API key
- `units` — `metric` / `imperial` / `standard`
- `lang` — `en` / `ar`

> You need an API key from [OpenWeatherMap](https://openweathermap.org/api). Add it to your `local.properties`:
> ```
> WEATHER_API_KEY=your_api_key_here
> ```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or later
- Min SDK 24 (Android 7.0)
- An OpenWeatherMap API key

### Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/YoussefAbdel-Fatah/Sky_Cast.git
   ```
2. Open the project in Android Studio
3. Add your API key to `local.properties`:
   ```
   WEATHER_API_KEY=your_api_key_here
   ```
4. Build and run on a device or emulator

---

## 🧪 Testing

### Unit Tests
```bash
./gradlew testDebugUnitTest
```

Tests cover:
- **WeatherLocalDataSourceImplTest** — DAO ↔ DataSource Flow integration
- **FavoritesRepositoryTest** — Repository ↔ DAO layer
- **DetailsViewModelTest** — ViewModel state management with mocked repositories
- **SettingsViewModelTest** — Settings observation and updates

### Instrumented Tests
```bash
./gradlew connectedDebugAndroidTest
```

Tests cover:
- **FavoriteDaoTest** — Room DAO CRUD operations on a real database

---

## 📄 License

This project is developed as part of the **ITI (Information Technology Institute)** Android Development track using Kotlin.
