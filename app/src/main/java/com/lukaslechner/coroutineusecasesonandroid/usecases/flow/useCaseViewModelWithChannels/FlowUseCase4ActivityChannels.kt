package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.useCaseViewModelWithChannels

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.data.Entry
import com.lukaslechner.coroutineusecasesonandroid.base.BaseActivity
import com.lukaslechner.coroutineusecasesonandroid.base.flowUseCase4Description
import com.lukaslechner.coroutineusecasesonandroid.databinding.ActivityFlowUsecase1Binding
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.helper.initChart
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.useCaseViewModelWithChannels.ViewModelWithChannels.NetworkState
import com.lukaslechner.coroutineusecasesonandroid.utils.setGone
import com.lukaslechner.coroutineusecasesonandroid.utils.setVisible
import com.lukaslechner.coroutineusecasesonandroid.utils.toast
import kotlinx.coroutines.launch

class FlowUseCase4ActivityChannels : BaseActivity() {

    private val binding by lazy { ActivityFlowUsecase1Binding.inflate(layoutInflater) }

    private val viewModel: ViewModelWithChannels by viewModels {
        ViewModelFactory(
            this@FlowUseCase4ActivityChannels,
            NetworkStockPriceDataSource()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.chart.initChart(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.currentStockPriceAsFlow.collect { uiState ->
                        render(uiState)
                    }
                }
                launch {
                    viewModel.networkStateChannelFlow.collect { networkState ->
                        when (networkState) {
                            is NetworkState.Available -> {
                                // toast("Network connection available again")
                            }
                            is NetworkState.Unavailable -> {
                                toast("No network connection - Live tracking stopped!")
                            }
                        }
                    }
                }
            }
        }


    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.progressBar.setVisible()
                binding.chart.setGone()
            }
            is UiState.Success -> {
                binding.progressBar.setGone()
                binding.chart.setVisible()
                updateChart(uiState.stock)
            }
        }
    }

    private fun updateChart(stock: Stock) {
        val currentLineData = binding.chart.data
        currentLineData.addEntry(
            Entry(
                currentLineData.entryCount.toFloat(),
                stock.currentPriceUsd
            ), 0
        )
        binding.chart.data = currentLineData
        binding.chart.invalidate()
    }

    override fun getToolbarTitle() = flowUseCase4Description
}