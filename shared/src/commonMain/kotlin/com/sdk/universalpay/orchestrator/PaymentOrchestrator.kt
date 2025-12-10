package com.sdk.universalpay.orchestrator

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayRegistry
import com.sdk.universalpay.model.*
import com.sdk.universalpay.storage.EncryptedSecureStorage

class PaymentOrchestrator(
    private val registry: GatewayRegistry,
    private val storage: EncryptedSecureStorage,
    private val config: SdkConfig
) {

    suspend fun setup(customerId: String, gatewayId: String, request: PaymentRequest): PaymentResult {
        val gateway = registry.allGateways.find { it.id == gatewayId }
            ?: return PaymentResult.Failure("GATEWAY_NOT_FOUND", "Gateway not available", gatewayId)

        val result = gateway.setupPaymentMethod(customerId, request)
        if (result is PaymentResult.Success && result.paymentMethodId != null) {
            val savedMethod = SavedPaymentMethod(
                paymentMethodId = result.paymentMethodId,
                gatewayId = gatewayId,
                gatewayName = gateway.name,
                lastFour = "****"
            )
            storage.saveMethod(customerId, savedMethod)
        }
        return result
    }

    suspend fun pay(request: PaymentRequest, savedMethodId: String? = null): PaymentResult {
        return if (savedMethodId != null) {
            payWithSavedMethod(request, savedMethodId)
        } else {
            payImmediate(request)
        }
    }

    suspend fun getSavedMethods(customerId: String): List<SavedPaymentMethod> {
        return try {
            storage.getMethods(customerId)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun payWithSavedMethod(request: PaymentRequest, savedMethodId: String): PaymentResult {
        val customerId = request.customerId ?: return PaymentResult.Failure(
            "NO_CUSTOMER", "Customer ID required", "orchestrator"
        )

        val savedMethod = storage.getMethods(customerId).find { it.paymentMethodId == savedMethodId }
            ?: return PaymentResult.Failure(
                "METHOD_NOT_FOUND", "Saved method not found", "orchestrator"
            )

        val gateway = registry.getGateway(savedMethod.gatewayId)
            ?: return PaymentResult.Failure(
                "GATEWAY_NOT_FOUND", "Gateway unavailable", savedMethod.gatewayId
            )

        return gateway.executePayment(request, savedMethodId)
    }

    private suspend fun payImmediate(request: PaymentRequest): PaymentResult {
        return PaymentResult.Failure("GATEWAY_REQUIRED", "Please select a gateway", "orchestrator")
    }

    fun getGateways() = registry.allGateways
    fun getOptions(countryCode: String) = registry.getOptionsForCountry(countryCode)
}
