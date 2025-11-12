plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
}

android {
    namespace = "com.example.smartplanner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.smartplanner"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // keep fast logs
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions { jvmTarget = "17" }
    kotlin { jvmToolchain(17) }

    buildFeatures { viewBinding = true }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Retrofit + OkHttp + Moshi
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // Firebase via BOM
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx") // FCM

    // Google Sign-In for SSO
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Security (encrypted prefs)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Room (with KSP) — already present; used for Weather + Tasks offline
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Desugaring for java.time on API < 26
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}
