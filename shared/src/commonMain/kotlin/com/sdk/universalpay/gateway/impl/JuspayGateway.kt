package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class JuspayGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "juspay"
    override val name = "Juspay"
    override val iconUrl = "https://www.juspay.in/assets/juspay-logo.svg"
    override val category = GatewayCategory.UPI
    override val countries = listOf("IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        PaymentResult.Success("juspay_setup_${customerId}", id, 0.0, "juspay_${customerId}")

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0", id)
        else PaymentResult.Success("JUSPAY_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode == "IN") {
        listOf(PaymentOption("juspay_upi", "Juspay", id, GatewayCategory.UPI))
    } else emptyList()
}