plugins {
    kotlin("jvm")
    id("org.beryx.runtime")
    application
}

group = "me.orfeo"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.update4j", "update4j", "1.5.6")
    implementation("com.google.code.gson", "gson", "2.8.6")
    implementation("org.xeustechnologies", "jcl-core", "2.8")
}


runtime {
    options.set(
        listOf(
            "--compress", "2",
            "--no-man-pages"
        )
    )
    launcher {

    }

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
    applicationName = "Launcher"
    mainClassName = "me.orfeo.MainKt"
}