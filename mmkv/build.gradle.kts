@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.tencent.mmkv"
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.buildTool.get()
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()


        buildConfigField("String", "FLAVOR", "\"StaticCpp\"")
    }

    sourceSets {
        val main by getting
        main.apply {
            manifest.srcFile("MMKV/Android/MMKV/mmkv/src/main/AndroidManifest.xml")
            java.setSrcDirs(listOf("MMKV/Android/MMKV/mmkv/src/main/java"))
            aidl.setSrcDirs(listOf("MMKV/Android/MMKV/mmkv/src/main/aidl"))
            res.setSrcDirs(listOf("MMKV/Android/MMKV/mmkv/src/main/res"))
            assets.setSrcDirs(listOf("MMKV/Android/MMKV/mmkv/src/main/assets"))
        }
    }
}

dependencies {
    compileOnly(libs.androidx.annotation)
}