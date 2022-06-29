package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase4

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.lukaslechner.coroutineusecasesonandroid.R
import com.lukaslechner.coroutineusecasesonandroid.base.BaseActivity
import com.lukaslechner.coroutineusecasesonandroid.base.flowUseCase4Description
import com.lukaslechner.coroutineusecasesonandroid.databinding.ActivityFlowUsecase1Binding
import kotlinx.coroutines.launch

class FlowUseCase4Activity : BaseActivity() {

    private val binding by lazy { ActivityFlowUsecase1Binding.inflate(layoutInflater) }

    private val viewModel: FlowUseCase4ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initChart()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.combinedStateFlow.collect { uiState ->
                    render(uiState)
                }
            }
        }

        // OR
        /* lifecycleScope.launch {
            viewModel.currentGoogleStockPriceAsFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect{ uiState ->
                    render(uiState)
                }
        }*/
    }

    private fun initChart() {
        val entries: ArrayList<Entry> = ArrayList()
        entries.add(Entry(0f, 2200f))

        val data = LineDataSet(entries, "Google Stock Price in Euro")
        data.style()

        val lineData = LineData(data)

        with(binding.googleStockChart) {
            this.data = lineData
            setDrawGridBackground(false)
            axisRight.isEnabled = false
            xAxis.isEnabled = false
        }

        binding.googleStockChart.axisLeft.apply {
            axisMinimum = 2100f
            axisMaximum = 2400f
        }
    }

    private fun LineDataSet.style() {
        val colorPrimary = ContextCompat.getColor(this@FlowUseCase4Activity, R.color.colorPrimary)
        color = colorPrimary
        setCircleColor(colorPrimary)
        lineWidth = 2.5f
        circleRadius = 1f
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Success -> {
                updateChart(uiState.stockPriceInEuro)
            }
        }
    }

    private fun updateChart(stockPriceInEuro: Float) {
        val currentLineData = binding.googleStockChart.data
        currentLineData.addEntry(
            Entry(
                currentLineData.entryCount.toFloat(),
                stockPriceInEuro
            ), 0
        )
        binding.googleStockChart.data = currentLineData
        binding.googleStockChart.invalidate()
    }

    override fun getToolbarTitle() = flowUseCase4Description
}