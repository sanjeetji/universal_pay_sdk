import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("native.cocoapods")
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    val xcf = XCFramework()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            xcf.add(this)
        }
    }

    cocoapods {
        summary = "Shared code for UniversalpaySdk"
        homepage = "Link to your project homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"

        framework {
            baseName = "shared"
        }

        val podfileFile = project.file("../iosApp/Podfile")
        if (podfileFile.exists()) {
            podfile = podfileFile
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            // Compose Activity
            implementation(libs.androidx.activity.compose)

            // Compose UI
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.ui.graphics)
            implementation(libs.androidx.compose.ui.tooling.preview)

            // Compose Material3
            implementation(libs.androidx.compose.material3)

            // Material Icons - BOTH REQUIRED
            implementation(libs.androidx.compose.material.icons)
            implementation(libs.androidx.compose.material.icons.extended)

            // Core Android
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.lifecycle.runtime.ktx)

            // Security Crypto
            implementation(libs.androidx.security.crypto)

            // JSON
            implementation(libs.gson)

            // Image Loading
            implementation(libs.coil.compose)

            // Coroutines
            implementation(libs.kotlinx.coroutines.android)

            // Third-party
            implementation(libs.razorpay.checkout)
        }

        iosMain.dependencies {
            // iOS-specific dependencies if needed
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.sdk.universalpay"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
