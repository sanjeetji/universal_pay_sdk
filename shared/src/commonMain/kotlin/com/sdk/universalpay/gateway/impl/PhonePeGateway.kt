package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class PhonePeGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "phonepe"
    override val name = "PhonePe"
    override val iconUrl = "https://www.phonepe.com/static/website/images/phonepe-logo.svg"
    override val category = GatewayCategory.UPI
    override val countries = listOf("IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        PaymentResult.Success("phonepe_setup_${customerId}", id, 0.0, "phonepe_${customerId}")

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0", id)
        else PaymentResult.Success("PHONEPE_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode == "IN") {
        listOf(PaymentOption("phonepe_upi", "PhonePe", id, GatewayCategory.UPI))
    } else emptyList()
}
