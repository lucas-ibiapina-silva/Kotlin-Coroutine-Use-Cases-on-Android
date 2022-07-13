package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lukaslechner.coroutineusecasesonandroid.base.BaseActivity
import com.lukaslechner.coroutineusecasesonandroid.base.flowUseCase4Description
import com.lukaslechner.coroutineusecasesonandroid.databinding.ActivityFlowUsecase1Binding
import com.lukaslechner.coroutineusecasesonandroid.utils.setGone
import com.lukaslechner.coroutineusecasesonandroid.utils.setVisible
import kotlinx.coroutines.launch

class FlowUseCase4Activity : BaseActivity() {

    private val binding by lazy { ActivityFlowUsecase1Binding.inflate(layoutInflater) }

    private val viewModel: FlowUseCase4ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // binding.chart.initChart(this)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.combinedStateFlow.collect { uiState ->
                    render(uiState)
                }
            }
        }

        // OR
        /* lifecycleScope.launch {
            viewModel.currentStockPriceAsFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect{ uiState ->
                    render(uiState)
                }
        }*/
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.progressBar.setVisible()
                // binding.chart.setGone()
            }
            is UiState.Success -> {
                binding.progressBar.setGone()
                // binding.chart.setVisible()
                updateChart(uiState.stockPriceInEuro)
            }
        }
    }

    private fun updateChart(stockPriceInEuro: Float) {
        // val currentLineData = binding.chart.data
        /*currentLineData.addEntry(
            Entry(
                currentLineData.entryCount.toFloat(),
                stockPriceInEuro
            ), 0
        )
        binding.chart.data = currentLineData
        binding.chart.invalidate()*/
    }

    override fun getToolbarTitle() = flowUseCase4Description
}