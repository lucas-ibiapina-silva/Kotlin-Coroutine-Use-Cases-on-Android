package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.useCaseCallbackFlow

import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock

sealed class UiState {
    object Loading : UiState()
    data class Success(val stock: Stock) : UiState()
    data class Error(val message: String) : UiState()
}