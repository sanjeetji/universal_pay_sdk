package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class PaytmGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "paytm"
    override val name = "Paytm"
    override val iconUrl = "https://assets.paytm.com/images/cashback/paytm-logo.png"
    override val category = GatewayCategory.WALLET
    override val countries = listOf("IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        PaymentResult.Success("paytm_setup_${customerId}", id, 0.0, "paytm_${customerId}")

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0 required", id)
        else PaymentResult.Success("PAYTM_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode == "IN") {
        listOf(PaymentOption("paytm_wallet", "Paytm Wallet", id, GatewayCategory.WALLET))
    } else emptyList()
}
