package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import kotlinx.coroutines.flow.*
import timber.log.Timber

class DebounceViewModel(
    private val mockApi: FlowMockApi = mockApi()
) : ViewModel() {

    private var searchInputFlow: Flow<String>? = null

    val uiState = MutableStateFlow<UiState>(UiState.Initial)

    fun setSearchInputFlow(searchInputFlow: Flow<String>) {
        this.searchInputFlow = searchInputFlow

        searchInputFlow
            .onEach { Timber.d("Search Term: $it") }
            .filter { searchTerm -> searchTerm.length > 2 }
            .onEach {
                uiState.value = UiState.Loading
            }
            .debounce(1000)
            .onEach { searchTerm ->
                Timber.d("Starting network request with search term: $searchTerm")
                val stockList = mockApi.getAllCurrentStockPrices(searchTerm)

                uiState.value = UiState.Success(stockList)
            }.launchIn(viewModelScope)
    }


}