package com.sdk.universalpay.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class SavedPaymentMethod @OptIn(ExperimentalTime::class) constructor(
    val paymentMethodId: String,
    val gatewayId: String,
    val lastFour: String,
    val gatewayName: String = "",
    val expiry: String? = null,
    val savedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val maskedCardNumber: String? = null
) {
    companion object {
        private const val serialVersionUID = 1L
    }
}