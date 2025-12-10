package com.sdk.universalpay.gateway.impl

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

class AmazonPayGateway(private val config: SdkConfig) : PaymentGateway {
    override val id = "amazonpay"
    override val name = "Amazon Pay"
    override val iconUrl = "https://m.media-amazon.com/images/G/01/AmazonPay/Amazon_Pay_Logo._CB445984492_.png"
    override val category = GatewayCategory.WALLET
    override val countries = listOf("IN")

    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult {
        return if (config.countryCode == "IN") {
            PaymentResult.Success(
                transactionId = "amazonpay_setup_${customerId}",
                gatewayId = id,
                amount = 0.0,
                paymentMethodId = "amazonpay_${customerId}"
            )
        } else {
            // ✅ FAILURE CASE - CORRECT
            PaymentResult.Failure(
                errorCode = "REGION_NOT_SUPPORTED",
                message = "Amazon Pay available only in India",
                gatewayId = id
            )
        }
    }


    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult {
        return if (request.amount <= 0) {
            // ✅ FAILURE CASE - CORRECT
            PaymentResult.Failure(
                errorCode = "INVALID_AMOUNT",
                message = "Amount must be greater than 0",
                gatewayId = id
            )
        } else {
            PaymentResult.Success(
                transactionId = "AMAZON_${request.orderId}",
                gatewayId = id,
                amount = request.amount,
                paymentMethodId = savedMethodId
            )
        }
    }
    override fun getOptions(countryCode: String) = listOf(
        PaymentOption("amazon_wallet", "Amazon Pay Wallet", id, GatewayCategory.WALLET)
    )
}