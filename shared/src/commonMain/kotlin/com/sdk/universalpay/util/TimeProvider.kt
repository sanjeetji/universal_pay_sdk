package com.sdk.universalpay.util

expect object TimeProvider {
    fun getCurrentTimeMillis(): Long
    fun generateOrderId(): String
}
