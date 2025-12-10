package com.sdk.universalpay.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
    var isProcessing by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            gateways = universalPay.getGateways()
        } catch (e: Exception) {
            onError(PaymentResult.Failure("INIT_ERROR", e.message ?: "Failed to load gateways", "sdk"))
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Payment,
                contentDescription = "Payment",
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Payment Methods",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        Divider(modifier = Modifier.padding(bottom = 16.dp))

        // Loading State
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading payment methods...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Gateways List
        if (!isLoading && gateways.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(gateways) { gateway ->
                    PaymentMethodItem(
                        gateway = gateway,
                        isProcessing = isProcessing == gateway.id,
                        onClick = {
                            scope.launch {
                                isProcessing = gateway.id
                                try {
                                    val result = universalPay.savePaymentMethod(
                                        customerId = customerId,
                                        gatewayId = gateway.id
                                    )

                                    when (result) {
                                        is PaymentResult.Success -> {
                                            if (result.paymentMethodId != null) {
                                                onSuccess(result.paymentMethodId!!)
                                            }
                                        }
                                        is PaymentResult.Failure -> onError(result)
                                        else -> {}
                                    }
                                } catch (e: Exception) {
                                    onError(
                                        PaymentResult.Failure(
                                            "SETUP_ERROR",
                                            e.message ?: "Setup failed",
                                            gateway.id
                                        )
                                    )
                                } finally {
                                    isProcessing = null
                                }
                            }
                        }
                    )
                }
            }
        }

        // Empty State
        if (!isLoading && gateways.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No payment methods available",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodItem(
    gateway: PaymentGateway,
    isProcessing: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(enabled = !isProcessing) { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gateway Icon
                if (!gateway.iconUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = gateway.iconUrl,
                        contentDescription = gateway.name,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = gateway.id.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = gateway.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = gateway.category.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Action Button
            if (isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Select",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun PaymentSheetPreview() {
    MaterialTheme {
        PaymentSheet(
            customerId = "preview-user",
            onSuccess = { /* no-op in preview */ },
            onError = { /* no-op in preview */ },
            modifier = Modifier
        )
    }
}