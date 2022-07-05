package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecaseDebounce

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lukaslechner.coroutineusecasesonandroid.R
import com.lukaslechner.coroutineusecasesonandroid.databinding.RecyclerviewItemCryptoCurrencyBinding
import com.lukaslechner.coroutineusecasesonandroid.usecases.flow.mock.CryptoCurrency

class CryptoCurrencyAdapter(private val cryptoCurrencyList: List<CryptoCurrency>): RecyclerView.Adapter<CryptoCurrencyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = RecyclerviewItemCryptoCurrencyBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_crypto_currency, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.name.text = cryptoCurrencyList[position].name
        holder.binding.currentPrice.text = "$${cryptoCurrencyList[position].currentPriceUsd}"
    }

    override fun getItemCount(): Int {
        return cryptoCurrencyList.size
    }

}