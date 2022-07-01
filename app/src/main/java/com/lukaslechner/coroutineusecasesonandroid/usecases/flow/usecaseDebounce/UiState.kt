package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.StockListing

sealed class UiState {
    object Loading : UiState()
    data class Success(val stockList: List<StockListing>) : UiState()
    data class Error(val message: String) : UiState()
}