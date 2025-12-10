package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class PayPalGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "paypal"
    override val name = "PayPal"
    override val iconUrl = "https://www.paypalobjects.com/webstatic/mktg/logo/pp_cc_mark_111x69.jpg"
    override val category = GatewayCategory.DIGITAL_WALLET
    override val countries = listOf("US", "GB", "CA", "AU", "IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        if (config.countryCode in countries) {
            PaymentResult.Success("paypal_setup_${customerId}", id, 0.0, "paypal_${customerId}")
        } else {
            PaymentResult.Failure("REGION_NOT_SUPPORTED", "PayPal not available", id)
        }

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0", id)
        else PaymentResult.Success("PAYPAL_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode in countries) {
        listOf(PaymentOption("paypal_wallet", "PayPal", id, GatewayCategory.DIGITAL_WALLET))
    } else emptyList()
}