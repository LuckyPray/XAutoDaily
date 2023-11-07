@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
}

android {
    namespace = "com.tencent.mmkv"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        minSdk = 24

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

    dependencies {
        compileOnly("androidx.annotation:annotation:1.4.0")
    }
}