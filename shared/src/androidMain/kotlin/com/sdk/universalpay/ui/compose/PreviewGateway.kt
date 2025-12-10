package com.sdk.universalpay.ui.compose

import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult

// Preview-only gateway implementation, used only in @Preview composables
data class PreviewGateway(
    override val id: String,
    override val name: String,
    override val category: GatewayCategory,
    override val iconUrl: String? = null,
    override val countries: List<String> = emptyList()
) : PaymentGateway {

    override suspend fun setupPaymentMethod(
        customerId: String,
        request: PaymentRequest
    ): PaymentResult = PaymentResult.Success(
        transactionId = "preview_setup_tx",
        gatewayId = id,
        amount = request.amount,
        paymentMethodId = "preview_pm"
    )

    override suspend fun executePayment(
        request: PaymentRequest,
        savedMethodId: String?
    ): PaymentResult = PaymentResult.Success(
        transactionId = "preview_pay_tx",
        gatewayId = id,
        amount = request.amount,
        paymentMethodId = savedMethodId
    )

    override fun getOptions(countryCode: String): List<PaymentOption> = emptyList()
}
