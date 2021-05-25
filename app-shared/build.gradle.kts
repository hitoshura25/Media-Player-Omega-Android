import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    ios {
        binaries {
            framework {
                baseName = "app-shared"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common-domain"))
                implementation(project(":navigation-domain"))
                implementation(project(":home-domain"))

                implementation(project(":my-library-domain"))
                implementation(project(":my-library-data"))
                implementation(project(":my-library-usecases"))

                implementation(project(":search-domain"))
                implementation(project(":search-data"))
                implementation(project(":search-usecases"))

                implementation(project(":player-domain"))

                implementation(project(":downloads-domain"))
                implementation(project(":downloads-data"))
                implementation(project(":downloads-usecases"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13")
            }
        }
        val iosMain by getting
        val iosTest by getting
    }
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
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