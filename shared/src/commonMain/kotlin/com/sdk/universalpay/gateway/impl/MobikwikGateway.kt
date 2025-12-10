package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class MobikwikGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "mobikwik"
    override val name = "Mobikwik"
    override val iconUrl = "https://www.mobikwik.com/assets/mobikwik-logo.svg"
    override val category = GatewayCategory.WALLET
    override val countries = listOf("IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        PaymentResult.Success("mobikwik_setup_${customerId}", id, 0.0, "mobikwik_${customerId}")

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0", id)
        else PaymentResult.Success("MOBIKWIK_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode == "IN") {
        listOf(PaymentOption("mobikwik_wallet", "Mobikwik", id, GatewayCategory.WALLET))
    } else emptyList()
}