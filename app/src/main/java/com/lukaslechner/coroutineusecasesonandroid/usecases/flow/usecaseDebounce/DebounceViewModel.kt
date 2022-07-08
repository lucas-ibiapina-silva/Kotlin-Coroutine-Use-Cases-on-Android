package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.*
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.CryptoCurrencyDao
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.mapToEntityList
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.mapToUiModelList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat
import timber.log.Timber

class DebounceViewModel(
    private val mockApi: FlowMockApi = mockApi(),
    private val cryptoCurrencyDatabase: CryptoCurrencyDao,
    private val networkStatusProvider: NetworkStatusProvider
) : ViewModel() {

    val selectedCurrency = MutableStateFlow(Currency.DOLLAR)

    private fun intervalFlow(interval: Long) = flow {
        while (currentCoroutineContext().isActive) {
            delay(interval)
            emit(Unit)
        }
    }

    init {
        intervalFlow(3000)
            .combine(networkStatusProvider.networkStatus) { _, networkStatus -> networkStatus }
            .filter { networkStatus -> networkStatus is NetworkStatusProvider.NetworkStatus.Available } // only on available internet connection
            .buffer()
            .onEach {
                Timber.d("fetching current crypto currency prices")
                val cryptoCurrencyPrices = mockApi.getCurrentCryptoCurrencyPrices()
                cryptoCurrencyDatabase.insert(cryptoCurrencyPrices.mapToEntityList())
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

    private val cryptoFlow =
        cryptoCurrencyDatabase.latestCryptoCurrencyPrices().map { it.mapToUiModelList() }

    private val cryptoFlowEuro: Flow<List<CryptoCurrency>> = combine(
        cryptoFlow,
        currencyRateFlow
    ) { latestCryptoPrice, latestCurrencyRate ->
        return@combine latestCryptoPrice.map {
            val euroPrice = it.currentPrice * latestCurrencyRate.usdInEuro
            it.copy(currency = Currency.EURO, currentPrice = euroPrice)
        }
    }

    // depending on the selected currency, either the items from the dollar price stream or the
    private val userDefinedCurrencyCryptoFlow: Flow<List<CryptoCurrency>> = selectedCurrency.flatMapLatest { selectedCurrency ->
        when (selectedCurrency) {
            Currency.DOLLAR -> cryptoFlow
            Currency.EURO -> cryptoFlowEuro
        }
    }

    val networkStatusChannel = networkStatusProvider.networkStatus

    val searchTermStateFlow = MutableStateFlow("")
    val searchTermFlow = searchTermStateFlow.debounce(1000)

    fun updateSearchTerm(searchTerm: String) {
        searchTermStateFlow.value = searchTerm
    }

    fun changeCurrency() {
        when (selectedCurrency.value) {
            Currency.DOLLAR -> selectedCurrency.value = Currency.EURO
            Currency.EURO -> selectedCurrency.value = Currency.DOLLAR
        }
    }

    val uiState = combine(
        userDefinedCurrencyCryptoFlow,
        searchTermFlow,
    ) { latestPrices, searchTerm ->
        return@combine if (searchTerm.length < 3) {
            latestPrices
        } else {
            latestPrices.filter { it.name.contains(searchTerm, ignoreCase = true) }
        }
    }
        .map {
            it.map { cryptoCurrency ->
                cryptoCurrency.copy(marketCap = cryptoCurrency.currentPrice * cryptoCurrency.totalSupply)
            }
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
        }.flowOn(Dispatchers.Default)
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
}