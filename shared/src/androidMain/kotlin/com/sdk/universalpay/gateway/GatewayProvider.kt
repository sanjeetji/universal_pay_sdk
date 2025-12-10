package com.sdk.universalpay.gateway

import android.content.Context
import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.context.ContextProvider
import com.sdk.universalpay.gateway.impl.UpiIntentGateway

actual fun getPlatformSpecificGateways(config: SdkConfig): List<PaymentGateway> {
    return try {
        val context: Context = ContextProvider.getContext()

        val gateways = mutableListOf<PaymentGateway>()

        // Add UPI for India
        if (config.countryCode == "IN") {
            gateways.add(UpiIntentGateway(config, context))
        }

        gateways
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
