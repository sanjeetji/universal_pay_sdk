package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class CardTokenGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "cardtoken"
    override val name = "Card Token"
    override val iconUrl: String? = null
    override val category = GatewayCategory.CARD
    override val countries = listOf("IN", "US")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        if (config.countryCode in countries) {
            PaymentResult.Success("token_setup_${customerId}", id, 0.0, "token_${customerId}")
        } else {
            PaymentResult.Failure("REGION_NOT_SUPPORTED", "Card Token not available", id)
        }

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0", id)
        else PaymentResult.Success("TOKEN_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode in countries) {
        listOf(PaymentOption("token_card", "Tokenized Cards", id, GatewayCategory.CARD))
    } else emptyList()
}