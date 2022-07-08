package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.lukaslechner.coroutineusecasesonandroid.CoroutineUsecasesOnAndroidApplication
import com.lukaslechner.coroutineusecasesonandroid.R
import com.lukaslechner.coroutineusecasesonandroid.base.BaseActivity
import com.lukaslechner.coroutineusecasesonandroid.base.flowUseCase3Description
import com.lukaslechner.coroutineusecasesonandroid.databinding.ActivityFlowDebounceBinding
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.CryptoCurrencyDatabase
import com.lukaslechner.coroutineusecasesonandroid.utils.setGone
import com.lukaslechner.coroutineusecasesonandroid.utils.setVisible
import com.lukaslechner.coroutineusecasesonandroid.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormat

class DebounceActivity : BaseActivity() {

    private val binding by lazy { ActivityFlowDebounceBinding.inflate(layoutInflater) }

    private val viewModel: DebounceViewModel by viewModels {
        ViewModelFactory(
            api = mockApi(),
            database = CryptoCurrencyDatabase.getInstance(applicationContext).cryptoCurrencyDao(),
            (application as CoroutineUsecasesOnAndroidApplication).networkStatusProvider,
            Dispatchers.Default
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.addItemDecoration(initItemDecoration())
        setSupportActionBar(binding.toolbarLayout.toolbar)

        binding.searchFieldEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.updateSearchTerm(s.toString())
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { uiState ->
                        render(uiState)
                    }
                }
                launch {
                    viewModel.currentTime.collect {
                        binding.currentTime.text = "currentTime: $it"
                    }
                }
                launch {
                    viewModel.networkStatusChannel.runningReduce { last, current ->
                        if (last is NetworkStatusProvider.NetworkStatus.Unavailable && current is NetworkStatusProvider.NetworkStatus.Available) {
                            toast("Network connection available again, continuing live tracking")
                        }
                        current
                    }
                        .collect {
                            if (it is NetworkStatusProvider.NetworkStatus.Unavailable) {
                                toast("No network connection, stopping live tracking")
                            }
                        }
                }
                launch {
                    viewModel.selectedCurrency.collect{

                    }
                }
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.progressBar.setVisible()
                binding.recyclerView.setGone()
                binding.noResultTextView.setGone()
            }
            is UiState.Success -> {
                if (uiState.cryptoCurrencyList.isEmpty()) {
                    binding.noResultTextView.setVisible()
                    binding.recyclerView.setGone()
                    binding.progressBar.setGone()
                } else {
                    binding.recyclerView.setVisible()
                    binding.lastUpdateTime.text =
                        "lastUpdateTime: ${LocalDateTime.now().toString(DateTimeFormat.fullTime())}"
                    binding.recyclerView.adapter = CryptoCurrencyAdapter(uiState.cryptoCurrencyList)
                    binding.progressBar.setGone()
                }
            }
        }
    }

    private fun initItemDecoration(): DividerItemDecoration {
        val itemDecorator =
            DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.recyclerview_divider
            )!!
        )
        return itemDecorator
    }

    override fun getToolbarTitle() = flowUseCase3Description

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.flow_usecase_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.show_prices_in_euro -> viewModel.changeCurrency()
            R.id.refresh_prices -> viewModel.refreshPrices()
        }

        return super.onOptionsItemSelected(item)
    }
}