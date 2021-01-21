package org.cyanotic.butterfly.core.utils

import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.time.Duration
import java.time.temporal.ChronoUnit

private val httpLogger = KotlinLogging.logger("HTTP Client")
private val logger = object : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        httpLogger.debug { message }
    }

}
private val interceptor = HttpLoggingInterceptor(logger = logger).also { it.level = HttpLoggingInterceptor.Level.BODY }

val httpClient = OkHttpClient.Builder()
    .connectTimeout(Duration.of(30, ChronoUnit.SECONDS))
    .readTimeout(Duration.of(30, ChronoUnit.SECONDS))
    .writeTimeout(Duration.of(30, ChronoUnit.SECONDS))
    .addInterceptor(interceptor)
    .build()