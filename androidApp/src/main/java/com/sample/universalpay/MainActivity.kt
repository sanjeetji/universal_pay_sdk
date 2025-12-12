package com.sample.universalpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sample.universalpay.ui.theme.UniversalpaySdkTheme
import com.sdk.universalpay.UniversalPay
import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult
import com.sdk.universalpay.storage.EncryptedSecureStorage
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        UniversalPay.initialize(
            config = SdkConfig.india("merchant_123"),
            storage = EncryptedSecureStorage(this)
        )

        setContent {
            UniversalpaySdkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UniversalPayDemo()
                }
            }
        }
    }
}

/*@Composable
fun UniversalPayDemo() {
    val universalPay = UniversalPay.get()
    val scope = rememberCoroutineScope()
    var uiState by remember { mutableStateOf(PaymentDemoState()) }
    var selectedGateway by remember { mutableStateOf<PaymentGateway?>(null) }

    LaunchedEffect(Unit) {
        uiState = try {
            uiState.copy(
                gateways = universalPay.getGateways(),
                isInitialized = true
            )
        } catch (e: Exception) {
            uiState.copy(
                lastResult = PaymentResult.Failure(
                    "INIT_ERROR",
                    e.message ?: "Initialization failed",
                    "sdk"
                )
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = "Payment",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "UniversalPay SDK Demo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "18+ Global Gateways",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (!uiState.isInitialized) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Initializing SDK...",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            OutlinedButton(
                onClick = {
                    selectedGateway?.let { gateway ->
                        scope.launch {
                            uiState = uiState.copy(isLoading = true)
                            try {
                                val result = universalPay.savePaymentMethod(
                                    customerId = "demo_user_123",
                                    gatewayId = gateway.id
                                )
                                uiState = uiState.copy(
                                    isLoading = false,
                                    lastResult = result,
                                    savedMethodId = if (result is PaymentResult.Success) result.paymentMethodId else null
                                )
                            } catch (e: Exception) {
                                uiState = uiState.copy(
                                    isLoading = false,
                                    lastResult = PaymentResult.Failure("SETUP_ERROR", e.message ?: "Setup failed", gateway.id)
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedGateway != null && !uiState.isLoading
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.CreditCard, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Setup Payment Method")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                onClick = {
                    uiState.savedMethodId?.let { savedMethodId ->
                        scope.launch {
                            uiState = uiState.copy(isLoading = true)
                            try {
                                val request = PaymentRequest(
                                    amount = 999.0,
                                    currency = "INR",
                                    orderId = "demo_order_${System.currentTimeMillis()}",
                                    customerId = "demo_user_123"
                                )
                                val result = universalPay.pay(request, savedMethodId)
                                uiState = uiState.copy(isLoading = false, lastResult = result)
                            } catch (e: Exception) {
                                uiState = uiState.copy(
                                    isLoading = false,
                                    lastResult = PaymentResult.Failure("PAY_ERROR", e.message ?: "Payment failed", "sdk")
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.savedMethodId != null && !uiState.isLoading
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Pay")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pay ₹999 (Saved Method)")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Select Gateway (${uiState.gateways.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (uiState.gateways.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No gateways available", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(uiState.gateways) { gateway ->
                GatewayCard(gateway = gateway, isSelected = selectedGateway?.id == gateway.id, onClick = { selectedGateway = gateway })
            }
        }

        uiState.lastResult?.let { result ->
            item {
                Spacer(modifier = Modifier.height(24.dp))
                ResultCard(result = result)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}*/

