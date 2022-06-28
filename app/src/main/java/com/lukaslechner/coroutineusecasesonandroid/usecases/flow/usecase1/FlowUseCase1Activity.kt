package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase1

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.lukaslechner.coroutineusecasesonandroid.R
import com.lukaslechner.coroutineusecasesonandroid.base.BaseActivity
import com.lukaslechner.coroutineusecasesonandroid.base.flowUseCase1Description
import com.lukaslechner.coroutineusecasesonandroid.databinding.ActivityFlowUsecase1Binding
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.GoogleStock

class FlowUseCase1Activity : BaseActivity() {

    private val binding by lazy { ActivityFlowUsecase1Binding.inflate(layoutInflater) }

    private val viewModel: FlowUseCase1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initChart()

        viewModel.currentGoogleStockPriceAsLiveData.observe(this) { uiState ->
            if (uiState != null) {
                render(uiState)
            }
        }
    }

    private fun initChart() {
        val entries: ArrayList<Entry> = ArrayList()
        entries.add(Entry(0f, 2000f))

        val data = LineDataSet(entries, "Google Stock Price")
        data.style()

        val lineData = LineData(data)

        with(binding.googleStockChart) {
            this.data = lineData
            setDrawGridBackground(false)
            axisRight.isEnabled = false
            xAxis.isEnabled = false
        }

        binding.googleStockChart.axisLeft.apply {
            axisMinimum = 1990f
            axisMaximum = 2100f
        }
    }

    private fun LineDataSet.style() {
        val colorPrimary = ContextCompat.getColor(this@FlowUseCase1Activity, R.color.colorPrimary)
        color = colorPrimary
        setCircleColor(colorPrimary)
        lineWidth = 2.5f
        circleRadius = 1f
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Success -> {
                updateChart(uiState.googleStock)
            }
        }
    }

    private fun updateChart(stock: GoogleStock) {
        val currentLineData = binding.googleStockChart.data
        currentLineData.addEntry(
            Entry(
                currentLineData.entryCount.toFloat(),
                stock.currentPriceUsd
            ), 0
        )
        binding.googleStockChart.data = currentLineData
        binding.googleStockChart.invalidate()
    }

    override fun getToolbarTitle() = flowUseCase1Description
}