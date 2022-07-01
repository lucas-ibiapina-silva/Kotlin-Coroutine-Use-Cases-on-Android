package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import timber.log.Timber

class FlowUseCase3ViewModel(
    dataSource: StockPriceDataSource = NetworkStockPriceDataSource()
) : ViewModel() {

    val currentStockPriceAsFlow: StateFlow<UiState> = dataSource.latestPrice
        .map { stockList ->
            UiState.Success(stockList)
        }.onEach {
            Timber.d("New value collected")
        }.stateIn(
            initialValue = UiState.Loading,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )
}