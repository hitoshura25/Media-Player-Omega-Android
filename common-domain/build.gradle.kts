plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    ios()
}

android {
    compileSdkVersion(rootProject.ext["targetSDKVersion"] as Int)
    defaultConfig {
        minSdkVersion(rootProject.ext["minSDKVersion"] as Int)
        targetSdkVersion(rootProject.ext["targetSDKVersion"] as Int)
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}