package org.cyanotic.butterfly.features

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.throttle(waitMillis: Int) = flow {
    coroutineScope {
        var nextMillis = 0L
        collect {
            val current = System.currentTimeMillis()
            if (nextMillis < current) {
                nextMillis = current + waitMillis
                emit(it)
            }
        }
    }
}