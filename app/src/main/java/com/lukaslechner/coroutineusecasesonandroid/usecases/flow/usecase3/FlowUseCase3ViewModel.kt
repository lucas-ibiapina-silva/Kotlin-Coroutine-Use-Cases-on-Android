package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.flow.*
import timber.log.Timber

class FlowUseCase3ViewModel(
    dataSource: StockPriceDataSource = NetworkStockPriceDataSource()
) : BaseViewModel<UiState>() {



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