@Composable
fun UniversalPayDemo() {
    val universalPay = UniversalPay.get()
    val scope = rememberCoroutineScope()

    var uiState by remember { mutableStateOf(PaymentDemoState()) }
    var selectedGateway by remember { mutableStateOf<PaymentGateway?>(null) }
    var savedMethods by remember { mutableStateOf<Map<String, List<String>>>(emptyMap()) } // gatewayId -> methodIds

    LaunchedEffect(Unit) {
        uiState = try {
            val gateways = universalPay.getGateways()
            val methodsPerGateway = gateways.associate { gateway ->
                gateway.id to emptyList<String>()
            }
            uiState.copy(
                gateways = gateways,
                isInitialized = true
            ).also { savedMethods = methodsPerGateway }
        } catch (e: Exception) {
            uiState.copy(
                lastResult = PaymentResult.Failure(
                    "INIT_ERROR",
                    e.message ?: "Initialization failed",
                    "sdk"
                )
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = "Payment",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "UniversalPay SDK Demo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${uiState.gateways.size} Gateways Connected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Init status
        if (!uiState.isInitialized) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Initializing SDK...",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Selected gateway info + actions
        item {
            SelectedGatewaySection(
                selectedGateway = selectedGateway,
                uiState = uiState,
                isLoading = uiState.isLoading,
                onSetupClick = { gateway ->
                    scope.launch {
                        uiState = uiState.copy(isLoading = true)
                        try {
                            val result = universalPay.savePaymentMethod(
                                customerId = "demo_user_123",
                                gatewayId = gateway.id
                            )
                            if (result is PaymentResult.Success && result.paymentMethodId != null) {
                                val updated = savedMethods.toMutableMap()
                                val list = updated[gateway.id].orEmpty() + result.paymentMethodId!!
                                updated[gateway.id] = list
                                savedMethods = updated
                            }
                            uiState = uiState.copy(
                                isLoading = false,
                                lastResult = result,
                                savedMethodId = if (result is PaymentResult.Success) result.paymentMethodId else null
                            )
                        } catch (e: Exception) {
                            uiState = uiState.copy(
                                isLoading = false,
                                lastResult = PaymentResult.Failure(
                                    "SETUP_ERROR",
                                    e.message ?: "Setup failed",
                                    gateway.id
                                )
                            )
                        }
                    }
                },
                onPayClick = { gateway, methodId ->
                    scope.launch {
                        uiState = uiState.copy(isLoading = true)
                        try {
                            val request = PaymentRequest(
                                amount = 999.0,
                                currency = "INR",
                                orderId = "demo_order_${System.currentTimeMillis()}",
                                customerId = "demo_user_123"
                            )
                            val result = universalPay.pay(request, methodId)
                            uiState = uiState.copy(
                                isLoading = false,
                                lastResult = result
                            )
                        } catch (e: Exception) {
                            uiState = uiState.copy(
                                isLoading = false,
                                lastResult = PaymentResult.Failure(
                                    "PAY_ERROR",
                                    e.message ?: "Payment failed",
                                    "sdk"
                                )
                            )
                        }
                    }
                },
                savedMethodsForGateway = { gatewayId -> savedMethods[gatewayId].orEmpty() }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Gateway selector
        item {
            Text(
                text = "Gateways (${uiState.gateways.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (uiState.gateways.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No gateways available",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(uiState.gateways) { gateway ->
                GatewayCard(
                    gateway = gateway,
                    isSelected = selectedGateway?.id == gateway.id,
                    onClick = { selectedGateway = gateway }
                )
            }
        }

        // Result
        uiState.lastResult?.let { result ->
            item {
                Spacer(modifier = Modifier.height(24.dp))
                ResultCard(result = result)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SelectedGatewaySection(
    selectedGateway: PaymentGateway?,
    uiState: PaymentDemoState,
    isLoading: Boolean,
    onSetupClick: (PaymentGateway) -> Unit,
    onPayClick: (PaymentGateway, String?) -> Unit,
    savedMethodsForGateway: (String) -> List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Selected Gateway",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (selectedGateway == null) {
                Text(
                    text = "Tap a gateway below to select it.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                return@Column
            }

            Text(
                text = "${selectedGateway.name} (${selectedGateway.category.name})",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            // Options (per-gateway)
            val options = remember(selectedGateway.id) {
                selectedGateway.getOptions(countryCode = "IN")
            }

            if (options.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Options:",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                val options = remember(selectedGateway.id) {
                    selectedGateway.getOptions(countryCode = "IN")
                }

                if (options.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Options (${options.size}):",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Column {
                        options.forEachIndexed { index, _ ->
                            Text(
                                text = "• Option ${index + 1}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Saved methods for this gateway
            val methods = savedMethodsForGateway(selectedGateway.id)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Saved Methods: ${methods.size}",
                style = MaterialTheme.typography.labelLarge
            )
            if (methods.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Column {
                    methods.forEach { id ->
                        Text(
                            text = "• $id",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Setup method
            OutlinedButton(
                onClick = { onSetupClick(selectedGateway) },
                enabled = !isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = "Save"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Setup Payment Method")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pay using last saved or any method id
            Button(
                onClick = {
                    val methodId = methods.lastOrNull() ?: uiState.savedMethodId
                    onPayClick(selectedGateway, methodId)
                },
                enabled = !isLoading && (methods.isNotEmpty() || uiState.savedMethodId != null)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Pay"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pay ₹999")
                }
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Processing...", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}



@Composable
fun GatewayCard(gateway: PaymentGateway, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = gateway.id.take(2).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = gateway.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                Text(text = gateway.category.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            }
        }
    }
}

// Preview-safe sealed class for PaymentCardState
sealed class PaymentCardState {
    abstract val color: Color
    abstract val title: String
    abstract val subtitle: String
    open val icon: ImageVector? = null

    data class Success(
        override val color: Color,
        val transactionId: String
    ) : PaymentCardState() {
        override val title: String = "Payment Successful"
        override val subtitle: String = "Transaction ID: $transactionId"
        override val icon: ImageVector = Icons.Default.CheckCircle
    }

    data class Failure(
        override val color: Color,
        val errorCode: String,
        val message: String
    ) : PaymentCardState() {
        override val title: String = "Payment Failed"
        override val subtitle: String = "$errorCode: $message"
        override val icon: ImageVector = Icons.Default.Error
    }

    data class Cancelled(
        override val color: Color
    ) : PaymentCardState() {
        override val title: String = "Payment Cancelled"
        override val subtitle: String = "The payment was cancelled by the user."
    }
}

@Composable
fun ResultCard(result: PaymentResult) {
    val state = when (result) {
        is PaymentResult.Success -> PaymentCardState.Success(
            color = MaterialTheme.colorScheme.primaryContainer,
            transactionId = result.transactionId
        )
        is PaymentResult.Failure -> PaymentCardState.Failure(
            color = MaterialTheme.colorScheme.errorContainer,
            errorCode = result.errorCode,
            message = result.message.orEmpty()
        )
        is PaymentResult.Cancelled -> PaymentCardState.Cancelled(
            color = MaterialTheme.colorScheme.secondaryContainer
        )
        // Exhaustive fallback for any unknown/unhandled sealed subclasses
        else -> PaymentCardState.Failure(
            color = MaterialTheme.colorScheme.errorContainer,
            errorCode = "UNKNOWN",
            message = "Unhandled payment result"
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = state.color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                state.icon?.let { icon ->
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Immutable
data class PaymentDemoState(
    val gateways: List<PaymentGateway> = emptyList(),
    val isInitialized: Boolean = false,
    val isLoading: Boolean = false,
    val savedMethodId: String? = null,
    val lastResult: PaymentResult? = null
)

// Preview-only implementation of PaymentGateway
private data class PreviewGateway(
    override val id: String,
    override val name: String,
    override val category: GatewayCategory,
    override val iconUrl: String? = null,
    override val countries: List<String> = listOf("IN")
) : PaymentGateway {
    override suspend fun setupPaymentMethod(customerId: String, request: PaymentRequest): PaymentResult =
        PaymentResult.Success("preview_setup_tx", id, request.amount, "preview_pm")

    override suspend fun executePayment(request: PaymentRequest, savedMethodId: String?): PaymentResult =
        PaymentResult.Success("preview_pay_tx", id, request.amount, savedMethodId)

    override fun getOptions(countryCode: String): List<PaymentOption> = emptyList()
}

@Composable
private fun UniversalPayDemoPreviewContent(uiState: PaymentDemoState) {
    var selectedGateway by remember { mutableStateOf<PaymentGateway?>(null) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("UniversalPay SDK Demo (Preview)", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.gateways.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No gateways available", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(uiState.gateways) { gateway ->
                GatewayCard(gateway = gateway, isSelected = selectedGateway?.id == gateway.id, onClick = { selectedGateway = gateway })
            }
        }

        uiState.lastResult?.let { result ->
            item {
                Spacer(modifier = Modifier.height(24.dp))
                ResultCard(result = result)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
fun UniversalPayDemoPreview() {
    UniversalpaySdkTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val dummyGateways = listOf(
                PreviewGateway(id = "razorpay", name = "Razorpay", category = GatewayCategory.CARD),
                PreviewGateway(id = "stripe", name = "Stripe", category = GatewayCategory.CARD)
            )

            val uiState = PaymentDemoState(
                gateways = dummyGateways,
                isInitialized = true,
                isLoading = false,
                savedMethodId = "saved_123",
                lastResult = PaymentResult.Success(
                    transactionId = "tx_123",
                    gatewayId = "razorpay",
                    amount = 999.0,
                    paymentMethodId = "pm_123"
                )
            )

            UniversalPayDemoPreviewContent(uiState = uiState)
        }
    }
}