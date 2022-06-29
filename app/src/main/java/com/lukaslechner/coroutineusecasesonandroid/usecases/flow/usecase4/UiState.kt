package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4

sealed class UiState {
    object Loading : UiState()
    data class Success(val stockPriceInEuro: Float) : UiState()
    data class Error(val message: String) : UiState()
}