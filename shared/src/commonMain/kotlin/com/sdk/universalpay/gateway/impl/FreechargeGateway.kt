package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class FreechargeGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "freecharge"
    override val name = "Freecharge"
    override val iconUrl = "https://www.freecharge.in/assets/freecharge-logo.svg"
    override val category = GatewayCategory.WALLET
    override val countries = listOf("IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        PaymentResult.Success("freecharge_setup_${customerId}", id, 0.0, "freecharge_${customerId}")

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0", id)
        else PaymentResult.Success("FREECHARGE_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode == "IN") {
        listOf(PaymentOption("freecharge_wallet", "Freecharge", id, GatewayCategory.WALLET))
    } else emptyList()
}