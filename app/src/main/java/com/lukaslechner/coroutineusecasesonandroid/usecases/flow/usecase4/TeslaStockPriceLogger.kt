package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class TeslaStockPriceLogger(
    private val stockPriceRepository: StockPriceRepositoryWithMutableSharedFlow,

    // TODO: use applicationScope?
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob())
) {

    var loggingJob: Job? = null

    fun startLogging() {
        loggingJob = stockPriceRepository
            .mutableSharedFlow
            .onEach { stockList ->
                val teslaStock = stockList.find { stock -> stock.name == "Tesla" }
                Timber.d("Tesla Price: ${teslaStock?.currentPrice}")
            }
            .launchIn(scope)
    }

    fun stopLogging() {
        loggingJob?.cancel()
    }

}

