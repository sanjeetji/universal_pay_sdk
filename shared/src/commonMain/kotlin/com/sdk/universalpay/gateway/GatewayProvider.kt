package com.sdk.universalpay.gateway

import com.sdk.universalpay.config.SdkConfig

expect fun getPlatformSpecificGateways(config: SdkConfig): List<PaymentGateway>
