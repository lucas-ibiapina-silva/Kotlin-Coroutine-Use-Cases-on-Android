package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4

import com.lukaslechner.coroutineusecasesonandroid.playground.structuredconcurrency.scope
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4.database.StockDao
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4.database.mapToEntityList
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4.database.mapToUiModelList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

class StockPriceRepository(
    val remoteDataSource: StockPriceDataSource,
    private val localDataSource: StockDao,

    // TODO: Use applicationScope
    scope: CoroutineScope = CoroutineScope(SupervisorJob())
) {

    val latestPriceSimple: Flow<List<Stock>> = remoteDataSource
        .latestPrice
        .onStart {
            val fromDb = localDataSource.currentStockPrices()
            if (fromDb.isNotEmpty()) {
                emit(fromDb.mapToUiModelList())
            }
        }
        .onEach {
            localDataSource.insert(it.mapToEntityList())
        }

    private var dbUpdatingJob: Job? = null

    val latestPriceCoroutine: SharedFlow<List<Stock>> = localDataSource
        .stockPrices()
        .map { it.mapToUiModelList() }
        .onStart {
            startStockDataFetching()
        }.onCompletion {
            Timber.d("Flow completed")
            stopStockDataFetching()
        }.shareIn(scope, SharingStarted.WhileSubscribed(), 0)

    private fun startStockDataFetching() {
        dbUpdatingJob = remoteDataSource
            .latestPrice
            .onEach { stockList ->
                Timber.d("Insert into db")
                localDataSource.insert(stockList.mapToEntityList())
            }.launchIn(scope)

        dbUpdatingJob!!.invokeOnCompletion {
            Timber.d("Coroutine completed")
        }
    }

    private fun stopStockDataFetching() {
        dbUpdatingJob?.cancel()
    }

    val latestPriceCoroutineSuspending: Flow<List<Stock>> = localDataSource
        .stockPrices()
        .map {
            Timber.d("Mapping")
            it.mapToUiModelList()
        }
        .onStart {
            Timber.d("Launching new Coroutine")
            coroutineScope {
                launch(Dispatchers.Default) {
                    remoteDataSource.latestPrice.collect { stockList ->
                        Timber.d("Insert into db")
                        localDataSource.insert(stockList.mapToEntityList())
                    }
                }.invokeOnCompletion {
                    Timber.d("Coroutine completed")
                }
            }
        }.onCompletion {
            Timber.d("Flow completed")
        }

}