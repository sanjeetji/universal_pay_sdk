package com.sdk.universalpay.gateway.impl

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.sdk.universalpay.config.SdkConfig
import com.sdk.universalpay.gateway.GatewayCategory
import com.sdk.universalpay.gateway.PaymentGateway
import com.sdk.universalpay.gateway.PaymentOption
import com.sdk.universalpay.model.PaymentRequest
import com.sdk.universalpay.model.PaymentResult
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class UpiIntentGateway(
    private val config: SdkConfig,
    private val context: Context
) : PaymentGateway {

    override val id: String = "upi_intent"
    override val name: String = "UPI Apps"
    override val iconUrl: String? = null
    override val category: GatewayCategory = GatewayCategory.UPI
    override val countries: List<String> = listOf("IN")

    override suspend fun setupPaymentMethod(
        customerId: String,
        request: PaymentRequest
    ): PaymentResult {
        return PaymentResult.Success(
            transactionId = "upi_setup_${customerId}",
            gatewayId = id,
            amount = 0.0
        )
    }

    override suspend fun executePayment(
        request: PaymentRequest,
        savedMethodId: String?
    ): PaymentResult = suspendCancellableCoroutine { continuation ->
        val upiApps = getInstalledUpiApps()
        if (upiApps.isEmpty()) {
            continuation.resume(PaymentResult.Failure(
                errorCode = "NO_UPI_APPS",
                message = "No UPI apps found",
                gatewayId = id
            ))
            return@suspendCancellableCoroutine
        }

        try {
            val componentActivity = context as? ComponentActivity ?: run {
                continuation.resume(PaymentResult.Failure(
                    errorCode = "INVALID_CONTEXT",
                    message = "Context must be ComponentActivity",
                    gatewayId = id
                ))
                return@suspendCancellableCoroutine
            }

            val upiApp = upiApps.first()
            val upiUri = createUpiUri(request)
            val intent = Intent(Intent.ACTION_VIEW, upiUri).apply {
                setPackage(upiApp.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val resultLauncher = componentActivity.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val paymentResult = when (result.resultCode) {
                    Activity.RESULT_OK -> {
                        val status = result.data?.getStringExtra("Status")?.lowercase() ?: ""
                        val txnId = result.data?.getStringExtra("txnId") ?: ""

                        if (status.contains("success")) {
                            PaymentResult.Success(
                                transactionId = txnId.ifEmpty { "UPI_${System.currentTimeMillis()}" },
                                gatewayId = id,
                                amount = request.amount
                            )
                        } else {
                            PaymentResult.Failure(
                                errorCode = status.ifEmpty { "UPI_FAILED" },
                                message = "Payment failed: $status",
                                gatewayId = id
                            )
                        }
                    }
                    Activity.RESULT_CANCELED -> PaymentResult.Cancelled
                    else -> PaymentResult.Failure(
                        errorCode = "UPI_REQUEST_FAILED",
                        message = "Request failed",
                        gatewayId = id
                    )
                }
                continuation.resume(paymentResult)
            }

            resultLauncher.launch(intent)

        } catch (e: Exception) {
            continuation.resume(PaymentResult.Failure(
                errorCode = "UPI_ERROR",
                message = e.message ?: "UPI error",
                gatewayId = id
            ))
        }
    }

    override fun getOptions(countryCode: String): List<PaymentOption> {
        return getInstalledUpiApps().map { app ->
            PaymentOption(
                id = app.packageName,
                name = app.appName,
                gatewayId = id,
                category = GatewayCategory.UPI
            )
        }
    }

    private fun getInstalledUpiApps(): List<UpiAppInfo> {
        val pm = context.packageManager
        val upiPackages = listOf(
            "com.phonepe.app", "net.one97.paytm", "com.google.android.apps.nbu.paisa.user",
            "com.paytm.app.wallet", "com.amazon.venezia", "com.whatsapp"
        )
        return upiPackages.mapNotNull { pkg ->
            try {
                @Suppress("DEPRECATION")
                val appInfo = pm.getApplicationInfo(pkg, 0)
                UpiAppInfo(pkg, pm.getApplicationLabel(appInfo).toString())
            } catch (e: Exception) { null }
        }
    }

    private fun createUpiUri(request: PaymentRequest): Uri {
        return Uri.parse("""
            upi://pay?pa=merchant@ybl&pn=Merchant&am=${request.amount}&cu=${request.currency}&tn=Order ${request.orderId}
        """.trimIndent())
    }

    private data class UpiAppInfo(val packageName: String, val appName: String)
}
