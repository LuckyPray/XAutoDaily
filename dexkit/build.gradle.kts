@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "org.luckypray.dexkit"
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.buildTool.get()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    kotlin {
        jvmToolchain(libs.versions.jvm.target.get().toInt())
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
    implementation(libs.flatbuffers.java)
}