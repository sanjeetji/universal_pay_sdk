package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class PaystackGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "paystack"
    override val name = "Paystack"
    override val iconUrl = "https://paystack.com/favicon.ico"
    override val category = GatewayCategory.CARD
    override val countries = listOf("NG", "GH", "ZA")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        if (config.countryCode in countries) {
            PaymentResult.Success("paystack_setup_${customerId}", id, 0.0, "paystack_${customerId}")
        } else {
            PaymentResult.Failure("REGION_NOT_SUPPORTED", "Paystack Africa only", id)
        }

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0 required", id)
        else PaymentResult.Success("PAYSTACK_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode in countries) {
        listOf(PaymentOption("paystack_card", "Cards", id, GatewayCategory.CARD))
    } else emptyList()
}
