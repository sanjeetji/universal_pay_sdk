package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class GooglePayGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "googlepay"
    override val name = "Google Pay"
    override val iconUrl = "https://www.gstatic.com/images/branding/product/1x/googlepay_64dp.png"
    override val category = GatewayCategory.UPI
    override val countries = listOf("IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        PaymentResult.Success("googlepay_setup_${customerId}", id, 0.0, "googlepay_${customerId}")

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0", id)
        else PaymentResult.Success("GOOGLEPAY_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode == "IN") {
        listOf(PaymentOption("googlepay_upi", "Google Pay", id, GatewayCategory.UPI))
    } else emptyList()
}