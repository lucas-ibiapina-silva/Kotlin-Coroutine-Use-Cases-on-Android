package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.CryptoCurrencyDao
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.mapToEntityList
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.mapToUiModelList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

class DebounceViewModel(
    private val mockApi: FlowMockApi = mockApi(),
    private val cryptoCurrencyDatabase: CryptoCurrencyDao
) : ViewModel() {

    private var searchInputFlow: Flow<String>? = null

    init {
        viewModelScope.launch {
            while (true) {
                delay(3000)
                val cryptoCurrencyPrices = mockApi.getCurrentCryptoCurrencyPrices()
                cryptoCurrencyDatabase.insert(cryptoCurrencyPrices.mapToEntityList())
            }
        }
    }

    val uiState = cryptoCurrencyDatabase
        .latestCryptoCurrencyPrices()
        .map { entityList ->
            entityList.mapToUiModelList()
        }
        .map {
            it.map { cryptoCurrency ->
                cryptoCurrency.copy(marketCap = cryptoCurrency.currentPriceUsd * cryptoCurrency.totalSupply)
            }
        }
        .map { cryptoCurrencyList ->
            UiState.Success(cryptoCurrencyList)
        }.onEach {
            Timber.d("New UiState: ${it.javaClass}")
        }.stateIn(
            initialValue = UiState.Loading,
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    /*fun setSearchInputFlow(searchInputFlow: Flow<String>) {
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
                val cryptoCurrencyList = mockApi.getCurrentCryptoCurrencyPrices()
                uiState.value = UiState.Success(cryptoCurrencyList)
            }.launchIn(viewModelScope)
    }*/


}