plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // We enable the 'kapt' plugin here for Room Database annotation processing
    id("kotlin-kapt")
}

android {
    namespace = "com.group10.carservicebook"
    // We set compileSdk to 36 because the latest androidx libraries require it
    compileSdk = 36

    defaultConfig {
        applicationId = "com.group10.carservicebook"
        minSdk = 24
        // We keep targetSdk at 34 (Android 14) for stability, but compile against 36
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room Database Components
    implementation("androidx.room:room-runtime:2.6.1")
    // We use kapt here (make sure the plugin 'id("kotlin-kapt")' is at the top)
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Lifecycle & Navigation Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
}