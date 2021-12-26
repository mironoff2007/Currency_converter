package ru.mironov.currencyconverter.util

import androidx.recyclerview.widget.DiffUtil
import ru.mironov.currencyconverter.model.CurrencyRate

class CurrencyDiffUtilCallback(oldList: List<CurrencyRate>, newList: List<CurrencyRate>) :
    DiffUtil.Callback() {
    private val oldList: List<CurrencyRate> = oldList
    private val newList: List<CurrencyRate> = newList

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldCurrency: CurrencyRate = oldList[oldItemPosition]
        val newCurrency: CurrencyRate = newList[newItemPosition]
        return oldCurrency.name == newCurrency.name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldCurrency: CurrencyRate = oldList[oldItemPosition]
        val newCurrency: CurrencyRate = newList[newItemPosition]
        return (oldCurrency.name == newCurrency.name
                && oldCurrency.rate == newCurrency.rate)
    }

}