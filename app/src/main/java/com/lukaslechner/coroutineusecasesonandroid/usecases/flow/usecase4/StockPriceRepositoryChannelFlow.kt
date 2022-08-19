package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4

import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4.database.StockDao
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4.database.mapToEntityList
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4.database.mapToUiModelList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import timber.log.Timber

class StockPriceRepositoryChannelFlow(
    val remoteDataSource: StockPriceDataSource,
    private val localDataSource: StockDao,

    // TODO: Use applicationScope
    scope: CoroutineScope = CoroutineScope(SupervisorJob())
) {

    val channelFlow = channelFlow {
        remoteDataSource
            .latestPrice
            .onEach { stockList ->
                Timber.d("Insert into db")
                localDataSource.insert(stockList.mapToEntityList())
            }
            .launchIn(this)
            .invokeOnCompletion {
                Timber.d("Stock Fetching Flow completed")
            }

        localDataSource
            .stockPrices()
            .map { it.mapToUiModelList() }
            .onEach { stockList ->
                send(stockList)
            }
            .launchIn(this)
            .invokeOnCompletion {
                Timber.d("Forward db items flow completed")
            }
    }
        .shareIn(scope, SharingStarted.WhileSubscribed(), 0)
        .onCompletion {
            Timber.d("SharedFlow completed")
        }
}