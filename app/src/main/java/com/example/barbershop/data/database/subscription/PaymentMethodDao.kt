package com.example.barbershop.data.database.subscription

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.barbershop.data.model.subscription.PaymentMethod
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentMethodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentMethod(paymentMethod: PaymentMethod)

    @Query("SELECT * FROM payment_methods WHERE id = :id")
    suspend fun getPaymentMethod(id: String): PaymentMethod?

    @Query("SELECT * FROM payment_methods")
    suspend fun getAllPaymentMethods(): List<PaymentMethod>

    @Query("DELETE FROM payment_methods WHERE id = :id")
    suspend fun deletePaymentMethod(id: String)

    @Query("DELETE FROM payment_methods")
    suspend fun deleteAllPaymentMethods()

    /** Синхронизация с сервером */

    @Query("SELECT * FROM payment_methods WHERE syncStatus = 'pending'")
    suspend fun getPendingPaymentMethods(): List<PaymentMethod>

    @Query("SELECT * FROM payment_methods WHERE operationType = 'delete' AND syncStatus = 'synced'")
    suspend fun getDeletedPaymentMethods(): List<PaymentMethod>

    @Query("UPDATE payment_methods SET syncStatus = :status WHERE id = :id")
    suspend fun updatePaymentMethodSyncStatus(id: String, status: String)

    @Query("UPDATE payment_methods SET syncStatus = :status, lastServerSyncTime = :timestamp WHERE id = :id")
    suspend fun updatePaymentMethodSyncStatusAndTime(id: String, status: String, timestamp: Long)

    @Query("UPDATE payment_methods SET lastServerSyncTime = 0")
    suspend fun clearPaymentMethodTime()

    @Query("SELECT * FROM payment_methods")
    fun getAllPaymentMethodsFlow(): Flow<List<PaymentMethod>>
}