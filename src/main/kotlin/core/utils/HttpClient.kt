package core.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

val httpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().also { it.level = HttpLoggingInterceptor.Level.BODY }).build()