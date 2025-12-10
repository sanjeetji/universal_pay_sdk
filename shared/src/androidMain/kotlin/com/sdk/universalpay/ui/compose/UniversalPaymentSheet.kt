package com.sdk.universalpay.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
                    "INIT_ERROR", e.message ?: "Failed to load gateways", "sdk"
                )
            )
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
        Text(
            text = "Select Payment Gateway",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Loading State
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Gateways Grid
        if (!isLoading && gateways.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(gateways) { gateway ->
                    GatewayGridItem(
                        gateway = gateway,
                        isSelected = selectedGateway?.id == gateway.id,
                        isProcessing = isProcessing,
                        onClick = {
                            selectedGateway =
                                if (selectedGateway?.id == gateway.id) null else gateway
                        },
                        onSelect = { selectedGateway = gateway })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { selectedGateway = null }, modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }

                Button(
                    onClick = {
                        selectedGateway?.let { gateway ->
                            scope.launch {
                                isProcessing = true
                                try {
                                    val result = universalPay.savePaymentMethod(
                                        customerId = customerId, gatewayId = gateway.id
                                    )
                                    onResult(result)
                                } catch (e: Exception) {
                                    onResult(
                                        PaymentResult.Failure(
                                            "SETUP_ERROR", e.message ?: "Setup failed", gateway.id
                                        )
                                    )
                                } finally {
                                    isProcessing = false
                                }
                            }
                        }
                    },
                    enabled = selectedGateway != null && !isProcessing,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Setup Payment")
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
                    text = "No payment gateways available",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun GatewayGridItem(
    gateway: PaymentGateway,
    isSelected: Boolean,
    isProcessing: Boolean,
    onClick: () -> Unit,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable(enabled = !isProcessing) { onClick() }, colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Gateway Icon
            if (gateway.iconUrl != null) {
                AsyncImage(
                    model = gateway.iconUrl,
                    contentDescription = gateway.name,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)
                        ),
                    contentScale = ContentScale.Fit
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        ), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = gateway.id.take(2).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = gateway.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = gateway.category.name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = MaterialTheme.typography.labelSmall.fontSize
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    painter = rememberVectorPainter(image = Icons.Default.Check),
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun UniversalPaymentSheetPreview() {
    MaterialTheme {
        UniversalPaymentSheet(
            customerId = "preview-user",
            amount = 499.0,
            currency = "INR",
            onResult = { /* no-op for preview */ },
            modifier = Modifier
        )
    }
}
