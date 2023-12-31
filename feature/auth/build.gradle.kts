plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.lighthouse.auth"

    defaultConfig {
        buildConfigField("String", "CURRENT_VER", "\"${libs.versions.appVersion.get()}\"")
    }

    buildTypes {
        debug {
            consumerProguardFile("proguard-rules.pro")
        }

        release {
            consumerProguardFile("proguard-rules.pro")
        }
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":common-ui"))
    implementation(project(":navigation"))

    api(libs.bundles.androidx.ui.foundation)
    api(libs.bundles.android.basic.ui)
    api(libs.bundles.navigation)
    kapt(libs.hilt.kapt)
    implementation(libs.hilt)
    implementation(libs.google.login)
    implementation(libs.guava)
    implementation(libs.splash.screen)
    implementation(libs.firebase.config)
    implementation(libs.bundles.basic.test)
    implementation(libs.bundles.image)
    implementation(libs.bundles.camerax)
}