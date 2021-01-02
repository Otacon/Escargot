import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.4.20"
    id("org.openjfx.javafxplugin") version "0.0.9"
    id("com.squareup.sqldelight") version "1.4.3"
    application
}

group = "me.orfeo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.okhttp3", "okhttp", "4.2.2")
    implementation("com.squareup.okhttp3", "logging-interceptor", "4.2.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.4.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-javafx", "1.4.2")
    implementation("org.simpleframework", "simple-xml", "2.7.1")
    implementation("com.squareup.sqldelight","sqlite-driver","1.4.3")

    testImplementation("junit", "junit", "4.12")
}

javafx {
    version = "15.0.1"
    modules = listOf("javafx.controls", "javafx.fxml")
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

application {
    mainClassName = "MainKt"
}