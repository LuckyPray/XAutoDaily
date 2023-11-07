@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "org.luckypray.dexkit"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    kotlin {
        jvmToolchain(8)
    }

    sourceSets {
        val main by getting
        main.apply {
            manifest.srcFile("DexKit/dexkit-android/src/main/AndroidManifest.xml")
            java.setSrcDirs(listOf("DexKit/dexkit/src/main/java"))
            res.setSrcDirs(listOf("DexKit/dexkit/src/main/res"))
        }
    }
}

dependencies {
    implementation("com.google.flatbuffers:flatbuffers-java:23.5.26")
}