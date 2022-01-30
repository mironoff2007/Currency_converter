package ru.mironov.currencyconverter.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.mironov.currencyconverter.R
import ru.mironov.currencyconverter.appComponent
import ru.mironov.currencyconverter.databinding.FragmentCurrenciesFavoriteBinding
import ru.mironov.currencyconverter.model.CurrencyFavorite
import ru.mironov.currencyconverter.model.ViewModelConverterFragment
import ru.mironov.currencyconverter.ui.recyclerview.CheckBoxChangeListener
import ru.mironov.currencyconverter.ui.recyclerview.CurrenciesFavoriteAdapter
import ru.mironov.currencyconverter.ui.recyclerview.CurrencyFavoriteViewHolder
import kotlin.collections.ArrayList

class CurrenciesFavoriteFragment : Fragment() {

    private lateinit var viewModel: ViewModelConverterFragment

    private var _binding: FragmentCurrenciesFavoriteBinding? = null

    private val binding get() = _binding!!

    private var errorToast: Toast? = null

    private lateinit var adapter: CurrenciesFavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCurrenciesFavoriteBinding.inflate(inflater, container, false)

        viewModel =
            requireContext().appComponent.factory.create(ViewModelConverterFragment::class.java)

        binding.checkAll.setOnCheckedChangeListener(object : CheckBoxChangeListener() {
            @SuppressLint("ResourceAsColor")
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Default) {

                    adapter.favoriteCurrencies.forEach() {
                        it.isFavorite = isChecked
                    }
                    viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Main) {
                        adapter.notifyChanges()
                    }
                }
            }
        })

        adapterSetup()

        val list = viewModel.getFavoriteCurrencies()

        if (list != null) {
            populateRecycler(list)
        }

        return binding.root
    }


    /**
     Set default currencies.
     Set EUR and USD if no is favorite
     Set USD if only one is favorite, but not USD
     Set EUR if only one is favorite, but USD is
     */
    fun addDefault(){
        //Two currencies are needed
        //Set two by default
        var noFavorite = true
        var count=0
        var isUsdFav=false
        var usdPos=0
        var eurPos=0
        var addedCurrencyName=""
        adapter.favoriteCurrencies.forEach() {
            if (it.isFavorite) {
                noFavorite = false
                count++
                if(it.name=="USD"){
                    isUsdFav=true
                }
            }
            if(it.name=="USD"){
                usdPos=adapter.favoriteCurrencies.indexOf(it)
            }
            if(it.name=="EUR"){
                eurPos=adapter.favoriteCurrencies.indexOf(it)
            }
        }
        if (noFavorite) {
            //No favorite currencies
            adapter.favoriteCurrencies.forEach(){
                if(it.name=="EUR"||it.name=="USD"){
                    it.isFavorite=true
                }
            }
            Toast.makeText(this.context, getString(R.string.favorite_is_empty), Toast.LENGTH_LONG).show()
        }
        //Only one currency is favorite
        //Add USD or EUR
        else if(count<2){
            if (isUsdFav){
                adapter.favoriteCurrencies[eurPos].isFavorite=true
                addedCurrencyName="EUR"
            }
            else{
                adapter.favoriteCurrencies[usdPos].isFavorite=true
                addedCurrencyName="USD"
            }
            Toast.makeText(this.context, getString(R.string.favorite_is_only_one)+" "+addedCurrencyName, Toast.LENGTH_LONG).show()

        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        addDefault()
        viewModel.saveFavoriteCurrencies(adapter.favoriteCurrencies)
    }

    private fun adapterSetup() {
        adapter = CurrenciesFavoriteAdapter(
            object :
                CurrenciesFavoriteAdapter.ItemClickListener<CurrencyFavoriteViewHolder> {
                override fun onClickListener(clickedItem: CurrencyFavoriteViewHolder) {
                    //On Recycler Item Clicked
                }
            },
            object : CheckBoxChangeListener() {
                @SuppressLint("ResourceAsColor")
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    adapter.favoriteCurrencies[buttonView?.tag.toString().toInt()].isFavorite =
                        isChecked
                    adapter.notifyItemChanged(buttonView?.tag.toString().toInt())
                }
            }
        )

        val layoutManager = LinearLayoutManager(this.requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                binding.recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )
        //Removes flickering on range update
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
            false
    }

    private fun populateRecycler(data: ArrayList<CurrencyFavorite>) {
        adapter.favoriteCurrencies = data
        adapter.notifyChanges()
    }
}