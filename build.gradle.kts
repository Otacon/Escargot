import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

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
        val userDir = File(System.getProperty("user.home"), "AppData\\Local\\Escargot\\").absolutePath
        val cb = org.update4j.Configuration.builder()
            .property("app.name", "MyApplication")
            .property("default.launcher.main.class", "MainKt")

        project.configurations.default.resolvedConfiguration.resolvedArtifacts.forEach { artifact ->
            val file = artifact.file
            val moduleVersionId = artifact.moduleVersion.id
            val group = moduleVersionId.group.replace(".", "/").orEmpty()
            val name = moduleVersionId.name.replace(".", "/")
            val version = moduleVersionId.version
            val jarFile = if (file.name.endsWith("win.jar")) {
                "${artifact.name}-${version}-win.jar"
            } else {
                "${artifact.name}-${version}.jar"
            }
            val url = project.repositories.toList()
                .filterIsInstance<MavenArtifactRepository>()
                .mapNotNull {
                    val url = "${it.url}$group/$name/$version/$jarFile"
                    try {
                        if (URL(url).openStream() != null) {
                            return@mapNotNull url
                        }
                    } catch (e: java.io.FileNotFoundException) {
                        println("$jarFile not found on $url")
                    }
                    null
                }.first()
            cb.file(
                org.update4j.FileMetadata
                    .readFrom(file.absolutePath)
                    .path("$userDir\\${file.name}")
                    .uri(url)
                    .classpath()
            )
        }

        val configuration = cb.build()
        val writer = File(buildDir, "update4jconfig.xml").writer()
        configuration.write(writer)
        writer.close()
    }
}