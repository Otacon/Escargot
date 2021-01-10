package me.orfeo

import org.update4j.Archive
import org.update4j.Configuration
import org.update4j.FileMetadata
import org.update4j.UpdateOptions
import java.nio.file.Paths
import java.nio.file.Files


const val libsDir = "build/libs/"
const val directory = "C:\\Users\\Orfeo\\AppData\\Local\\Escargot\\"
fun main() {
    val cb = Configuration.builder()
        .property("app.name", "MyApplication")
        .file(
            createFileMetadata(
                "sqlite-driver-1.4.4.jar",
                "https://repo.maven.apache.org/maven2/com/squareup/sqldelight/sqlite-driver/1.4.4/sqlite-driver-1.4.4.jar"
            )
        )
        .file(
            createFileMetadata(
                "jdbc-driver-1.4.4.jar",
                "https://repo.maven.apache.org/maven2/com/squareup/sqldelight/jdbc-driver/1.4.4/jdbc-driver-1.4.4.jar"
            )
        )
        .file(
            createFileMetadata(
                "sqldelight-coroutines-extensions-jvm-1.2.1.jar",
                "https://repo.maven.apache.org/maven2/com/squareup/sqldelight/coroutines-extensions-jvm/1.2.1/coroutines-extensions-jvm-1.2.1.jar"
            )
        )
        .file(
            createFileMetadata(
                "runtime-jvm-1.4.4.jar",
                "https://repo.maven.apache.org/maven2/com/squareup/sqldelight/runtime-jvm/1.4.4/runtime-jvm-1.4.4.jar"
            )
        )
        .file(
            createFileMetadata(
                "logging-interceptor-4.2.2.jar",
                "https://repo.maven.apache.org/maven2/com/squareup/okhttp3/logging-interceptor/4.2.2/logging-interceptor-4.2.2.jar"
            )
        )
        .file(
            createFileMetadata(
                "okhttp-4.2.2.jar",
                "https://repo.maven.apache.org/maven2/com/squareup/okhttp3/okhttp/4.2.2/okhttp-4.2.2.jar"
            )
        )
        .file(
            createFileMetadata(
                "kotlinx-coroutines-javafx-1.4.2.jar",
                "https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-javafx/1.4.2/kotlinx-coroutines-javafx-1.4.2.jar"
            )
        )
        .file(
            createFileMetadata(
                "kotlinx-datetime-jvm-0.1.1.jar",
                "https://kotlin.bintray.com/kotlinx/org/jetbrains/kotlinx/kotlinx-datetime-jvm/0.1.1/kotlinx-datetime-jvm-0.1.1.jar"
            )
        )
        .file(
            createFileMetadata(
                "kotlin-logging-jvm-2.0.2.jar",
                "https://repo.maven.apache.org/maven2/io/github/microutils/kotlin-logging-jvm/2.0.2/kotlin-logging-jvm-2.0.2.jar"
            )
        )
        .file(
            createFileMetadata(
                "kotlin-stdlib-jdk8-1.4.20.jar",
                "https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk8/1.4.20/kotlin-stdlib-jdk8-1.4.20.jar"
            )
        )
        .file(
            createFileMetadata(
                "okio-2.2.2.jar",
                "https://repo.maven.apache.org/maven2/com/squareup/okio/okio/2.2.2/okio-2.2.2.jar"
            )
        )
        .file(
            createFileMetadata(
                "kotlinx-coroutines-core-jvm-1.4.2.jar",
                "https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core-jvm/1.4.2/kotlinx-coroutines-core-jvm-1.4.2.jar"
            )
        )
        .file(
            createFileMetadata(
                "kotlin-stdlib-jdk7-1.4.20.jar",
                "https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-jdk7/1.4.20/kotlin-stdlib-jdk7-1.4.20.jar"
            )
        )
        .file(
            createFileMetadata(
                "kotlin-stdlib-1.4.20.jar",
                "https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.4.20/kotlin-stdlib-1.4.20.jar"
            )
        )
        .file(
            createFileMetadata(
                "simple-xml-2.7.1.jar",
                "https://repo.maven.apache.org/maven2/org/simpleframework/simple-xml/2.7.1/simple-xml-2.7.1.jar"
            )
        )
        .file(
            createFileMetadata(
                "logback-classic-1.2.3.jar",
                "https://repo.maven.apache.org/maven2/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar"
            )
        )
        .file(
            createFileMetadata(
                "slf4j-api-1.7.29.jar",
                "https://repo.maven.apache.org/maven2/org/slf4j/slf4j-api/1.7.29/slf4j-api-1.7.29.jar"
            )
        )
        .file(
            createFileMetadata(
                "logback-core-1.2.3.jar",
                "https://repo.maven.apache.org/maven2/ch/qos/logback/logback-core/1.2.3/logback-core-1.2.3.jar"
            )
        )
        .file(
            createFileMetadata(
                "javafx-fxml-15.0.1-win.jar",
                "https://repo.maven.apache.org/maven2/org/openjfx/javafx-fxml/15.0.1/javafx-fxml-15.0.1-win.jar"
            )
        )
        .file(
            createFileMetadata(
                "javafx-controls-15.0.1-win.jar",
                "https://repo.maven.apache.org/maven2/org/openjfx/javafx-controls/15.0.1/javafx-controls-15.0.1-win.jar"
            )
        )
        .file(
            createFileMetadata(
                "javafx-media-15.0.1-win.jar",
                "https://repo.maven.apache.org/maven2/org/openjfx/javafx-media/15.0.1/javafx-media-15.0.1-win.jar"
            )
        )
        .file(
            createFileMetadata(
                "javafx-graphics-15.0.1-win.jar",
                "https://repo.maven.apache.org/maven2/org/openjfx/javafx-graphics/15.0.1/javafx-graphics-15.0.1-win.jar"
            )
        )
        .file(
            createFileMetadata(
                "javafx-base-15.0.1-win.jar",
                "https://repo.maven.apache.org/maven2/org/openjfx/javafx-base/15.0.1/javafx-base-15.0.1-win.jar"
            )
        )
        .file(
            createFileMetadata(
                "kotlinx-coroutines-core-common-1.3.2-1.3.60.jar",
                "https://repo.maven.apache.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core-common/1.3.2-1.3.60/kotlinx-coroutines-core-common-1.3.2-1.3.60.jar"
            )
        )
        .file(
            createFileMetadata(
                "kotlin-stdlib-common-1.4.20.jar",
                "https://repo.maven.apache.org/maven2/org/jetbrains/kotlin/kotlin-stdlib-common/1.4.20/kotlin-stdlib-common-1.4.20.jar"
            )
        )
        .file(
            createFileMetadata(
                "annotations-13.0.jar",
                "https://repo.maven.apache.org/maven2/org/jetbrains/annotations/13.0/annotations-13.0.jar"
            )
        )
        .file(
            createFileMetadata(
                "stax-1.2.0.jar",
                "https://repo.maven.apache.org/maven2/stax/stax/1.2.0/stax-1.2.0.jar"
            )
        )
        .file(
            createFileMetadata(
                "stax-api-1.0.1.jar",
                "https://repo.maven.apache.org/maven2/stax/stax-api/1.0.1/stax-api-1.0.1.jar"
            )
        )
        .file(
            createFileMetadata(
                "xpp3-1.1.3.3.jar",
                "https://repo.maven.apache.org/maven2/xpp3/xpp3/1.1.3.3/xpp3-1.1.3.3.jar"
            )
        )
        .file(
            createFileMetadata(
                "sqlite-jdbc-3.21.0.1.jar",
                "https://repo.maven.apache.org/maven2/org/xerial/sqlite-jdbc/3.21.0.1/sqlite-jdbc-3.21.0.1.jar"
            )
        )

        .file(
            FileMetadata
                .readFrom(libsDir + "Escargot-1.0.0.jar")
                .path(directory + "Escargot-1.0.0.jar")
                .uri("file:$libsDir" + "Escargot-1.0.0.jar")
                .classpath()
        )
        .property("default.launcher.main.class", "MainKt")

    val configuration = cb.build()
    Files.newBufferedWriter(Paths.get("config.xml")).use { out -> configuration.write(out) }
    configuration.update(UpdateOptions.archive(Paths.get("update.zip")))
    Archive.read("update.zip").install()
    configuration.launch()
}

fun createFileMetadata(filename: String, url: String): FileMetadata.Reference {
    return FileMetadata
        .readFrom(libsDir + filename)
        .path(directory + filename)
        .uri(url)
        .classpath()
}