// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion by extra("1.6.10")
    val gradleVersion by extra("7.2.2")
    val gradlePluginVersion by extra("2.2.1")
    val gradleProtobuf by extra("0.8.18")
    val composeVersion by extra("1.2.0-alpha07")
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$gradleVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("com.google.protobuf:protobuf-gradle-plugin:$gradleProtobuf")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.bak files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://api.xposed.info")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
