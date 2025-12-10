package com.sdk.universalpay.config

enum class Environment {
    SANDBOX,
    PRODUCTION
}

enum class Mode {
    IMMEDIATE,
    DEFERRED,
    HYBRID
}

data class SdkConfig(
    val merchantId: String,
    val environment: Environment = Environment.SANDBOX,
    val mode: Mode = Mode.HYBRID,
    val countryCode: String = "IN",
    val colorPrimary: Long? = null,        // For UI theming
    val colorSecondary: Long? = null
) {
    companion object {
        fun create(merchantId: String) = SdkConfig(merchantId)

        fun india(merchantId: String) = SdkConfig(
            merchantId = merchantId,
            countryCode = "IN",
            mode = Mode.HYBRID
        )

        fun usa(merchantId: String) = SdkConfig(
            merchantId = merchantId,
            countryCode = "US",
            mode = Mode.IMMEDIATE
        )
    }
}
