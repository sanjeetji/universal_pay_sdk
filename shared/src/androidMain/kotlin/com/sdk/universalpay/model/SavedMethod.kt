package com.sdk.universalpay.model

import java.io.Serializable

data class SavedMethod(
    val paymentMethodId: String,
    val gatewayId: String,
    val lastFour: String,
    val expiry: String? = null,
    val savedAt: Long = System.currentTimeMillis()
) : Serializable
