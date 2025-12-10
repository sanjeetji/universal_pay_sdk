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
fun UniversalPaymentSheet(
    customerId: String,
    amount: Double = 0.0,
    currency: String = "INR",
    onResult: (PaymentResult) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 3
) {
    val universalPay = UniversalPay.get()
    val scope = rememberCoroutineScope()
    var gateways by remember { mutableStateOf<List<PaymentGateway>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedGateway by remember { mutableStateOf<PaymentGateway?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            gateways = universalPay.getGateways()
        } catch (e: Exception) {
            onResult(
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

    UniversalPaymentSheetContent(
        modifier = modifier,
        gateways = gateways,
        isLoading = isLoading,
        selectedGateway = selectedGateway,
        isProcessing = isProcessing,
        columns = columns,
        onSelectGateway = { gateway ->
            selectedGateway = if (selectedGateway?.id == gateway.id) null else gateway
        },
        onClear = { selectedGateway = null },
        onSetupPayment = {
            val gateway = selectedGateway ?: return@UniversalPaymentSheetContent
            scope.launch {
                isProcessing = true
                try {
                    val result = universalPay.savePaymentMethod(
                        customerId = customerId,
                        gatewayId = gateway.id
                    )
                    onResult(result)
                } catch (e: Exception) {
                    onResult(
                        PaymentResult.Failure(
                            errorCode = "SETUP_ERROR",
                            message = e.message ?: "Setup failed",
                            gatewayId = gateway.id
                        )
                    )
                } finally {
                    isProcessing = false
                }
            }
        }
    )
}
