@file:Suppress("UnstableApiUsage")

import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.BuiltArtifact
import org.gradle.internal.os.OperatingSystem
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*

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
val signingPropFile = File(projectDir, "signing.properties")
val performSigning = signingPropFile.exists()

val appVerCode: Int get() {
    val versionCode = SimpleDateFormat("yyMMddHH", Locale.ENGLISH).format(Date())
    println("versionCode: $versionCode")
    return versionCode.toInt()
}
val buildNum: String get() = SimpleDateFormat("MMddHH", Locale.ENGLISH).format(Date())
val appVerName: String = "3.0.14"

android {
    namespace = "me.teble.xposed.autodaily"

    compileSdk = 33
    buildToolsVersion = "33.0.1"
    ndkVersion = "25.0.8775105"

    defaultConfig {
        applicationId = "me.teble.xposed.autodaily"
        minSdk = 24
        targetSdk = 33
        versionCode = appVerCode
        versionName = appVerName

        externalNativeBuild {
            cmake {
                targets("xa_native")
                abiFilters("armeabi-v7a", "arm64-v8a")
                arguments("-DANDROID_STL=c++_static")
                val flags = arrayOf(
                    "-Wall",
                    "-Qunused-arguments",
                    "-fno-rtti",
                    "-fvisibility=hidden",
                    "-fvisibility-inlines-hidden",
                    "-Wno-unused-value",
                    "-Wno-unused-variable",
                    "-Wno-unused-command-line-argument",
                    "-DMMKV_DISABLE_CRYPT",
                )
                cppFlags("-std=c++17", *flags)
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
            if (performSigning) {
                val properties = Properties().apply {
                    load(signingPropFile.reader())
                }
                storeFile = File(properties.getProperty("storeFilePath"))
                storePassword = properties.getProperty("storePassword")
                keyAlias = properties.getProperty("keyAlias")
                keyPassword = properties.getProperty("keyPassword")
            }
        }
    }

    buildFeatures {
        prefab = true
    }

    buildTypes {
        all {
            signingConfig =
                if (performSigning) signingConfigs.getByName("config")
                else signingConfigs.getByName("debug")
        }
        val debug by getting {
            versionNameSuffix = ".$buildNum-debug"
            val debugFlags = arrayOf<String>(
//                "-DMODULE_SIGNATURE=E7A8AEB0A1431D12EB04BF1B7FC31960",
//                "-DTEST_SIGNATURE",
            )
            externalNativeBuild {
                cmake {
                    arguments.addAll(
                        arrayOf(
                            "-DCMAKE_CXX_FLAGS_DEBUG=-Og",
                            "-DCMAKE_C_FLAGS_DEBUG=-Og",
                        )
                    )
                    cFlags += debugFlags
                    cppFlags += debugFlags
                }
            }
        }
        val release by getting  {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
            externalNativeBuild {
                cmake {
                    val releaseFlags = arrayOf<String>(
                        "-ffunction-sections",
                        "-fdata-sections",
                        "-Wl,--gc-sections",
                        "-Oz",
                        "-Wl,--exclude-libs,ALL",
                        "-flto=full",
                    )
                    cppFlags += releaseFlags
                    cFlags += releaseFlags
                    val configFlags = arrayOf(
                        "-Oz",
                        "-DNDEBUG",
                        "-DMODULE_SIGNATURE=FF9FF61037FF85BEDDBA5C98A3CB7600"
                    ).joinToString(" ")
                    arguments(
                        "-DCMAKE_BUILD_TYPE=Release",
                        "-DCMAKE_CXX_FLAGS_RELEASE=$configFlags",
                        "-DCMAKE_C_FLAGS_RELEASE=$configFlags",
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
    packagingOptions {
        jniLibs.excludes += arrayOf("lib/**/liblog.so", "lib/**/libz.so")
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
    implementation(project(":dexkit"))
    implementation(project(":mmkv"))
    compileOnly(project(":stub"))

    compileOnly("de.robv.android.xposed:api:82")
    implementation("com.github.kyuubiran:EzXHelper:1.0.3")

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
    implementation("dev.rikka.shizuku:api:12.2.0")
    implementation("dev.rikka.shizuku:provider:12.2.0")

//    implementation("io.luckypray:dexkit:1.0.0")
//    implementation("com.github.LuckyPray:DexKit:1.0.0")
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
