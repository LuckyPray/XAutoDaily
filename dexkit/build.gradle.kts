@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 32

    sourceSets {
        val main by getting
        main.apply {
            manifest.srcFile("DexKit/Android/dexkit/src/main/AndroidManifest.xml")
            java.setSrcDirs(listOf("DexKit/Android/dexkit/src/main/java"))
            res.setSrcDirs(listOf("DexKit/Android/dexkit/src/main/res"))
        }
    }
}