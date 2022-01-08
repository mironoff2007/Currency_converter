package ru.mironov.currencyconverter.ui.recyclerview

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mironov.currencyconverter.model.CurrencyRate
import ru.mironov.currencyconverter.util.FormatNumbers.formatDoubleToString
import ru.mironov.currencyconverter.databinding.ItemCurrencyBinding
import ru.mironov.currencyconverter.util.FlagSetter.setFlag
import java.util.*

class CurrenciesAdapter(
    private val listener: ItemClickListener<CurrencyRate>,
    private val locale: Locale
) :
    RecyclerView.Adapter<CurrencyViewHolder>(), View.OnClickListener {

    var rates: LinkedList<CurrencyRate> = LinkedList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCurrencyBinding.inflate(inflater, parent, false)
        binding.root.setOnClickListener(this)

        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {

        val currency = rates[position]
        with(holder.binding) {

            if (position === 0) {
                currencyRate.visibility = View.GONE
                currencyName.visibility = View.GONE
                currencyIcon.visibility = View.INVISIBLE
            } else {
                //Update currencies
                currencyIcon.visibility = View.VISIBLE
                currencyRate.visibility = View.VISIBLE
                currencyName.visibility = View.VISIBLE
                currencyRate.inputType = InputType.TYPE_NULL
                currencyName.text = currency.name
                currencyRate.setText(formatDoubleToString(currency.rate, locale))
                setFlag(currency.name, currencyIcon)
            }

        }

        val itemBinding = holder.binding
        itemBinding.root.setOnClickListener { listener.onClickListener(holder) }
    }

    override fun getItemCount(): Int = rates.size

    interface ItemClickListener<I> {
        fun onClickListener(item: CurrencyViewHolder) {
        }
    }

    override fun onClick(v: View?) {}

    fun swap(pos1: Int, pos2: Int) {
        Collections.swap(rates,pos1,pos2)
        notifyItemRangeChanged(pos1,pos2-pos1)
        notifyItemMoved(pos2, pos1)
    }
}