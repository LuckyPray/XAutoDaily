@file:Suppress("UnstableApiUsage")

import android.annotation.SuppressLint
import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.BuiltArtifact
import java.nio.file.Paths
import org.gradle.internal.os.OperatingSystem

fun findInPath(executable: String): String? {
    val pathEnv = System.getenv("PATH")
    return pathEnv.split(File.pathSeparator).map { folder ->
        Paths.get("${folder}${File.separator}${executable}${if (OperatingSystem.current().isWindows) ".exe" else ""}")
            .toFile()
    }.firstOrNull { path ->
        path.exists()
    }?.absolutePath
}

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlinx-serialization")
}

val releaseStoreFile: String? by rootProject
val releaseStorePassword: String? by rootProject
val releaseKeyAlias: String? by rootProject
val releaseKeyPassword: String? by rootProject

val appVerCode: String = "22090611"
val appVerName: String = "3.0.8"

android {
    namespace = "me.teble.xposed.autodaily"

    compileSdk = 31
    buildToolsVersion = "32.0.0"
    ndkVersion = "25.0.8775105"

    defaultConfig {
        applicationId = "me.teble.xposed.autodaily"
        minSdk = 24
        @SuppressLint("OldTargetApi")
        targetSdk = 31
        versionCode = appVerCode.toInt()
        versionName = appVerName

        externalNativeBuild {
            cmake {
                targets("xa_native")
                abiFilters("armeabi-v7a", "arm64-v8a")
//                arguments("-DANDROID_STL=c++_static")
                arguments("-DANDROID_STL=none")
                val flags = arrayOf(
                    "-Wall",
//                    "-Werror",
                    "-Qunused-arguments",
                    "-Wno-gnu-string-literal-operator-template",
                    "-fno-rtti",
                    "-fvisibility=hidden",
                    "-fvisibility-inlines-hidden",
                    "-fno-exceptions",
                    "-fno-stack-protector",
                    "-fomit-frame-pointer",
                    "-Wno-builtin-macro-redefined",
                    "-Wno-unused-value",
                    "-Wno-unused-variable",
                    "-D__FILE__=__FILE_NAME__",
                    "-Dthrow='abort();'",
//                    if (performSigning) {
//                        // release 签名
//                        "-DMODULE_SIGNATURE=FF9FF61037FF85BEDDBA5C98A3CB7600"
//                    } else {
                        // android 默认 debug md5 签名可通过 signingReport 获取
                        "-DMODULE_SIGNATURE=CB49EE96102F0D9C331AB59A5921AA42"
//                    }
                )
                cppFlags("-std=c++20", *flags)
                cFlags("-std=c18", *flags)
                findInPath("ccache")?.let {
                    println("Using ccache $it")
                    arguments += "-DANDROID_CCACHE=$it"
                }
            }
        }
    }

    signingConfigs {
        create("config") {
            releaseStoreFile?.also {
                storeFile = rootProject.file(it)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildFeatures {
        prefab = true
    }

    androidResources {
        noCompress("libxa_native.so")
    }

    buildTypes {
        all {
            signingConfig =
                if (releaseStoreFile.isNullOrEmpty()) signingConfigs.getByName("debug")
                else signingConfigs.getByName("config")
        }
        val debug by getting {
            externalNativeBuild {
                cmake {
                    arguments.addAll(
                        arrayOf(
                            "-DCMAKE_CXX_FLAGS_DEBUG=-Og",
                            "-DCMAKE_C_FLAGS_DEBUG=-Og",
                        )
                    )
                }
            }
        }
        val alpha by creating  {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles("proguard-rules.pro")
            externalNativeBuild {
                cmake {
                    arguments.addAll(
                        arrayOf(
                            "-DCMAKE_CXX_FLAGS_DEBUG=-Og",
                            "-DCMAKE_C_FLAGS_DEBUG=-Og",
                        )
                    )
                }
            }
        }
        val rc by creating {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
            externalNativeBuild {
                cmake {
                    val flags = arrayOf(
                        "-flto",
                        "-ffunction-sections",
                        "-fdata-sections",
                        "-Wl,--gc-sections",
                        "-fno-unwind-tables",
                        "-fno-asynchronous-unwind-tables",
                        "-Wl,--exclude-libs,ALL",
                    )
                    cppFlags.addAll(flags)
                    cFlags.addAll(flags)
                    val configFlags = arrayOf(
                        "-Oz",
                        "-DNDEBUG"
                    ).joinToString(" ")
                    arguments(
                        "-DCMAKE_BUILD_TYPE=Release",
                        "-DCMAKE_CXX_FLAGS_RELEASE=$configFlags",
                        "-DCMAKE_C_FLAGS_RELEASE=$configFlags",
                        "-DDEBUG_SYMBOLS_PATH=${project.buildDir.absolutePath}/symbols/$name",
                    )
                }
            }
        }
        val release by getting  {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles("proguard-rules.pro")
            externalNativeBuild {
                cmake {
                    val flags = arrayOf(
                        "-flto",
                        "-ffunction-sections",
                        "-fdata-sections",
                        "-Wl,--gc-sections",
                        "-fno-unwind-tables",
                        "-fno-asynchronous-unwind-tables",
                        "-Wl,--exclude-libs,ALL",
                    )
                    cppFlags.addAll(flags)
                    cFlags.addAll(flags)
                    val configFlags = arrayOf(
                        "-Oz",
                        "-DNDEBUG"
                    ).joinToString(" ")
                    arguments(
                        "-DCMAKE_BUILD_TYPE=Release",
                        "-DCMAKE_CXX_FLAGS_RELEASE=$configFlags",
                        "-DCMAKE_C_FLAGS_RELEASE=$configFlags",
                        "-DDEBUG_SYMBOLS_PATH=${project.buildDir.absolutePath}/symbols/$name",
                    )
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf(
            "-Xuse-k2",
            "-Xno-param-assertions",
            "-Xno-call-assertions",
            "-Xno-receiver-assertions",
            "-opt-in=kotlin.RequiresOptIn",
        )
    }

    packagingOptions {
        resources {
            excludes += arrayOf("**")
        }
    }


    lint {
        abortOnError = false
    }

    dependenciesInfo {
        includeInApk = false
    }
    androidResources {
        additionalParameters += arrayOf("--allow-reserved-package-id", "--package-id", "0x62")
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = rootProject.extra["composeVersion"] as String
    }

    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1+"
        }
    }
}

abstract class CopyApksTask : DefaultTask() {
    @get:Internal
    abstract val transformer: Property<(input: BuiltArtifact) -> File>

    @get:InputDirectory
    abstract val apkFolder: DirectoryProperty

    @get:OutputDirectory
    abstract val outFolder: DirectoryProperty

    @get:Internal
    abstract val transformationRequest: Property<ArtifactTransformationRequest<CopyApksTask>>

    @TaskAction
    fun taskAction() = transformationRequest.get().submit(this) { builtArtifact ->
        File(builtArtifact.outputFile).copyTo(transformer.get()(builtArtifact), true)
    }
}

androidComponents.onVariants { variant ->
    if (variant.name != "release") return@onVariants
    val updateArtifact = project.tasks.register<CopyApksTask>("copy${variant.name.capitalize()}Apk")
    val transformationRequest = variant.artifacts.use(updateArtifact)
        .wiredWithDirectories(CopyApksTask::apkFolder, CopyApksTask::outFolder)
        .toTransformMany(SingleArtifact.APK)
    updateArtifact.configure {
        this.transformationRequest.set(transformationRequest)
        transformer.set { builtArtifact ->
            File(projectDir, "${variant.name}/XAutoDaily_${builtArtifact.versionName}.apk")
        }
    }
}

configurations.all {
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
}

dependencies {
    implementation(project(":mmkv"))
    compileOnly(project(":stub"))

    compileOnly("de.robv.android.xposed:api:82")
    implementation("com.github.kyuubiran:EzXHelper:1.0.1")

    implementation("com.google.protobuf:protobuf-kotlin-lite:3.21.2")
    compileOnly("com.google.protobuf:protoc:3.21.2")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")

    // jetpack compose
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.compose.ui:ui:${rootProject.extra["composeVersion"]}")
    implementation("androidx.compose.ui:ui-tooling:${rootProject.extra["composeVersion"]}")
    implementation("androidx.compose.material:material:${rootProject.extra["composeVersion"]}")
    implementation("androidx.navigation:navigation-compose:2.5.0-alpha04")
    implementation("cn.hutool:hutool-core:5.8.0.M1")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    // Other
    implementation("com.hankcs:aho-corasick-double-array-trie:1.2.3")
    implementation("net.bytebuddy:byte-buddy-android:1.12.7")
    implementation("com.charleskorn.kaml:kaml:0.36.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    // implementation ("org.apache-extras.beanshell:bsh:2.0b6")
    // shizuku
    implementation("dev.rikka.shizuku:api:12.1.0")
    implementation("dev.rikka.shizuku:provider:12.1.0")
    implementation("dev.rikka.ndk.thirdparty:cxx:1.2.0")
}

val adbExecutable: String = androidComponents.sdkComponents.adb.get().asFile.absolutePath

val restartQQ = task("restartQQ").doLast {
    exec {
        commandLine(adbExecutable, "shell", "am", "force-stop", "com.tencent.mobileqq")
    }
    exec {
        commandLine(
            adbExecutable, "shell", "am", "start",
            "$(pm resolve-activity --components com.tencent.mobileqq)"
        )
    }
}

val optimizeReleaseRes = task("optimizeReleaseRes").doLast {
    val aapt2 = Paths.get(
        project.android.sdkDirectory.path,
        "build-tools", project.android.buildToolsVersion, "aapt2"
    )
    val zip = Paths.get(
        project.buildDir.path, "intermediates",
        "optimized_processed_res", "release", "resources-release-optimize.ap_"
    )
    val optimized = File("${zip}.opt")
    val cmd = exec {
        commandLine(aapt2, "optimize", "--collapse-resource-names", "-o", optimized, zip)
        isIgnoreExitValue = true
    }
    if (cmd.exitValue == 0) {
        delete(zip)
        optimized.renameTo(zip.toFile())
    }
}
tasks.whenTaskAdded {
    when (name) {
        "optimizeReleaseResources" -> {
            finalizedBy(optimizeReleaseRes)
        }
        "installDebug" -> {
            finalizedBy(restartQQ)
        }
    }
}
