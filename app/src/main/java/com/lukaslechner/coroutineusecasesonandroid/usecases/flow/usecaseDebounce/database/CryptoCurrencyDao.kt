package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CryptoCurrencyDao {

    @Query("SELECT * FROM `crypto-currencies`")
    suspend fun currentCryptoCurrencyPrices(): List<CryptoCurrencyEntity>

    @Query("SELECT * FROM `crypto-currencies`")
    fun latestCryptoCurrencyPrices(): Flow<List<CryptoCurrencyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cryptoCurrencyPrices: List<CryptoCurrencyEntity>)

    @Query("DELETE FROM `crypto-currencies`")
    suspend fun clear()
}