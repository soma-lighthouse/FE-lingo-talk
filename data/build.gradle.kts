import org.jetbrains.kotlin.konan.properties.Properties

val lighthouseFile = rootProject.file("lighthouse.properties")
val properties = Properties()
properties.load(lighthouseFile.inputStream())

plugins {
    kotlin("android")
    id("com.android.library")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.lighthouse.android.data"

    defaultConfig {
        buildConfigField(
            "String",
            "LIGHTHOUSE_BASE_URL",
            properties.getProperty("LIGHTHOUSE_BASE_URL")
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.hilt)
    implementation("com.google.firebase:protolite-well-known-types:18.0.0")
    kapt(libs.hilt.kapt)
    kapt(libs.room.complier)
    implementation(libs.bundles.basic.test)
    implementation(libs.bundles.room)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.okhttp)
    implementation(libs.bundles.gson)
}