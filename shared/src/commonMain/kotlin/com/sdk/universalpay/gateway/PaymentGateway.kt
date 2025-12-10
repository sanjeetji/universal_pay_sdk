package com.sdk.universalpay.gateway

import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

enum class GatewayCategory {
    UPI, CARD, WALLET, BANK, BNPL, CRYPTO, DIGITAL_WALLET
}

interface PaymentGateway {
    val id: String
    val name: String
    val iconUrl: String?
    val category: GatewayCategory
    val countries: List<String>

    suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult
    suspend fun executePayment(request: PaymentRequest, savedMethodId: String? = null): PaymentResult
    fun getOptions(countryCode: String): List<PaymentOption>
}

data class PaymentOption(
    val id: String,
    val name: String,
    val gatewayId: String,
    val category: GatewayCategory
)
