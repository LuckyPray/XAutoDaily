@file:Suppress("UnstableApiUsage")

import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.BuiltArtifact
import org.gradle.internal.os.OperatingSystem
import java.io.ByteArrayOutputStream
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.Properties

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
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.serialization)
    alias(libs.plugins.licenses)
    id("kotlin-parcelize")
}
val signingPropFile = File(projectDir, "signing.properties")
val performSigning = signingPropFile.exists()

val appVerCode: Int by lazy {
    val versionCode = SimpleDateFormat("yyMMddHH", Locale.ENGLISH).format(Date())
    println("versionCode: $versionCode")
    versionCode.toInt()
}
val buildNum: String get() = SimpleDateFormat("MMddHH", Locale.ENGLISH).format(Date())
val appVerName: String = "3.0.23-fix"
val updateLog = """
    1. 修复模块在 QQ 9.0.8 上加载异常的问题
    2. 更新内置配置版本为 v44
    fix. 修复模块在 QQ 8.9.68 上加载异常的问题
""".trimIndent()

// 执行 gradle licenseReleaseReport
licenses {
    additionalProjects(":dexkit", ":mmkv", ":stub", ":system")
    reports {
        html.enabled = false
        json {
            enabled = true
            destination = file("./src/main/assets/licenses.json")
        }
    }
}

android {
    namespace = "me.teble.xposed.autodaily"
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.buildTool.get()
    ndkVersion = libs.versions.ndk.get()

    defaultConfig {
        applicationId = "me.teble.xposed.autodaily"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = appVerCode
        versionName = appVerName

        buildFeatures {
            buildConfig = true
            aidl = true
        }

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
            val debugFlags = arrayOf(
                "-DMODULE_SIGNATURE=E7A8AEB0A1431D12EB04BF1B7FC31960",
//                "-DTEST_SIGNATURE",
            )
            externalNativeBuild {
                cmake {
                    cppFlags += debugFlags
                    cFlags += debugFlags
                    arguments(
                        "-DCMAKE_CXX_FLAGS_DEBUG=-Og",
                        "-DCMAKE_C_FLAGS_DEBUG=-Og",
                    )
                }
            }
        }
        val release by getting {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
            externalNativeBuild {
                cmake {
                    val releaseFlags = arrayOf(
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
        sourceCompatibility(JavaVersion.VERSION_21)
        targetCompatibility(JavaVersion.VERSION_21)
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xno-param-assertions",
            "-Xno-call-assertions",
            "-Xno-receiver-assertions",
            "-opt-in=kotlin.RequiresOptIn",
        )
    }

    kotlin {
        jvmToolchain(libs.versions.jvm.target.get().toInt())
        sourceSets.all {
            languageSettings {
                enableLanguageFeature("ContextReceivers")
            }
        }
    }

    packaging {
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
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1+"
        }
    }
    packaging {
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
    val updateArtifact = project.tasks.register<CopyApksTask>(
        "copy${
            variant.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }
        }Apk"
    )
    val transformationRequest = variant.artifacts.use(updateArtifact)
        .wiredWithDirectories(CopyApksTask::apkFolder, CopyApksTask::outFolder)
        .toTransformMany(SingleArtifact.APK)
    updateArtifact.configure {
        this.transformationRequest.set(transformationRequest)
        transformer.set { builtArtifact ->
            File(projectDir, "${variant.name}/XAutoDaily_${builtArtifact.versionName}.apk")
        }
    }
    // update version.json
    findInPath("git")?.let {
        val stdout = ByteArrayOutputStream()
        val appGradlePath = "app/build.gradle.kts"
        val cmd = exec {
            workingDir = project.rootDir

            val cmdLine = if (OperatingSystem.current().isWindows) {
                arrayOf(
                    "cmd",
                    "/c",
                    "for /f usebackq^ tokens^=2^ delims^=^\" %G in (`git diff $appGradlePath ^| findstr /r /c:\"^+val[ ]appVerName\"`) do @echo %G"
                )
            } else {
                arrayOf(
                    "sh",
                    "-c",
                    "git diff $appGradlePath | grep -e '^+val appVerName' | awk -F '\"' '{print \$2}'"
                )
            }
            commandLine(*cmdLine)
            standardOutput = stdout
            // 没有更新版本号，会返回非0导致构建失败
            isIgnoreExitValue = true
        }
        if (cmd.exitValue == 0 && stdout.size() > 0) {
            println("version be updated to ${stdout.toString().trim()}")
            val versionJson = File(project.rootDir, "app-meta.json")
            val formatTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .format(LocalDateTime.now())
            versionJson.writeText(
                """
                {
                    "versionName": "$appVerName",
                    "versionCode": $appVerCode,
                    "updateTime": "$formatTime",
                    "updateLog": "${updateLog.replace("\n", "\\n")}"
                }
            """.trimIndent()
            )
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
    implementation(project(":system"))

    implementation(libs.androidx.multidex)

    compileOnly(libs.api)
    implementation(libs.ezxhelper)

    implementation(libs.protobuf.kotlin.lite)
    compileOnly(libs.protoc)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.kotlinx.datetime)


    implementation(libs.androidx.browser)
    // jetpack compose
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.animation)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.runtime)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.materialWindow)

    implementation(libs.androidx.material3.adaptive)
    implementation(libs.compose.shadows.plus)
    implementation("com.slack.circuit:circuit-foundation:0.19.1")
    implementation("com.slack.circuit:circuitx-gesture-navigation:0.19.1")
    implementation("com.slack.circuit:circuitx-overlays:0.19.1")
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // ViewModel utilities for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.hutool.core)
    implementation(libs.okhttp)
    // Other
    implementation(libs.aho.corasick.double.array.trie)
    implementation(libs.byte.buddy.android)
    implementation(libs.kaml)
    // implementation ("org.apache-extras.beanshell:bsh:2.0b6")
    // shizuku
    implementation(libs.shizuku.api)
    implementation(libs.provider)
}

val adbExecutable: String = androidComponents.sdkComponents.adb.get().asFile.absolutePath


fun killApp(name: String) {
    exec {
        commandLine(adbExecutable, "shell", "am", "force-stop", name)
    }
}

fun startApp(name: String) {
    exec {
        commandLine(
            adbExecutable, "shell", "am", "start",
            "$(pm resolve-activity --components $name)"
        )
    }
}

fun restartApp(name: String) {
    killApp(name)
    startApp(name)
}

val restartQQ = task("restartQQ").doLast {
    restartApp("com.tencent.mobileqq")
}

val restartTim = task("restartTim").doLast {
    restartApp("com.tencent.tim")
}

val optimizeReleaseRes = task("optimizeReleaseRes").doLast {
    val aapt2 = Paths.get(
        project.android.sdkDirectory.path,
        "build-tools", project.android.buildToolsVersion, "aapt2"
    )
    val zip = Paths.get(
        project.layout.buildDirectory.asFile.get().path, "intermediates",
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
            // 能不能让这里执行 licenseReleaseReport
            finalizedBy(optimizeReleaseRes)
        }

        "installDebug" -> {
            // 不知道怎么判断，先这样写
            finalizedBy(restartTim)
            finalizedBy(restartQQ)
        }
    }
}
