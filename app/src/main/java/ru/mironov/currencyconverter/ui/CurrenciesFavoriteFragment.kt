package ru.mironov.currencyconverter.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import kotlinx.android.synthetic.main.fragment_currencies.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mironov.currency_converter.util.FormatNumbers.formatDoubleToString
import ru.mironov.currency_converter.util.FormatNumbers.getDoubleFromText
import ru.mironov.currencyconverter.R
import ru.mironov.currencyconverter.appComponent
import ru.mironov.currencyconverter.databinding.FragmentCurrenciesBinding
import ru.mironov.currencyconverter.databinding.FragmentCurrenciesFavoriteBinding
import ru.mironov.currencyconverter.model.CurrencyRate
import ru.mironov.currencyconverter.model.Status
import ru.mironov.currencyconverter.model.ViewModelCurrenciesFragment
import ru.mironov.currencyconverter.retrofit.ErrorUtil
import ru.mironov.currencyconverter.ui.recyclerview.CurrenciesAdapter
import ru.mironov.currencyconverter.ui.recyclerview.CurrencyViewHolder
import ru.mironov.currencyconverter.util.CurrencyDiffUtilCallback
import ru.mironov.currencyconverter.util.FlagSetter.setFlag
import java.util.*

class CurrenciesFavoriteFragment : Fragment() {

    private lateinit var viewModel: ViewModelCurrenciesFragment

    private var _binding: FragmentCurrenciesFavoriteBinding? = null

    private val binding get() = _binding!!

    private val progressBarDelay: Long = 500

    private var errorToast: Toast? = null

    private lateinit var adapter: CurrenciesAdapter

    private var timerProgressBar: Job? = null

    private val locale = Locale.US

    private val textChangeListener: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            viewModel.calculateCurrencies(getDoubleFromText(s.toString()))
        }

        override fun afterTextChanged(editable: Editable) {}
    }


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

        adapterSetup()
        setupObserver()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun adapterSetup() {
        adapter = CurrenciesAdapter(object : CurrenciesAdapter.ItemClickListener<CurrencyRate> {
            override fun onClickListener(clickedItem: CurrencyViewHolder) {
                //On Recycler Item Clicked

                //Lock to edit
                if (recyclerView.findViewHolderForAdapterPosition(0) != null) {
                    val firstItem =
                        recyclerView.findViewHolderForAdapterPosition(0) as CurrencyViewHolder
                    firstItem.binding.currencyRate.inputType = InputType.TYPE_NULL
                    firstItem.binding.currencyRate.removeTextChangedListener(textChangeListener)
                }

                //Unlock to edit clicked
                //clickedItem.binding.currencyRate.addTextChangedListener(textChangeListener)
                clickedItem.binding.currencyRate.inputType = InputType.TYPE_CLASS_NUMBER
                clickedItem.binding.currencyRate.requestFocus()


            }
        }, locale)

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

    private fun populateRecycler( data: LinkedList<CurrencyRate>) {
        //Calculate all currencies using input
        if (binding.firstRow.currencyName.text == viewModel.responseCurrency) {
            viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Default) {

                val currencyDiffUtilCallback =
                    CurrencyDiffUtilCallback(adapter.rates, data)
                val currencyDiffResult: DiffUtil.DiffResult =
                    DiffUtil.calculateDiff(currencyDiffUtilCallback)

                viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Main) {
                    adapter.rates = data
                    currencyDiffResult.dispatchUpdatesTo(adapter)
                }
            }
        }
    }

    @SuppressLint("ShowToast")
    private fun setupObserver() {
        viewModel.mutableStatus.observe(this.viewLifecycleOwner) { status ->
            when (status) {
                is Status.DATA -> {
                    populateRecycler(status.someData as LinkedList<CurrencyRate>)
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