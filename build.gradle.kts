// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion by extra("1.9.20")
    val gradleVersion by extra("7.4.2")
    val gradlePluginVersion by extra("2.2.1")
    val composeVersion by extra("1.6.0-alpha08")
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        mavenLocal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$gradleVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://api.xposed.info")
        mavenLocal()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
