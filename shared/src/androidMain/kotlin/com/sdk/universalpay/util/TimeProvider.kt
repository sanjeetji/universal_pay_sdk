package com.sdk.universalpay.util

actual object TimeProvider {
    actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()
    
    actual fun generateOrderId(): String = 
        "order_${System.currentTimeMillis()}_${(Math.random() * 10000).toInt()}"
}
