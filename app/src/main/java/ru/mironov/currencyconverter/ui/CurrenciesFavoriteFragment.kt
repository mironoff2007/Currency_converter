package ru.mironov.currencyconverter.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mironov.currencyconverter.R
import ru.mironov.currencyconverter.appComponent
import ru.mironov.currencyconverter.databinding.FragmentCurrenciesFavoriteBinding
import ru.mironov.currencyconverter.model.CurrencyFavorite
import ru.mironov.currencyconverter.model.Status
import ru.mironov.currencyconverter.model.ViewModelCurrenciesFragment
import ru.mironov.currencyconverter.retrofit.ErrorUtil
import ru.mironov.currencyconverter.ui.recyclerview.CurrenciesFavoriteAdapter
import ru.mironov.currencyconverter.ui.recyclerview.CurrencyFavoriteViewHolder
import java.util.*
import kotlin.collections.ArrayList

class CurrenciesFavoriteFragment : Fragment() {

    private lateinit var viewModel: ViewModelCurrenciesFragment

    private var _binding: FragmentCurrenciesFavoriteBinding? = null

    private val binding get() = _binding!!

    private val progressBarDelay: Long = 500

    private var errorToast: Toast? = null

    private lateinit var adapter: CurrenciesFavoriteAdapter

    private var timerProgressBar: Job? = null

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
            requireContext().appComponent.factory.create(ViewModelCurrenciesFragment::class.java)

        val list=ArrayList<CurrencyFavorite>()
        viewModel.repository.getCurrenciesNames().forEach(){
            list.add(CurrencyFavorite(it,false))
        }
        populateRecycler(list)

        adapterSetup()
        setupObserver()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun adapterSetup() {
        adapter = CurrenciesFavoriteAdapter(object :
            CurrenciesFavoriteAdapter.ItemClickListener<CurrencyFavorite> {
            override fun onClickListener(clickedItem: CurrencyFavoriteViewHolder) {
                //On Recycler Item Clicked


            }
        })

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
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun populateRecycler(data: ArrayList<CurrencyFavorite>) {
        adapter.favoriteCurrencies = data
        adapter.notifyChanges()
    }

    @SuppressLint("ShowToast")
    private fun setupObserver() {
        viewModel.mutableStatus.observe(this.viewLifecycleOwner) { status ->
            when (status) {
                is Status.DATA -> {
                    timerProgressBar?.cancel()
                    binding.progressBar.visibility = View.GONE
                }
                is Status.LOADING -> {
                    //Show progress bar only for long response
                    timerProgressBar =
                        viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Default) {
                            delay(progressBarDelay)
                            viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Main) {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                        }
                }
                is Status.ERROR -> {
                    timerProgressBar?.cancel()
                    binding.progressBar.visibility = View.GONE

                    errorToast?.cancel()
                    errorToast = if (viewModel.isRatesEmpty()) {
                        Toast.makeText(
                            this.requireContext(),
                            getString(R.string.error) + " - " + ErrorUtil.getErrorMessage(
                                requireContext(),
                                status.code
                            ),
                            Toast.LENGTH_LONG
                        )
                    } else {
                        Toast.makeText(
                            this.requireContext(),
                            getString(R.string.from_cache),
                            Toast.LENGTH_LONG
                        )
                    }
                    errorToast?.show()
                }
            }
        }
    }
}