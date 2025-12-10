package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class PawaPayGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "pawapay"
    override val name = "PawaPay"
    override val iconUrl = "https://pawapay.cloud/assets/pawapay-logo.svg"
    override val category = GatewayCategory.WALLET
    override val countries = listOf("NG", "GH", "KE")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        if (config.countryCode in countries) {
            PaymentResult.Success("pawapay_setup_${customerId}", id, 0.0, "pawapay_${customerId}")
        } else {
            PaymentResult.Failure("REGION_NOT_SUPPORTED", "PawaPay Africa only", id)
        }

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        if (request.amount <= 0) PaymentResult.Failure("INVALID_AMOUNT", "Amount > 0", id)
        else PaymentResult.Success("PAWAPAY_${request.orderId}", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String) = if (countryCode in countries) {
        listOf(PaymentOption("pawapay_wallet", "PawaPay", id, GatewayCategory.WALLET))
    } else emptyList()
}