package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.*
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.CryptoCurrencyDao
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.mapToEntityList
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.mapToUiModelList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import timber.log.Timber
import kotlin.system.measureTimeMillis

class DebounceViewModel(
    private val mockApi: FlowMockApi = mockApi(),
    private val cryptoCurrencyDatabase: CryptoCurrencyDao,
    private val networkStatusProvider: NetworkStatusProvider,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModel() {

    val selectedCurrency = MutableStateFlow(Currency.DOLLAR)

    private val userTriggeredRefreshFlow = MutableStateFlow(Unit)

    private fun intervalFlow(interval: Long) = flow {
        while (currentCoroutineContext().isActive) {
            delay(interval)
            emit(Unit)
        }
    }

    init {
        intervalFlow(3000)
            .combine(userTriggeredRefreshFlow) { _,_ -> Unit}
            .combine(networkStatusProvider.networkStatus) { _, networkStatus -> networkStatus }
            .filter { networkStatus -> networkStatus is NetworkStatusProvider.NetworkStatus.Available } // only on available internet connection
            .buffer(0)
            .onEach {
                Timber.d("fetching current crypto currency prices")
                val cryptoCurrencyPrices = mockApi.getCurrentCryptoCurrencyPrices()
                cryptoCurrencyDatabase.insert(cryptoCurrencyPrices.mapToEntityList())
                Timber.d("Fetching crypto prices finished")
            }
            .launchIn(viewModelScope)
    }

    // emits currency rate items every 5 seconds in case user wants to see prices in euro and has internet connection
    private val currencyRateFlow: Flow<CurrencyRate> = intervalFlow(5000)
        .combine(selectedCurrency) { _, currentCurrency -> currentCurrency }
        .filter { currency -> currency == Currency.EURO } // only if user wants to see euro prices
        .combine(networkStatusProvider.networkStatus) { _, networkStatus -> networkStatus }
        .filter { networkStatus -> networkStatus is NetworkStatusProvider.NetworkStatus.Available } // only on available internet connection
        .buffer()
        .transform {
            emit(mockApi.getCurrentCurrencyRate())
        }
        .distinctUntilChanged()

    private val cryptoFlow =
        cryptoCurrencyDatabase.latestCryptoCurrencyPrices().map { it.mapToUiModelList() }
            .flowOn(defaultDispatcher)

    private val cryptoFlowEuro: Flow<List<CryptoCurrency>> = combine(
        cryptoFlow,
        currencyRateFlow
    ) { latestCryptoPrice, latestCurrencyRate ->
        var euroPrices: List<CryptoCurrency>? = null
        val calculationTime = measureTimeMillis {
            euroPrices = latestCryptoPrice.map {
                val euroPrice = it.currentPrice * latestCurrencyRate.usdInEuro
                it.copy(currency = Currency.EURO, currentPrice = euroPrice)
            }
        }

        Timber.d("Calculating euro prices took: $calculationTime ms")

        euroPrices!!
    }.flowOn(defaultDispatcher)

    // depending on the selected currency, either the items from the dollar price stream or the
    private val userDefinedCurrencyCryptoFlow: Flow<List<CryptoCurrency>> =
        selectedCurrency.flatMapLatest { selectedCurrency ->
            when (selectedCurrency) {
                Currency.DOLLAR -> cryptoFlow
                Currency.EURO -> cryptoFlowEuro
            }
        }

    val networkStatusChannel = networkStatusProvider.networkStatus

    val rawSearchTermStateFlow = MutableStateFlow("")
    val searchTermFlow = rawSearchTermStateFlow
        .debounce(1000)
        .map { searchTerm ->
            when (searchTerm.length) {
                0 -> SearchTerm.Empty
                in 1..2 -> SearchTerm.TooShort
                else -> SearchTerm.Valid(searchTerm)
            }
        }.distinctUntilChanged()

    fun updateSearchTerm(searchTerm: String) {
        rawSearchTermStateFlow.value = searchTerm
    }

    fun changeCurrency() {
        when (selectedCurrency.value) {
            Currency.DOLLAR -> selectedCurrency.value = Currency.EURO
            Currency.EURO -> selectedCurrency.value = Currency.DOLLAR
        }
    }

    private val filteredResult: Flow<Pair<List<CryptoCurrency>, Float?>> = combine(
        userDefinedCurrencyCryptoFlow,
        searchTermFlow.filterNot { it is SearchTerm.TooShort },
    ) { latestPrices, searchTerm ->
        return@combine when (searchTerm) {
            is SearchTerm.Valid -> latestPrices.filter {
                it.name.contains(
                    searchTerm.searchTerm,
                    ignoreCase = true
                )
            }
            else -> latestPrices
        }
    }.map {
        val startCalculation = System.currentTimeMillis()
        val result = it.map { cryptoCurrency ->
            cryptoCurrency.copy(marketCap = cryptoCurrency.currentPrice * cryptoCurrency.totalSupply)
        }
        val endCalculation = System.currentTimeMillis()
        Timber.d("Market Cap calculation took ${endCalculation - startCalculation}ms")
        result
    }.runningReduce { lastCryptoCurrencyList, currentCryptoCurrencyList ->
        currentCryptoCurrencyList.map { currentCrypto ->
            val lastPrice =
                lastCryptoCurrencyList.find { it.name == currentCrypto.name }?.currentPrice
                    ?: return@map currentCrypto
            return@map when {
                currentCrypto.currentPrice < lastPrice -> currentCrypto.copy(priceTrend = PriceTrend.DOWN)
                currentCrypto.currentPrice == lastPrice -> currentCrypto.copy(priceTrend = PriceTrend.NEUTRAL)
                else -> currentCrypto.copy(priceTrend = PriceTrend.UP)
            }
        }
    }.map { cryptoCurrencyList ->
        val startCalculation = System.currentTimeMillis()
        val totalMarketCap = cryptoCurrencyList.map { it.marketCap }
            .reduceOrNull { acc, cryptoCurrency -> acc + cryptoCurrency }
        val endCalculation = System.currentTimeMillis()
        cryptoCurrencyList.sortedByDescending { it.marketCap }
        Timber.d("Total Market Cap calculation ($totalMarketCap) and sorting took ${endCalculation - startCalculation}ms")
        Pair(cryptoCurrencyList, totalMarketCap)
    }.flowOn(defaultDispatcher)

    val uiState = MutableStateFlow<UiState>(UiState.Initial)

    init {
        searchTermFlow
            .filterNot { it is SearchTerm.TooShort }
            .combine(selectedCurrency) { searchTerm, selectedCurrency ->
                uiState.value = UiState.Loading
            }.flatMapLatest {
                filteredResult
            }.onEach {
                uiState.value = UiState.Success(it.first, it.second)
            }.launchIn(viewModelScope)

    }

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

    fun refreshPrices() {
        Timber.d("User triggers price refresh")
        userTriggeredRefreshFlow.value = Unit
    }

    sealed class SearchTerm {
        object Empty : SearchTerm()
        object TooShort : SearchTerm()
        class Valid(val searchTerm: String) : SearchTerm()
    }
}