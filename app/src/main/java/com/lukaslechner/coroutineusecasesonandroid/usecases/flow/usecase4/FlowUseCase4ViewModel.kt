package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import timber.log.Timber

class FlowUseCase4ViewModel(
    private val stockPriceDataSource: StockPriceDataSource = StockPriceDataSource(),
    private val currencyRateDataSource: CurrencyRateDataSource = CurrencyRateDataSource()
) : ViewModel() {


    private val currentStockPriceInEuroCombined: Flow<Float> =
        stockPriceDataSource.latestPrice.combine(currencyRateDataSource.latestRate) { stock, currencyRate ->
            stock.currentPriceUsd * currencyRate.usdInEuro
        }

    val combinedStateFlow = currentStockPriceInEuroCombined
        .onEach { stockPriceInEuro ->
            Timber.d("Calculated stock price in euro: $stockPriceInEuro")
        }.map { stockPriceInEuro ->
            UiState.Success(stockPriceInEuro)
        }.stateIn(
            initialValue = UiState.Loading,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    private val currentStockPriceInEuroZipped: Flow<Float> =
        stockPriceDataSource.latestPrice.zip(currencyRateDataSource.latestRate) { stock, currencyRate ->
            stock.currentPriceUsd * currencyRate.usdInEuro
        }

    val zippedStateFlow = currentStockPriceInEuroCombined
        .onEach { stockPriceInEuro ->
            Timber.d("Calculated stock price in euro: $stockPriceInEuro")
        }.map { stockPriceInEuro ->
            UiState.Success(stockPriceInEuro)
        }.stateIn(
            initialValue = UiState.Loading,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    val combineTransformStateFlow =
        stockPriceDataSource.latestPrice.combineTransform(currencyRateDataSource.latestRate) { stock, currencyRate ->
            emit(UiState.Loading)

            // simulate heavy calculation
            delay(2000)

            val stockPriceInEuro = stock.currentPriceUsd * currencyRate.usdInEuro
            emit(UiState.Success(stockPriceInEuro))
        }.stateIn(
            initialValue = UiState.Loading,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
}