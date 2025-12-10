package com.sdk.universalpay.util

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.math.abs
import kotlin.random.Random

actual object TimeProvider {
    actual fun getCurrentTimeMillis(): Long {
        val date = NSDate()
        val timestamp = (date.timeIntervalSince1970 * 1000).toLong()
        return timestamp
    }
    
    actual fun generateOrderId(): String {
        val timestamp = getCurrentTimeMillis()
        val random = Random.nextInt(10000)
        return "order_${timestamp}_$random"
    }
}
