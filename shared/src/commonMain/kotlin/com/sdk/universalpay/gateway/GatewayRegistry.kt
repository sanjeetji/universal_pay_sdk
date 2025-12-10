package com.sdk.universalpay.gateway

import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.impl.*

class GatewayRegistry(private val config: SdkConfig) {

    private val all: List<PaymentGateway> by lazy {
        buildGatewayList(config)
    }

    private fun buildGatewayList(config: SdkConfig): List<PaymentGateway> {
        val commonGateways = listOf(
            RazorpayGateway(config),
            StripeGateway(config),
            AmazonPayGateway(config),
            PhonePeGateway(config),
            PaytmGateway(config),
            PaystackGateway(config),
            CashfreeGateway(config),
            JuspayGateway(config),
            CCAvenueGateway(config),
            BharatPeGateway(config),
            MobikwikGateway(config),
            FreechargeGateway(config),
            PayPalGateway(config),
            CardTokenGateway(config),
            SimplGateway(config),
            PawaPayGateway(config),
            GooglePayGateway(config)
        )

        // Add platform-specific gateways
        val platformGateways = getPlatformSpecificGateways(config)

        return commonGateways + platformGateways
    }

    val allGateways: List<PaymentGateway>
        get() = all.filter { config.countryCode in it.countries }

    fun getGateway(id: String): PaymentGateway? = allGateways.find { it.id == id }

    fun getGatewaysByCategory(category: GatewayCategory): List<PaymentGateway> =
        allGateways.filter { it.category == category }

    fun getOptionsForCountry(countryCode: String): List<PaymentOption> =
        allGateways.filter { countryCode in it.countries }
            .flatMap { it.getOptions(countryCode) }
}
