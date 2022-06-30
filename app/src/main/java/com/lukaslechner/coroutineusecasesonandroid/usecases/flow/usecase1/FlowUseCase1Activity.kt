package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase1

import android.os.Bundle
import androidx.activity.viewModels
import com.github.mikephil.charting.data.Entry
import com.lukaslechner.coroutineusecasesonandroid.base.BaseActivity
import com.lukaslechner.coroutineusecasesonandroid.base.flowUseCase1Description
import com.lukaslechner.coroutineusecasesonandroid.databinding.ActivityFlowUsecase1Binding
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.helper.initChart
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.Stock
import com.lukaslechner.coroutineusecasesonandroid.utils.setGone
import com.lukaslechner.coroutineusecasesonandroid.utils.setVisible

class FlowUseCase1Activity : BaseActivity() {

    private val binding by lazy { ActivityFlowUsecase1Binding.inflate(layoutInflater) }

    private val viewModel: FlowUseCase1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.chart.initChart(this)

        viewModel.currentStockPriceAsLiveData.observe(this) { uiState ->
            if (uiState != null) {
                render(uiState)
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

    override fun getToolbarTitle() = flowUseCase1Description
}