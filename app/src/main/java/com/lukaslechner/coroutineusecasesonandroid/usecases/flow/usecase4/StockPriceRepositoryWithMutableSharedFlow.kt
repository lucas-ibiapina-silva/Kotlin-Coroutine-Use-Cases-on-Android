package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4

import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4.database.StockDao
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4.database.mapToEntityList
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4.database.mapToUiModelList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import timber.log.Timber

class StockPriceRepositoryWithMutableSharedFlow(
    val remoteDataSource: StockPriceDataSource,
    private val localDataSource: StockDao,

    // TODO: Use applicationScope
    scope: CoroutineScope = CoroutineScope(SupervisorJob())
) {

    private val _mutableSharedFlow = MutableSharedFlow<List<Stock>>(replay = 1)
    val mutableSharedFlow = _mutableSharedFlow.asSharedFlow()

    private var dbUpdatingJob: Job? = null

    init {
        localDataSource
            .stockPrices()
            .map { it.mapToUiModelList() }
            .onEach { stockList ->
                _mutableSharedFlow.emit(stockList)
            }
            .launchIn(scope)

        _mutableSharedFlow
            .subscriptionCount
            .onEach { subscriptionCount ->
                Timber.d("subscriberCount: $subscriptionCount")
                dbUpdatingJob = if (dbUpdatingJob == null && subscriptionCount > 0) {
                    Timber.d("Start stock data fetching")
                    remoteDataSource
                        .latestPrice
                        .onEach { stockList ->
                            Timber.d("Insert fetched stock data into db")
                            localDataSource.insert(stockList.mapToEntityList())
                        }
                        .launchIn(scope)
                } else {
                    if (subscriptionCount == 0) {
                        Timber.d("Stop stock data fetching")
                        dbUpdatingJob?.cancel()
                        null
                    } else {
                        dbUpdatingJob
                    }
                }
            }
            .launchIn(scope)
    }
}