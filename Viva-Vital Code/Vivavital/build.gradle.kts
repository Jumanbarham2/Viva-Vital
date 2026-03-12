plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.vivavital"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.vivavital"
        minSdk = 24
        targetSdk = 34
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

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


    buildFeatures {
        viewBinding = true
    }

    packagingOptions {
        resources.excludes += setOf(
                "META-INF/*",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt"
        )
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")  // Downgrade core-ktx
    implementation("androidx.core:core:1.12.0")      // Downgrade core
    implementation("androidx.compose.ui:ui-text:1.5.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.collection:collection-ktx:1.4.0") // More stable version

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.2")) // Most stable version
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-inappmessaging-ktx") // Added -ktx

    // Google Play Services
    implementation("com.google.android.gms:play-services-auth:20.7.0") // Stable version

    // Third-party Libraries
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0") // Updated
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.airbnb.android:lottie:6.3.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Compose (if you need it)
    implementation("androidx.compose.ui:ui-text:1.6.0") // Updated from ui-text-android
}