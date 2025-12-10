package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class RazorpayGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "razorpay"
    override val name = "Razorpay"
    override val iconUrl = "https://razorpay.com/assets/razorpay-logo.png"
    override val category = GatewayCategory.CARD
    override val countries = listOf("IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult {
        return if (config.countryCode == "IN") {
            PaymentResult.Success(
                transactionId = "rzp_setup_${customerId}",
                gatewayId = id,
                amount = 0.0,
                paymentMethodId = "rzp_${customerId}"
            )
        } else {
            PaymentResult.Failure(
                errorCode = "REGION_NOT_SUPPORTED",
                message = "Razorpay available only in India",
                gatewayId = id
            )
        }
    }

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult {
        return if (request.amount <= 0) {
            PaymentResult.Failure(
                errorCode = "INVALID_AMOUNT",
                message = "Amount must be greater than 0",
                gatewayId = id
            )
        } else {
            PaymentResult.Success(
                transactionId = "RZP_${request.orderId}",
                gatewayId = id,
                amount = request.amount,
                paymentMethodId = savedMethodId
            )
        }
    }

    override fun getOptions(countryCode: String): List<PaymentOption> = if (countryCode == "IN") {
        listOf(
            PaymentOption("rzp_card", "Cards", id, GatewayCategory.CARD),
            PaymentOption("rzp_netbanking", "Net Banking", id, GatewayCategory.BANK),
            PaymentOption("rzp_wallet", "Wallets", id, GatewayCategory.WALLET)
        )
    } else emptyList()
}
