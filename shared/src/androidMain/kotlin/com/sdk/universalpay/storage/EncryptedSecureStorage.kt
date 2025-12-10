package com.sdk.universalpay.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.sdk.universalpay.model.SavedPaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class EncryptedSecureStorage(private val context: Context) {

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    // ✅ FIXED: Use SharedPreferences (EncryptedSharedPreferences extends it)
    private val encryptedSharedPreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "payment_methods_encrypted",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        ) as SharedPreferences
    }

    private val gson: Gson by lazy { Gson() }

    actual suspend fun saveMethod(
        customerId: String,
        method: SavedPaymentMethod
    ): Unit = withContext(Dispatchers.IO) {
        try {
            val key = buildKey(customerId, method.gatewayId)
            val json = gson.toJson(method)
            encryptedSharedPreferences.edit().putString(key, json).apply()
        } catch (e: Exception) {
            e.printStackTrace()
            throw SaveMethodException("Failed to save payment method: ${e.message}", e)
        }
    }

    actual suspend fun getMethods(customerId: String): List<SavedPaymentMethod> =
        withContext(Dispatchers.IO) {
            try {
                val prefix = "payment_method_${customerId}_"
                encryptedSharedPreferences.all
                    .filterKeys { key -> key.startsWith(prefix) }
                    .mapNotNull { (_, value) ->
                        try {
                            val jsonString = value as? String ?: return@mapNotNull null
                            gson.fromJson(jsonString, SavedPaymentMethod::class.java)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                throw GetMethodsException("Failed to retrieve payment methods: ${e.message}", e)
            }
        }

    actual suspend fun deleteMethod(
        customerId: String,
        gatewayId: String
    ): Unit = withContext(Dispatchers.IO) {
        try {
            val key = buildKey(customerId, gatewayId)
            encryptedSharedPreferences.edit().remove(key).apply()
        } catch (e: Exception) {
            e.printStackTrace()
            throw DeleteMethodException("Failed to delete payment method: ${e.message}", e)
        }
    }

    actual suspend fun clearAll(): Unit = withContext(Dispatchers.IO) {
        try {
            encryptedSharedPreferences.edit().clear().apply()
        } catch (e: Exception) {
            e.printStackTrace()
            throw ClearException("Failed to clear payment methods: ${e.message}", e)
        }
    }

    private fun buildKey(customerId: String, gatewayId: String): String =
        "payment_method_${customerId}_${gatewayId}"
}

// Custom Exceptions
class SaveMethodException(message: String, cause: Throwable? = null) : Exception(message, cause)
class GetMethodsException(message: String, cause: Throwable? = null) : Exception(message, cause)
class DeleteMethodException(message: String, cause: Throwable? = null) : Exception(message, cause)
class ClearException(message: String, cause: Throwable? = null) : Exception(message, cause)
