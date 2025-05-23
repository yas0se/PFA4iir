plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.pfa4iir"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pfa4iir"
        minSdk = 28
        targetSdk = 35
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Web scraping
    implementation(libs.jsoup)          // Jsoup for HTML parsing
    implementation(libs.okhttp)         // OkHttp for networking
    implementation(libs.swiperefreshlayout)

    // UI
    implementation(libs.swiperefreshlayout) // Pull-to-refresh

    // Image loading (Java-compatible)
    implementation(libs.glide) {
        exclude(group = "com.android.support")
    }
    annotationProcessor(libs.glide.compiler) // Required for Glide
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("com.google.android.gms:play-services-maps:19.2.0")
    implementation("org.json:json:20250107")
}