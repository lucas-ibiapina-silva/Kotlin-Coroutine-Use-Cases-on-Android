package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import com.lukaslechner.coroutineusecasesonandroid.R
import com.lukaslechner.coroutineusecasesonandroid.base.BaseActivity
import com.lukaslechner.coroutineusecasesonandroid.base.flowUseCase3Description
import com.lukaslechner.coroutineusecasesonandroid.databinding.ActivityFlowDebounceBinding
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce.database.CryptoCurrencyDatabase
import com.lukaslechner.coroutineusecasesonandroid.utils.setGone
import com.lukaslechner.coroutineusecasesonandroid.utils.setVisible
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DebounceActivity : BaseActivity() {

    private val binding by lazy { ActivityFlowDebounceBinding.inflate(layoutInflater) }

    private val viewModel: DebounceViewModel by viewModels {
        ViewModelFactory(
            api = mockApi(),
            database = CryptoCurrencyDatabase.getInstance(applicationContext).cryptoCurrencyDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.addItemDecoration(initItemDecoration())

        val searchInputFlow = callbackFlow {
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    s?.toString()?.let { trySend(it) }
                }
            }
            binding.searchFieldEditText.addTextChangedListener(textWatcher)
            awaitClose {
                binding.searchFieldEditText.removeTextChangedListener(textWatcher)
            }
        }

        // viewModel.setSearchInputFlow(searchInputFlow)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect{ uiState ->
                    render(uiState)
                }
            }
        }
    }

    private fun render(uiState: UiState) {
        when (uiState) {
            is UiState.Loading -> {
                binding.progressBar.setVisible()
                binding.recyclerView.setGone()
            }
            is UiState.Success -> {
                binding.recyclerView.setVisible()
                binding.recyclerView.adapter = CryptoCurrencyAdapter(uiState.stockList)
                binding.progressBar.setGone()
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
}