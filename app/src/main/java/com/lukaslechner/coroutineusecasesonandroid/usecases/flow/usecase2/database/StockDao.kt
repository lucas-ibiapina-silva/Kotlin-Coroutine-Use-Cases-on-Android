package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase2.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StockDao {

    @Query("SELECT * FROM stock")
    suspend fun currentAlphabetStockPrices(): List<StockEntity>

    @Query("SELECT * FROM stock")
    fun stockPrices(): Flow<List<StockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stockEntity: StockEntity)

    @Query("DELETE FROM stock")
    suspend fun clear()
}