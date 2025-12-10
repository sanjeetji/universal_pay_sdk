package com.sdk.universalpay.model


data class PaymentRequest(
    val amount: Double,
    val currency: String,
    val orderId: String,
    val customerId: String? = null,
    val metadata: Map<String, String> = emptyMap()
)

sealed class PaymentResult {
    data class Success(
        val transactionId: String,
        val gatewayId: String,
        val amount: Double,
        val paymentMethodId: String? = null,
        val rawResponse: Map<String, Any> = emptyMap()
    ) : PaymentResult()

    data class Failure(
        val errorCode: String,
        val message: String,
        val gatewayId: String
    ) : PaymentResult()

    object Cancelled : PaymentResult()
}
