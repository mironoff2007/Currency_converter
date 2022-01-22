package ru.mironov.currencyconverter.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
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
import ru.mironov.currencyconverter.util.FormatNumbers.formatDoubleToString
import ru.mironov.currencyconverter.util.FormatNumbers.getDoubleFromText
import ru.mironov.currencyconverter.R
import ru.mironov.currencyconverter.appComponent
import ru.mironov.currencyconverter.databinding.FragmentCurrenciesBinding
import ru.mironov.currencyconverter.model.CurrencyRate
import ru.mironov.currencyconverter.model.Status
import ru.mironov.currencyconverter.model.ViewModelCurrenciesFragment
import ru.mironov.currencyconverter.retrofit.ErrorUtil
import ru.mironov.currencyconverter.ui.recyclerview.CurrenciesAdapter
import ru.mironov.currencyconverter.ui.recyclerview.CurrencyViewHolder
import ru.mironov.currencyconverter.util.CurrencyDiffUtilCallback
import ru.mironov.currencyconverter.util.FlagSetter.setFlag
import java.util.*

class CurrenciesFragment : Fragment() {

    private lateinit var viewModel: ViewModelCurrenciesFragment

    private var _binding: FragmentCurrenciesBinding? = null

    private val binding get() = _binding!!

    private val progressBarDelay: Long = 500

    private var errorToast: Toast? = null

    private lateinit var adapter: CurrenciesAdapter

    private lateinit var timerJob: Job

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
    ): View {

        _binding = FragmentCurrenciesBinding.inflate(inflater, container, false)

        viewModel =
            requireContext().appComponent.factory.create(ViewModelCurrenciesFragment::class.java)

        adapterSetup()
        setupObserver()
        setupFirstRow(CurrencyRate(getString(R.string.eur), 1.0))

        viewModel.getCurrencyRate(
            binding.firstRow.currencyName.text.toString()
        )

        //Start timer
        /*
        timerJob = viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Default) {
            fixedRateTimer("timer", false, 0L, 1000)
            {
                viewModel.getCurrencyRate(
                    binding.firstRow.currencyName.text.toString()
                )
            }
        }
        */
        return binding.root
    }

    private fun setupFirstRow(currencyRate: CurrencyRate) {
        binding.firstRow.currencyRate.textLocale = locale
        binding.firstRow.currencyRate.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.firstRow.currencyRate.addTextChangedListener(textChangeListener)

        binding.firstRow.currencyName.text = currencyRate.name
        binding.firstRow.currencyRate.setText(formatDoubleToString(currencyRate.rate, locale))
        setFlag(currencyRate.name, binding.firstRow.currencyIcon)
    }

    private fun adapterSetup() {
        adapter = CurrenciesAdapter(object : CurrenciesAdapter.ItemClickListener<CurrencyRate> {
            override fun onClickListener(item: CurrencyViewHolder) {
                //On Recycler Item Clicked

                binding.firstRow.currencyRate.removeTextChangedListener(textChangeListener)

                //Lock to edit
                if (recyclerView.findViewHolderForAdapterPosition(0) != null) {
                    val firstItem =
                        recyclerView.findViewHolderForAdapterPosition(0) as CurrencyViewHolder
                    firstItem.binding.currencyRate.inputType = InputType.TYPE_NULL
                }

                //Unlock to edit clicked
                item.binding.currencyRate.inputType = InputType.TYPE_CLASS_NUMBER
                item.binding.currencyRate.requestFocus()

                //Swap
                binding.firstRow.currencyName.text = item.binding.currencyName.text
                binding.firstRow.currencyRate.text = item.binding.currencyRate.text
                setFlag(
                    binding.firstRow.currencyName.text.toString(),
                    binding.firstRow.currencyIcon
                )

                viewModel.responseCurrency = binding.firstRow.currencyName.text.toString()
                viewModel.swap(item.bindingAdapterPosition)
                adapter.swap(0, item.bindingAdapterPosition)
                binding.firstRow.currencyRate.addTextChangedListener(textChangeListener)
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

    private fun populateRecycler(data: ArrayList<CurrencyRate>) {
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
                is Status.RESPONSE -> {
                    Log.d("My_tag","response")
                    viewModel.calculateCurrencies(getDoubleFromText(binding.firstRow.currencyRate.text.toString()))
                }
                is Status.DATA -> {
                    populateRecycler(status.someData as ArrayList<CurrencyRate>)
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

    override fun onPause() {
        super.onPause()
        val navHostFragment=requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if(navHostFragment?.arguments==null) {
            val args = Bundle()
            args.putString(CURRENCY_FROM_NAME, binding.firstRow.currencyName.text.toString())
            if( adapter.rates.size>1){
            args.putString(CURRENCY_TO_NAME, adapter.rates[1].name)}
            navHostFragment?.arguments = args
        }
        else{
            navHostFragment!!.requireArguments().putString(CURRENCY_FROM_NAME, binding.firstRow.currencyName.text.toString())
            if( adapter.rates.size>1){
            navHostFragment!!.requireArguments().putString(CURRENCY_TO_NAME, adapter.rates[1].name)}
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.firstRow.currencyRate.removeTextChangedListener(textChangeListener)
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    companion object{
        const val CURRENCY_FROM_NAME="CURRENCY_FROM_NAME"
        const val CURRENCY_TO_NAME="CURRENCY_TO_NAME"
    }
}