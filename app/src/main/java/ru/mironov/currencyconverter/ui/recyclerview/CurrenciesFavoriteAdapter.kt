package ru.mironov.currencyconverter.ui.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import ru.mironov.currencyconverter.R
import ru.mironov.currencyconverter.model.CurrencyRate
import ru.mironov.currencyconverter.databinding.ItemCurrencyFavoriteBinding
import ru.mironov.currencyconverter.model.CurrencyFavorite
import ru.mironov.currencyconverter.util.FlagSetter.setFlag
import java.util.*
import kotlin.collections.ArrayList

class CurrenciesFavoriteAdapter(
    private val listener: ItemClickListener<CurrencyFavoriteViewHolder>,
    private val checkListener: CheckBoxChangeListener

) :

    RecyclerView.Adapter<CurrencyFavoriteViewHolder>(), View.OnClickListener {

    var favoriteCurrencies: ArrayList<CurrencyFavorite> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyFavoriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCurrencyFavoriteBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)

        return CurrencyFavoriteViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: CurrencyFavoriteViewHolder, position: Int) {

        val currency = favoriteCurrencies[position]
        with(holder.binding) {

            currencyName.text = currency.name
            currencyCheck.tag=position
            setFlag(currency.name.toString(), currencyIcon)

            holder.binding.currencyCheck.setOnCheckedChangeListener (null)

            if(currency.isFavorite){
                currencyName.setTextColor(R.color.black)
                currencyIcon.imageAlpha=255
                currencyCheck.isChecked=true
            }
            else{
                currencyName.setTextColor(R.color.gray)
                currencyIcon.imageAlpha=100
                currencyCheck.isChecked=false
            }
        }

        val itemBinding = holder.binding
        itemBinding.root.setOnClickListener { listener.onClickListener(holder) }
        itemBinding.currencyCheck.setOnCheckedChangeListener (checkListener)
    }

    override fun getItemCount(): Int = favoriteCurrencies.size

    interface ItemClickListener<I> {
        fun onClickListener(item: CurrencyFavoriteViewHolder) {
        }
    }

    interface ItemOnCheckedChangeListener {

    }

    override fun onClick(v: View?) {}

    fun notifyChanges(){
        this.notifyDataSetChanged()
    }
}
