package com.sdk.universalpay.storage

import com.sdk.universalpay.model.SavedPaymentMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSKeyedArchiver
import platform.Foundation.NSKeyedUnarchiver
import platform.Foundation.NSMutableDictionary
import platform.Security.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import platform.Foundation.NSData

actual class EncryptedSecureStorage {

    private val userDefaults = NSUserDefaults.standardUserDefaults()
    private val json = Json { ignoreUnknownKeys = true }
    private val keychainService = "com.universalpay.payment_methods"

    actual suspend fun saveMethod(customerId: String, method: SavedPaymentMethod) =
        withContext(Dispatchers.Default) {
            try {
                val key = buildKey(customerId, method.gatewayId)
                val jsonString = json.encodeToString(method)

                // Save to Keychain for sensitive data
                val query = NSMutableDictionary().apply {
                    setObject(kSecClassGenericPassword, kSecClass)
                    setObject(key, kSecAttrAccount)
                    setObject(keychainService, kSecAttrService)
                    setObject(jsonString.toNSData(), kSecValueData)
                }

                SecItemDelete(query)
                SecItemAdd(query, null)

            } catch (e: Exception) {
                e.printStackTrace()
                throw SaveMethodException("Failed to save payment method: ${e.message}", e)
            }
        }

    actual suspend fun getMethods(customerId: String): List<SavedPaymentMethod> =
        withContext(Dispatchers.Default) {
            try {
                val prefix = "payment_method_${customerId}_"
                val methods = mutableListOf<SavedPaymentMethod>()

                // Query Keychain
                val query = NSMutableDictionary().apply {
                    setObject(kSecClassGenericPassword, kSecClass)
                    setObject(keychainService, kSecAttrService)
                    setObject(kSecMatchLimitAll, kSecMatchLimit)
                    setObject(true, kSecReturnData)
                }

                val result = mutableListOf<Any?>()
                SecItemCopyMatching(query, result)

                result.forEach { item ->
                    if (item is NSData) {
                        try {
                            val jsonString = item.toNSString()
                            val method = json.decodeFromString<SavedPaymentMethod>(jsonString)
                            if (method.paymentMethodId.startsWith(prefix)) {
                                methods.add(method)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                methods

            } catch (e: Exception) {
                e.printStackTrace()
                throw GetMethodsException("Failed to retrieve payment methods: ${e.message}", e)
            }
        }

    actual suspend fun deleteMethod(customerId: String, gatewayId: String) =
        withContext(Dispatchers.Default) {
            try {
                val key = buildKey(customerId, gatewayId)
                val query = NSMutableDictionary().apply {
                    setObject(kSecClassGenericPassword, kSecClass)
                    setObject(key, kSecAttrAccount)
                    setObject(keychainService, kSecAttrService)
                }

                SecItemDelete(query)

            } catch (e: Exception) {
                e.printStackTrace()
                throw DeleteMethodException("Failed to delete payment method: ${e.message}", e)
            }
        }

    actual suspend fun clearAll() = withContext(Dispatchers.Default) {
        try {
            val query = NSMutableDictionary().apply {
                setObject(kSecClassGenericPassword, kSecClass)
                setObject(keychainService, kSecAttrService)
            }

            SecItemDelete(query)

        } catch (e: Exception) {
            e.printStackTrace()
            throw ClearException("Failed to clear payment methods: ${e.message}", e)
        }
    }

    private fun buildKey(customerId: String, gatewayId: String): String =
        "payment_method_${customerId}_${gatewayId}"

    private fun String.toNSData(): NSData =
        this.toByteArray().toNSData()

    private fun NSData.toNSString(): String {
        val bytes = this.bytes ?: return ""
        return String(bytes as ByteArray, Charsets.UTF_8)
    }
}

// Custom Exceptions
class SaveMethodException(message: String, cause: Throwable? = null) : Exception(message, cause)
class GetMethodsException(message: String, cause: Throwable? = null) : Exception(message, cause)
class DeleteMethodException(message: String, cause: Throwable? = null) : Exception(message, cause)
class ClearException(message: String, cause: Throwable? = null) : Exception(message, cause)
