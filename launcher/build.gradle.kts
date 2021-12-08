plugins {
    kotlin("jvm")
    id("org.beryx.runtime") version "1.12.1"
    application
}

group = "org.cyanotic.butterfly-launcher"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.update4j", "update4j", "1.5.6")

    implementation("io.github.microutils", "kotlin-logging-jvm", "2.0.2")

    implementation("org.slf4j", "slf4j-api", "1.7.26")
    implementation("ch.qos.logback", "logback-classic", "1.2.3")
    implementation("ch.qos.logback", "logback-core", "1.2.3")
}


runtime {
    options.set(
        listOf(
            "--compress", "2",
            "--no-man-pages"
        )
    )

    modules.addAll(
        "java.desktop",
        "java.xml",
        "java.sql",
        "java.naming",
        "java.logging",
        "java.management",
        "jdk.unsupported",
        "jdk.jfr",
        "java.scripting",
        "jdk.crypto.cryptoki",
        "jdk.zipfs"
    )

    jpackage {
        val os = org.gradle.internal.os.OperatingSystem.current()

        installerOptions = listOf(
            "--resource-dir", "src/main/resources"
        )

        imageName = "Escargot"

        when {
            os.isWindows -> {
                installerType = "msi"
                imageOptions = listOf(
                    "--win-console",
                    "--icon", "src/main/resources/e-logo.ico"
                )
                installerOptions = listOf(
                    "--win-per-user-install",
                    "--win-dir-chooser",
                    "--win-menu",
                    "--win-shortcut"
                )
            }
            os.isMacOsX -> {
                imageOptions = listOf(
                    "--icon", "src/main/resources/e-logo.icns"
                )
            }
            os.isLinux -> {
                imageOptions = listOf(
                    "--icon", "src/main.resources/e-logo.png",
                    "--type", "deb",
                    "--vendor", "Cyanotic.dev"
                )
            }
        }
    }
}

application {
    applicationName = "Escargot Launcher"
    mainClassName = "org.cyanotic.butterfly_launcher.MainKt"
}