package com.example.barbershop.data.database.subscription

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.barbershop.data.model.subscription.Currency
import com.example.barbershop.data.model.subscription.ExchangeRate

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<Currency>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExchangeRates(rates: List<ExchangeRate>)

    @Query("SELECT * FROM currencies")
    suspend fun getCurrencies(): List<Currency>

    @Query("SELECT * FROM exchange_rates WHERE fromCurrencyId = :from AND toCurrencyId = :to")
    suspend fun getExchangeRate(from: String, to: String): ExchangeRate?
}