package ru.mironov.currencyconverter.ui.recyclerview

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.mironov.currencyconverter.model.CurrencyRate
import ru.mironov.currency_converter.util.FormatNumbers.formatDoubleToString
import ru.mironov.currencyconverter.databinding.ItemCurrencyBinding
import ru.mironov.currencyconverter.util.FlagSetter.setFlag
import java.util.*

class CurrenciesAdapter(
    private val listener: ItemClickListener<CurrencyRate>,
    private val locale:Locale
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
            if (position == 0) {
                //Make first row editable with listener
                currencyRate.textLocale = locale
                currencyRate.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                //currencyRate.addTextChangedListener(textChangeListener)
            } else {
                currencyRate.inputType = InputType.TYPE_NULL
                //currencyRate.removeTextChangedListener(textChangeListener)
            }
            //Update currencies
            currencyName.text = currency.name
            currencyRate.setText(formatDoubleToString(currency.rate, locale))
            setFlag(currency.name,currencyIcon)
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
        val tempCur = rates[pos2]
        rates.removeAt(pos2)
        rates.add(pos1, tempCur)
        notifyItemMoved(pos2, pos1)
        notifyItemChanged(1)
    }
}