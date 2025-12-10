package com.sdk.universalpay

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayRegistry
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult
import com.sdk.universalpay.model.SavedPaymentMethod
import com.sdk.universalpay.orchestrator.PaymentOrchestrator
import com.sdk.universalpay.storage.EncryptedSecureStorage
import com.sdk.universalpay.util.TimeProvider
import kotlin.concurrent.Volatile

class UniversalPay private constructor(
    private val config: SdkConfig,
    private val storage: EncryptedSecureStorage,
    val orchestrator: PaymentOrchestrator
) {
    companion object {
        @Volatile
        private var INSTANCE: UniversalPay? = null

        fun initialize(config: SdkConfig, storage: EncryptedSecureStorage) {
            val registry = GatewayRegistry(config)
            INSTANCE = UniversalPay(
                config = config,
                storage = storage,
                orchestrator = PaymentOrchestrator(registry, storage, config)
            )
        }

        fun get(): UniversalPay = INSTANCE ?: error("Call UniversalPay.initialize() first")
    }

    suspend fun savePaymentMethod(
        customerId: String,
        gatewayId: String
    ): PaymentResult {
        val orderId = TimeProvider.generateOrderId()
        val request = PaymentRequest(
            amount = 0.0,
            currency = "INR",
            orderId = orderId,
            customerId = customerId
        )
        return orchestrator.setup(customerId, gatewayId, request)
    }

    suspend fun pay(
        request: PaymentRequest,
        savedMethodId: String? = null
    ): PaymentResult {
        return orchestrator.pay(request, savedMethodId)
    }

    suspend fun getSavedMethods(customerId: String): List<SavedPaymentMethod> {
        return orchestrator.getSavedMethods(customerId)
    }

    suspend fun deleteSavedMethod(customerId: String, gatewayId: String): Boolean {
        return try {
            storage.deleteMethod(customerId, gatewayId)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun clearAllMethods(customerId: String): Boolean {
        return try {
            storage.clearAll()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getGateways() = orchestrator.getGateways()
    fun getOptions(countryCode: String = "IN") = orchestrator.getOptions(countryCode)
    fun getConfig(): SdkConfig = config
}
