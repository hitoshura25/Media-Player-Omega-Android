import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.4.0" // TODO: Extract to this property?
}

kotlin {
    android()
    ios {
        binaries {
            framework {
                baseName = "home-domain"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":navigation-domain"))
                implementation(project(":player-domain"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${rootProject.ext["kotlinSerializationVersion"]}")
            }
        }
        val androidMain by getting
        val iosMain by getting
    }
}

android {
    compileSdkVersion(rootProject.ext["targetSDKVersion"] as Int)
    defaultConfig {
        minSdkVersion(rootProject.ext["minSDKVersion"] as Int)
        targetSdkVersion(rootProject.ext["targetSDKVersion"] as Int)
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
    val framework =
        kotlin.targets.getByName<KotlinNativeTarget>(targetName).binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}
tasks.getByName("build").dependsOn(packForXcode)