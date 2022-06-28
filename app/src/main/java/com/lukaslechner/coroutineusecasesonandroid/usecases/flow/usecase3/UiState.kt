package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase3

import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.GoogleStock

sealed class UiState {
    object Loading : UiState()
    data class Success(val googleStockList: List<GoogleStock>) : UiState()
    data class Error(val message: String) : UiState()
}