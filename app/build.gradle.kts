@file:Suppress("UnstableApiUsage")

import com.android.build.api.artifact.ArtifactTransformationRequest
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.BuiltArtifact
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
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
    alias(libs.plugins.kotlinCompose)
}
val signingPropFile = File(projectDir, "signing.properties")
val performSigning = signingPropFile.exists()

val appVerCode: Int by lazy {
    val versionCode = SimpleDateFormat("yyMMddHH", Locale.ENGLISH).format(Date())
    println("versionCode: $versionCode")
    versionCode.toInt()
}
val buildNum: String get() = SimpleDateFormat("MMddHH", Locale.ENGLISH).format(Date())
val appVerName: String = "3.0.30-fix"
val updateLog = """
    【问题修复】
    - 修复 在 QQ v9.1.55 及以上版本中获取cookie失败的问题
    - 其它修复与优化
    
    - 修复 3.0.30 存在的一个问题
""".trimIndent()

// 执行 gradle licenseReleaseReport
licenses {
    additionalProjects(":dexkit", ":mmkv", ":stub", ":system")
    reports {
        html.enabled = false
        json {
            enabled = true
            outputFile = file("./src/main/assets/licenses.json")
        }
    }
}

composeCompiler {
    includeSourceInformation = true

    featureFlags = setOf(
        ComposeFeatureFlag.StrongSkipping.disabled(),
        ComposeFeatureFlag.OptimizeNonSkippingGroups
    )
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
            prefab = true
            compose = true
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

    buildTypes {
        all {
            signingConfig =
                if (performSigning) signingConfigs.getByName("config")
                else signingConfigs.getByName("debug")
        }
        val debug by getting {
            versionNameSuffix = ".$buildNum-debug"
            val debugFlags = arrayOf<String>(
                "-DMODULE_SIGNATURE=FF9FF61037FF85BEDDBA5C98A3CB7600",
                "-DPKG_NAME=${namespace}",
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
                        "-DPKG_NAME=${namespace}",
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

    lint {
        abortOnError = false
    }

    dependenciesInfo {
        includeInApk = false
    }
    androidResources {
        additionalParameters += arrayOf("--allow-reserved-package-id", "--package-id", "0x62")
    }

    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1+"
        }
    }
    packaging {
        resources {
            excludes += arrayOf("**")
        }
        jniLibs.excludes += arrayOf("lib/**/liblog.so", "lib/**/libz.so")
    }
}

kotlin {
    jvmToolchain(libs.versions.jvm.target.get().toInt())
    sourceSets.all {
        languageSettings {
            enableLanguageFeature("ContextParameters")
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
        // todo exec 等待迁移
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
            versionJson.writeText("""
                {
                    "versionName": "$appVerName",
                    "versionCode": $appVerCode,
                    "updateTime": "$formatTime",
                    "updateLog": "${updateLog.replace("\n", "\\n")}"
                }
            """.trimIndent())
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
    implementation(libs.hiddenapibypass)

    compileOnly(libs.api)
    implementation(libs.ezxhelper)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.protobuf.kotlin.lite)
    compileOnly(libs.protoc)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.kotlinx.datetime)


    implementation(libs.androidx.browser)
    // jetpack compose
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.navigation)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.materialWindow)


    implementation(libs.androidx.material3.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.androidx.adaptive.navigation)
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // ViewModel utilities for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

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

    implementation(libs.androidx.navigation.compose)
}

val adbExecutable: String = androidComponents.sdkComponents.adb.get().asFile.absolutePath


fun killApp(name: String) {
    providers.exec {
        commandLine(adbExecutable, "shell", "am", "force-stop", name)
    }
}

fun startApp(name: String) {
    providers.exec {
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

val restartQQ = tasks.register("restartQQ") {
    doLast {
        restartApp("com.tencent.mobileqq")
    }
}

val restartTim = tasks.register("restartTim") {
    doLast {
        restartApp("com.tencent.tim")
    }
}

val optimizeReleaseRes = tasks.register("optimizeReleaseRes") {
    doLast {
        val aapt2 = Paths.get(
            project.android.sdkDirectory.path,
            "build-tools", project.android.buildToolsVersion, "aapt2"
        )
        val zip = Paths.get(
            project.layout.buildDirectory.asFile.get().path, "intermediates",
            "optimized_processed_res", "release", "resources-release-optimize.ap_"
        )
        val optimized = File("${zip}.opt")
        val cmd = providers.exec {
            commandLine(aapt2, "optimize", "--collapse-resource-names", "-o", optimized, zip)
            isIgnoreExitValue = true
        }
        if (cmd.result.get().exitValue == 0) {
            delete(zip)
            optimized.renameTo(zip.toFile())
        }
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
//            finalizedBy(restartTim)
            finalizedBy(restartQQ)
        }
    }
}
