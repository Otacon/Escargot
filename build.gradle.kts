import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL
import java.nio.file.StandardCopyOption
import java.nio.file.Paths
import java.nio.file.Files

buildscript {

    dependencies {
        classpath("org.update4j", "update4j", "1.5.6")
    }
}

plugins {
    id("java")
    kotlin("jvm") version "1.4.20"
    id("org.openjfx.javafxplugin") version "0.0.9"
    id("com.squareup.sqldelight") version "1.4.4"
    id("org.beryx.runtime") version "1.12.1"
    application
}

group = "me.orfeo"
version = "1.0.0"

repositories {
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx/")
}

dependencies {
    implementation("com.squareup.okhttp3", "okhttp", "4.2.2")
    implementation("com.squareup.okhttp3", "logging-interceptor", "4.2.2")
    implementation("org.simpleframework", "simple-xml", "2.7.1")

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.4.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-javafx", "1.4.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-datetime-jvm", "0.1.1")

    implementation("com.squareup.sqldelight", "sqlite-driver", "1.4.4")
    implementation("com.squareup.sqldelight", "coroutines-extensions", "1.2.1")
    implementation("io.github.microutils", "kotlin-logging-jvm", "2.0.2")

    implementation("org.slf4j", "slf4j-api", "1.7.26")
    implementation("ch.qos.logback", "logback-classic", "1.2.3")
    implementation("ch.qos.logback", "logback-core", "1.2.3")

    testImplementation("junit", "junit", "4.12")
}

javafx {
    version = "15.0.1"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.media")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
}

sqldelight {
    database("Database") { // This will be the name of the generated database class.
        packageName = "me.orfeo"
    }
}

runtime {
    options.set(
        listOf(
            "--compress", "2",
            "--no-man-pages"
        )
    )

    jpackage {
        // MacOSX config
        //jvmArgs.add("-Duser.dir=/tmp")
        imageOptions = listOf("--icon", "src/main/resources/e-logo.icns")

        // Base Config
        installerOptions = listOf(
            "--resource-dir", "src/main/resources"
        )

        //Windows Config
        imageOptions = listOf("--win-console")
    }

}

application {
    applicationName = "Escargot"
    mainClassName = "MainKt"
}

task("copyRuntimeLibs", type = Copy::class) {
    into("build/libs")
    from(configurations.compileClasspath)
}

task("getURLofDependencyArtifact") {
    doFirst {
        val winUserDir = File(System.getProperty("user.home"), "AppData\\Local\\escargot\\libs").absolutePath
        val macUserDir = File(System.getProperty("user.home"), "Library/ApplicationSupport/escargot/libs").absolutePath
        val linuxUserDir = File(System.getProperty("user.home"), ".escargot/libs").absolutePath
        val cb = org.update4j.Configuration.builder()
            .property("app.name", "Escargot")
            .property("default.launcher.main.class", "MainKt")
        val outputDir = File(buildDir, "update4j")
        outputDir.mkdirs()

        val references = project.configurations.default.resolvedConfiguration.resolvedArtifacts.map { artifact ->
            Update4JArtifactInfo(
                file = artifact.file,
                path = null,
                group = artifact.moduleVersion.id.group,
                name = artifact.moduleVersion.id.name,
                version = artifact.moduleVersion.id.version,
                osVersion = null,
                url = null
            )
        }.flatMap {
            val group = it.group.replace(".", "/").orEmpty()
            val name = it.name.replace(".", "/")
            val version = it.version
            val urlPrefix = "${it.name}-${version}"
            if (it.file.name.endsWith("win.jar") || it.file.name.endsWith("mac.jar") || it.file.name.endsWith("linux.jar")) {
                listOf(
                    it.copy(
                        path = "$winUserDir\\$urlPrefix-win.jar",
                        osVersion = "win",
                        url = "$group/$name/$version/$urlPrefix-win.jar"
                    ),
                    it.copy(
                        path = "$linuxUserDir/$urlPrefix-linux.jar",
                        osVersion = "linux",
                        url = "$group/$name/$version/$urlPrefix-linux.jar"
                    ),
                    it.copy(
                        path = "$macUserDir/$urlPrefix-mac.jar",
                        osVersion = "mac",
                        url = "$group/$name/$version/$urlPrefix-mac.jar"
                    )
                )
            } else {
                listOf(
                    it.copy(
                        path = "$winUserDir\\$urlPrefix.jar",
                        osVersion = "win",
                        url = "$group/$name/$version/$urlPrefix.jar"
                    ),
                    it.copy(
                        path = "$linuxUserDir/$urlPrefix.jar",
                        osVersion = "linux",
                        url = "$group/$name/$version/$urlPrefix.jar"
                    ),
                    it.copy(
                        path = "$macUserDir/$urlPrefix.jar",
                        osVersion = "mac",
                        url = "$group/$name/$version/$urlPrefix.jar"
                    )
                )

            }
        }.map { artifactInfo ->
            project.repositories.asSequence()
                .filterIsInstance<MavenArtifactRepository>()
                .mapNotNull { repository ->
                    val url = "${repository.url}${artifactInfo.url}"
                    try {
                        val input = URL(url).openStream()
                        val fileName = Paths.get(url).last().toString()
                        if (input != null) {
                            Files.copy(
                                input,
                                Paths.get(outputDir.absolutePath, fileName),
                                StandardCopyOption.REPLACE_EXISTING
                            )
                            return@mapNotNull artifactInfo.copy(
                                file = File(outputDir.absolutePath, fileName),
                                url = url
                            )
                        }
                    } catch (e: java.io.FileNotFoundException) {
                        println("${artifactInfo.file.name} not found on $url")
                    }
                    null
                }
                .first()
        }.map { artifact ->
            val os = when (artifact.osVersion) {
                "mac" -> org.update4j.OS.MAC
                "linux" -> org.update4j.OS.LINUX
                "win" -> org.update4j.OS.WINDOWS
                else -> throw IllegalArgumentException("Invalid os: $artifact.osVersion")
            }
            org.update4j.FileMetadata
                .readFrom(artifact.file.absolutePath)
                .path(artifact.path!!)
                .uri(artifact.url!!)
                .os(os)
                .classpath()
        } + listOf(
            org.update4j.FileMetadata
                .readFrom(Paths.get(buildDir.absolutePath, "libs", "Escargot-1.0.0.jar"))
                .path("$macUserDir/Escargot-1.0.0.jar")
                .uri("http://127.0.0.1:8887/Escargot-1.0.0.jar")
                .os(org.update4j.OS.MAC)
                .classpath(),
            org.update4j.FileMetadata
                .readFrom(Paths.get(buildDir.absolutePath, "libs", "Escargot-1.0.0.jar"))
                .path("$linuxUserDir/Escargot-1.0.0.jar")
                .uri("http://127.0.0.1:8887/Escargot-1.0.0.jar")
                .os(org.update4j.OS.LINUX)
                .classpath(),
            org.update4j.FileMetadata
                .readFrom(Paths.get(buildDir.absolutePath, "libs", "Escargot-1.0.0.jar"))
                .path("$winUserDir\\Escargot-1.0.0.jar")
                .uri("http://127.0.0.1:8887/Escargot-1.0.0.jar")
                .os(org.update4j.OS.WINDOWS)
                .classpath()
        )

        cb.files(references)

        val configuration = cb.build()
        val writer = File(outputDir, "update4jconfig.xml").writer()
        configuration.write(writer)
        writer.close()
    }
}

data class Update4JArtifactInfo(
    val file: File,
    val path: String?,
    val group: String,
    val name: String,
    val version: String,
    val osVersion: String?,
    val url: String?
)