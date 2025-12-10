package com.sdk.universalpay.storage

import com.sdk.universalpay.model.SavedPaymentMethod

expect class EncryptedSecureStorage {
    suspend fun saveMethod(customerId: String, method: SavedPaymentMethod)
    suspend fun getMethods(customerId: String): List<SavedPaymentMethod>
    suspend fun deleteMethod(customerId: String, gatewayId: String)
    suspend fun clearAll()
}
