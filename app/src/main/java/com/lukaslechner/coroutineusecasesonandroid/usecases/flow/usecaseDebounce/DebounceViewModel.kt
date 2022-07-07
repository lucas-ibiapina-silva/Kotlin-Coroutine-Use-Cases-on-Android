package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.FlowMockApi
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.PriceTrend
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.CryptoCurrencyDao
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.mapToEntityList
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.mapToUiModelList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
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

    val searchTermStateFlow = MutableStateFlow("")
    val searchTermFlow = searchTermStateFlow.debounce(1000)

    fun updateSearchTerm(searchTerm: String) {
        searchTermStateFlow.value = searchTerm
    }

    val uiState = combine(cryptoCurrencyDatabase.latestCryptoCurrencyPrices(), searchTermFlow) { latestPrices, searchTerm ->
        return@combine if (searchTerm.length < 3) {
            latestPrices
        } else {
            latestPrices.filter { it.name.contains(searchTerm, ignoreCase = true) }
        }
    }.map { entityList ->
            entityList.mapToUiModelList()
        }
        .map {
            it.map { cryptoCurrency ->
                cryptoCurrency.copy(marketCap = cryptoCurrency.currentPriceUsd * cryptoCurrency.totalSupply)
            }
        }.runningReduce { lastCryptoCurrencyList, currentCryptoCurrencyList ->
            currentCryptoCurrencyList.map { currentCrypto ->
                val lastPrice =
                    lastCryptoCurrencyList.find { it.name == currentCrypto.name }?.currentPriceUsd ?: return@map currentCrypto
                return@map when {
                    currentCrypto.currentPriceUsd < lastPrice -> currentCrypto.copy(priceTrend = PriceTrend.DOWN)
                    currentCrypto.currentPriceUsd == lastPrice -> currentCrypto.copy(priceTrend = PriceTrend.NEUTRAL)
                    else -> currentCrypto.copy(priceTrend = PriceTrend.UP)
                }
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

    val currentTime: StateFlow<String> = ticker()
        .conflate()
        .map { LocalDateTime.now() }
        .map { it.toString(DateTimeFormat.fullTime()) }
        .distinctUntilChanged()
        .stateIn(
            initialValue = LocalDateTime.now().toString(DateTimeFormat.fullTime()),
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    fun ticker(interval: Long = 500) = flow {
        while (true) {
            emit(Unit)
            delay(interval)
        }
    }

/*    fun setSearchInputFlow(searchInputFlow: Flow<String>) {
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