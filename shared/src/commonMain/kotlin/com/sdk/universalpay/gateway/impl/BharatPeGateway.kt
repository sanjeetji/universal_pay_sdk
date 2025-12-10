package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class BharatPeGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "bharatpe"
    override val name = "BharatPe"
    override val iconUrl = "https://bharatpe.in/assets/bharatpe-logo.svg"
    override val category = GatewayCategory.UPI
    override val countries = listOf("IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        PaymentResult.Success("bharatpe_setup_${customerId}", id, 0.0, "bharatpe_${customerId}")

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0", id)
        else PaymentResult.Success("BHARATPE_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode == "IN") {
        listOf(PaymentOption("bharatpe_upi", "BharatPe", id, GatewayCategory.UPI))
    } else emptyList()
}