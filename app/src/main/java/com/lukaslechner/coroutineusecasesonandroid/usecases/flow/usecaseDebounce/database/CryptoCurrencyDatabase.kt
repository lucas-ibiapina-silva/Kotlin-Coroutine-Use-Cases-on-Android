package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CryptoCurrencyEntity::class], version = 1, exportSchema = false)
abstract class CryptoCurrencyDatabase : RoomDatabase() {

    abstract fun cryptoCurrencyDao(): CryptoCurrencyDao

    companion object {
        private var INSTANCE: CryptoCurrencyDatabase? = null

        fun getInstance(context: Context): CryptoCurrencyDatabase {
            if (INSTANCE == null) {
                synchronized(CryptoCurrencyDatabase::class) {
                    INSTANCE = buildRoomDb(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildRoomDb(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CryptoCurrencyDatabase::class.java,
                "crypto-currencies.db"
            ).build()

    }

}

