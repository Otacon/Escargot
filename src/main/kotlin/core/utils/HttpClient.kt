package core.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import mu.KotlinLogging

private val httpLogger = KotlinLogging.logger {}
private val logger = object: HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        httpLogger.debug { message }
    }

}
private val interceptor = HttpLoggingInterceptor(logger = logger).also { it.level = HttpLoggingInterceptor.Level.BODY }

val httpClient = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .build()