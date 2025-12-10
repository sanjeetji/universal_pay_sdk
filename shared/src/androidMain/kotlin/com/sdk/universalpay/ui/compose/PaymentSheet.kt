package com.sdk.universalpay.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sdk.universalpay.UniversalPay
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.model.PaymentResult
import kotlinx.coroutines.launch

@Composable
fun PaymentSheet(
    customerId: String,
    onSuccess: (String) -> Unit,
    onError: (PaymentResult.Failure) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val universalPay = UniversalPay.get()
    val scope = rememberCoroutineScope()

    var gateways by remember { mutableStateOf<List<PaymentGateway>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var processingGatewayId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            gateways = universalPay.getGateways()
        } catch (e: Exception) {
            onError(
                PaymentResult.Failure(
                    errorCode = "INIT_ERROR",
                    message = e.message ?: "Failed to load gateways",
                    gatewayId = "sdk"
                )
            )
        } finally {
            isLoading = false
        }
    }

    PaymentSheetContent(
        modifier = modifier,
        gateways = gateways,
        isLoading = isLoading,
        processingGatewayId = processingGatewayId,
        onGatewayClick = { gateway ->
            scope.launch {
                processingGatewayId = gateway.id
                try {
                    val result = universalPay.savePaymentMethod(
                        customerId = customerId,
                        gatewayId = gateway.id
                    )
                    when (result) {
                        is PaymentResult.Success -> result.paymentMethodId?.let(onSuccess)
                        is PaymentResult.Failure -> onError(result)
                        PaymentResult.Cancelled -> {
                            // ignore / handle if needed
                        }
                    }
                } catch (e: Exception) {
                    onError(
                        PaymentResult.Failure(
                            errorCode = "SETUP_ERROR",
                            message = e.message ?: "Setup failed",
                            gatewayId = gateway.id
                        )
                    )
                } finally {
                    processingGatewayId = null
                }
            }
        }
    )
}
