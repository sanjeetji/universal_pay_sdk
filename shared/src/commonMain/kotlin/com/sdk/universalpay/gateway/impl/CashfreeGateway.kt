package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class CashfreeGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "cashfree"
    override val name = "Cashfree"
    override val iconUrl = "https://images.cashfree.com/static/cf-logo.png"
    override val category = GatewayCategory.CARD
    override val countries = listOf("IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        PaymentResult.Success("cashfree_setup_${customerId}", id, 0.0, "cashfree_${customerId}")

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0 required", id)
        else PaymentResult.Success("CASHFREE_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode == "IN") {
        listOf(PaymentOption("cashfree_card", "Cards", id, GatewayCategory.CARD))
    } else emptyList()
}
