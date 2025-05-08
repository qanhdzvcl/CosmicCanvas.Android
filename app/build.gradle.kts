plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
}
hilt {
    enableAggregatingTask = false
}
android {
    namespace = "com.emrepbu.cosmiccanvas"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.emrepbu.cosmiccanvas"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "NASA_API_KEY", "\"DEMO_KEY\"") // Replace with your NASA API key
        buildConfigField("String", "NASA_BASE_URL", "\"https://api.nasa.gov/\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    
    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    
    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.appcompat)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    // Use the latest version of the AndroidX Hilt compiler
    ksp("androidx.hilt:hilt-compiler:1.2.0")
    
    // Fix for JavaPoet compatibility issue
    implementation("com.squareup:javapoet:1.13.0")
    // Force all Hilt annotation processors to use this version of JavaPoet
    configurations.all {
        resolutionStrategy.force("com.squareup:javapoet:1.13.0")
    }
    
    // Retrofit + Networking
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    
    // Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    
    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    
    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    
    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    
    // Splash Screen
    implementation(libs.androidx.core.splashscreen)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.work.testing)
    
    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}