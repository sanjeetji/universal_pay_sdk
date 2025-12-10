package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class SimplGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "simpl"
    override val name = "Simpl"
    override val iconUrl = "https://www.simpl.com/assets/simpl-logo.svg"
    override val category = GatewayCategory.BNPL
    override val countries = listOf("IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        PaymentResult.Success("simpl_setup_${customerId}", id, 0.0, "simpl_${customerId}")

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0", id)
        else PaymentResult.Success("SIMPL_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode == "IN") {
        listOf(PaymentOption("simpl_bnpl", "Buy Now Pay Later", id, GatewayCategory.BNPL))
    } else emptyList()
}