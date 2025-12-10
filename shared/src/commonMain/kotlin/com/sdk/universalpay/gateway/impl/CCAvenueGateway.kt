package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class CCAvenueGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "ccavenue"
    override val name = "CCAvenue"
    override val iconUrl = "https://www.ccavenue.com/assets/logo.png"
    override val category = GatewayCategory.CARD
    override val countries = listOf("IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        PaymentResult.Success("ccavenue_setup_${customerId}", id, 0.0, "ccavenue_${customerId}")

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0", id)
        else PaymentResult.Success("CCAVENUE_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode == "IN") {
        listOf(PaymentOption("ccavenue_card", "Cards", id, GatewayCategory.CARD))
    } else emptyList()
}