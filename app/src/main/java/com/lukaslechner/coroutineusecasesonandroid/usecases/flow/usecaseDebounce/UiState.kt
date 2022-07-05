package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.CryptoCurrency

sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Success(val stockList: List<CryptoCurrency>) : UiState()
    data class Error(val message: String) : UiState()
}