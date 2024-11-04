package com.example.wiz_cast

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout

suspend fun <T> StateFlow<T>.getOrAwaitValue(
    time: Long = 2000L
): T {
    return withTimeout(time) {
        this@getOrAwaitValue.first()
    }
}
