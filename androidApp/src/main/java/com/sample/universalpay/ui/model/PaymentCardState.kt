/*
package com.sample.universalpay.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme

sealed class PaymentCardState(
    open val color: Color,
    open val icon: ImageVector?,
    open val title: String,
    open val subtitle: String
) {
    data class Success(
        override val color: Color,
        val transactionId: String
    ) : PaymentCardState(
        color = color,
        icon = Icons.Default.CheckCircle,
        title = "✅ Payment Success",
        subtitle = "TXN: $transactionId"
    )

    data class Failure(
        override val color: Color,
        val errorCode: String,
        val message: String
    ) : PaymentCardState(
        color = color,
        icon = null,
        title = "❌ Payment Failed",
        subtitle = "$errorCode: $message"
    )

    data class Cancelled(
        override val color: Color
    ) : PaymentCardState(
        color = color,
        icon = null,
        title = "⏸️ Cancelled",
        subtitle = "Payment was cancelled"
    )
}
*/
