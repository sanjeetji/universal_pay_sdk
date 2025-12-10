package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class StripeGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "stripe"
    override val name = "Stripe"
    override val iconUrl = "https://stripe.com/img/v3/home/logo.svg"
    override val category = GatewayCategory.CARD
    override val countries = listOf("IN", "US", "GB", "CA", "AU")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult {
        return if (config.countryCode in countries) {
            PaymentResult.Success(
                transactionId = "stripe_setup_${customerId}",
                gatewayId = id,
                amount = 0.0,
                paymentMethodId = "stripe_${customerId}"
            )
        } else {
            PaymentResult.Failure(
                errorCode = "REGION_NOT_SUPPORTED",
                message = "Stripe not available in your region",
                gatewayId = id
            )
        }
    }

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult {
        return if (request.amount <= 0) {
            PaymentResult.Failure("INVALID_AMOUNT", "Amount must be > 0", id)
        } else {
            PaymentResult.Success(
                transactionId = "STRIPE_${request.orderId}",
                gatewayId = id,
                amount = request.amount,
                paymentMethodId = savedMethodId
            )
        }
    }

    override fun getOptions(countryCode: String): List<PaymentOption> = if (countryCode in countries) {
        listOf(PaymentOption("stripe_card", "Cards", id, GatewayCategory.CARD))
    } else emptyList()
}
