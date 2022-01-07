package ru.mironov.currencyconverter.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mironov.currencyconverter.model.CurrencyRate
import ru.mironov.currencyconverter.databinding.ItemCurrencyFavoriteBinding
import ru.mironov.currencyconverter.model.CurrencyFavorite
import ru.mironov.currencyconverter.util.FlagSetter.setFlag
import java.util.*
import kotlin.collections.ArrayList

class CurrenciesFavoriteAdapter(
    private val listener: ItemClickListener<CurrencyFavorite>,

    ) :

    RecyclerView.Adapter<CurrencyFavoriteViewHolder>(), View.OnClickListener {

    var favoriteCurrencies: ArrayList<CurrencyFavorite> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyFavoriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCurrencyFavoriteBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)

        return CurrencyFavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyFavoriteViewHolder, position: Int) {

        val currency = favoriteCurrencies[position]
        with(holder.binding) {

            currencyName.text = currency.name
            setFlag(currency.name, currencyIcon)
        }

        val itemBinding = holder.binding
        itemBinding.root.setOnClickListener { listener.onClickListener(holder) }
    }

    override fun getItemCount(): Int = favoriteCurrencies.size

    interface ItemClickListener<I> {
        fun onClickListener(item: CurrencyFavoriteViewHolder) {
        }
    }

    override fun onClick(v: View?) {}

    fun notifyChanges(){
        this.notifyDataSetChanged()
    }
}