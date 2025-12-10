package com.sdk.universalpay.gateway

import com.sdk.universalpay.config.SdkConfig

actual fun getPlatformSpecificGateways(config: SdkConfig): List<PaymentGateway> {
    // iOS-specific gateways here
    return emptyList()
}
