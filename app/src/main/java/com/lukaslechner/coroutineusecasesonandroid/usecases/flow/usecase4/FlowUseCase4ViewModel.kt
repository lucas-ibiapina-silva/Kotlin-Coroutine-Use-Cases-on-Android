package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import timber.log.Timber

class FlowUseCase4ViewModel(
    private val stockPriceDataSource: StockPriceDataSource = StockPriceDataSource(),
    private val currencyRateDataSource: CurrencyRateDataSource = CurrencyRateDataSource()
) : BaseViewModel<UiState>() {


    private val currentGoogleStockPriceInEuroCombined: Flow<Float> =
        stockPriceDataSource.latestPrice.combine(currencyRateDataSource.latestRate) { googleStock, currencyRate ->
            googleStock.currentPriceUsd * currencyRate.usdInEuro
        }

    val combinedStateFlow = currentGoogleStockPriceInEuroCombined
        .onEach { stockPriceInEuro ->
            Timber.d("Calculated stock price in euro: $stockPriceInEuro")
        }.map { stockPriceInEuro ->
            UiState.Success(stockPriceInEuro)
        }.stateIn(
            initialValue = UiState.Loading,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    private val currentGoogleStockPriceInEuroZipped: Flow<Float> =
        stockPriceDataSource.latestPrice.zip(currencyRateDataSource.latestRate) { googleStock, currencyRate ->
            googleStock.currentPriceUsd * currencyRate.usdInEuro
        }

    val zippedStateFlow = currentGoogleStockPriceInEuroCombined
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
        stockPriceDataSource.latestPrice.combineTransform(currencyRateDataSource.latestRate) { googleStock, currencyRate ->
            emit(UiState.Loading)

            // simulate heavy calculation
            delay(2000)

            val stockPriceInEuro = googleStock.currentPriceUsd * currencyRate.usdInEuro
            emit(UiState.Success(stockPriceInEuro))
        }.stateIn(
            initialValue = UiState.Loading,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
}