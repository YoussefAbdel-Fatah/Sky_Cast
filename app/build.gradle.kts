plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") // Apply it here
}

android {
    namespace = "com.example.skycast"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.skycast"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

ksp {
    arg("room.generateKotlin", "true")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // 1. Compose Navigation (To move between Home, Settings, Favorites, etc.)
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // 2. Lifecycle & ViewModel (To manage data during screen rotations)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")

    // 3. Retrofit & OkHttp (To fetch weather from OpenWeatherMap API)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0") // Parses JSON
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Helps debug network calls

    // 4. Room Database (To save favorite locations locally)
    val room_version = "2.7.0-alpha11"
    implementation ("androidx.room:room-ktx:${room_version}")
    implementation("androidx.room:room-runtime:${room_version}")
    ksp("androidx.room:room-compiler:$room_version")

    // 5. Coroutines (For background tasks like network calls and database queries)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // 6. WorkManager (To schedule background tasks for your Rain/Temperature Alerts)
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // 7. Google Play Services Location (To get the user's GPS location)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // 8. DataStore
    // Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    //
    // 9.OpenStreetMap library
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // Unit Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("app.cash.turbine:turbine:1.0.0") // Makes testing Flows incredibly easy

    // Android Testing (for the Room DAO)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
}
