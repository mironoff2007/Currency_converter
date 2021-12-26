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
import ru.mironov.currencyconverter.model.CurrencyRate
import ru.mironov.currencyconverter.model.Status
import ru.mironov.currencyconverter.util.CurrencyDiffUtilCallback
import ru.mironov.currency_converter.util.FormatNumbers.formatDoubleToString
import ru.mironov.currency_converter.util.FormatNumbers.getDoubleFromText
import ru.mironov.currencyconverter.R
import ru.mironov.currencyconverter.appComponent
import ru.mironov.currencyconverter.databinding.FragmentCurrenciesBinding
import ru.mironov.currencyconverter.model.ViewModelCurrenciesFragment
import ru.mironov.currencyconverter.repository.Repository
import ru.mironov.currencyconverter.util.FlagSetter.setFlag
import java.util.*
import javax.inject.Inject

class CurrenciesFragment: Fragment() {

    @Inject
    lateinit var repository: Repository

    private lateinit var viewModel: ViewModelCurrenciesFragment

    private var _binding: FragmentCurrenciesBinding?=null

    private val binding get() = _binding!!

    val progressBarDelay: Long = 2000

    var errorToast: Toast? = null

    private lateinit var adapter: CurrenciesAdapter

    private lateinit var timerJob: Job

    private var timerProgressBar: Job? = null

    private val locale = Locale.US

    private val textChangeListener: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            populateRecycler(getDoubleFromText(s.toString()))
        }

        override fun afterTextChanged(editable: Editable) {

        }
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

        _binding = FragmentCurrenciesBinding.inflate(inflater, container, false)

        viewModel = requireContext().appComponent.factory.create(ViewModelCurrenciesFragment::class.java)

        adapterSetup()
        setupObserver()
        setupFirstRaw(CurrencyRate(getString(R.string.eur), 1.0))


        viewModel.getCurrencyRate(
            binding.firstRow.currencyName.text.toString())
                    /*
        //Start timer
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

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
    private fun setupFirstRaw(currencyRate: CurrencyRate) {
        binding.firstRow.currencyRate.textLocale = locale
        binding.firstRow.currencyRate.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.firstRow.currencyRate.addTextChangedListener(textChangeListener)

        binding.firstRow.currencyName.text = currencyRate.name
        binding.firstRow.currencyRate.setText(formatDoubleToString(currencyRate.rate, locale))
        setFlag(currencyRate.name, binding.firstRow.currencyIcon)
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

                //Swap
                binding.firstRow.currencyName.text = clickedItem.binding.currencyName.text
                binding.firstRow.currencyRate.text = clickedItem.binding.currencyRate.text
                setFlag(
                    binding.firstRow.currencyName.text.toString(),
                    binding.firstRow.currencyIcon
                )

                viewModel.swap(clickedItem.bindingAdapterPosition)
                adapter.swap(0, clickedItem.bindingAdapterPosition)

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

    private fun populateRecycler(value: Double) {
        //Calculate all currencies using input
        if (binding.firstRow.currencyName.text == viewModel.responseCurrency) {
            viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Default) {
                synchronized(viewModel.arrayRates) {

                    val changedRates = LinkedList<CurrencyRate>()

                    viewModel.arrayRates.forEach { it ->
                        changedRates.add(CurrencyRate(it.name, it.rate * value))
                    }
                    changedRates[0].rate = value

                    val currencyDiffUtilCallback =
                        CurrencyDiffUtilCallback(adapter.rates, changedRates)
                    val currencyDiffResult: DiffUtil.DiffResult =
                        DiffUtil.calculateDiff(currencyDiffUtilCallback)

                    viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Main) {

                        adapter.rates = changedRates
                        currencyDiffResult.dispatchUpdatesTo(adapter)
                        //adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    @SuppressLint("ShowToast")
    private fun setupObserver() {
        viewModel.mutableStatus.observe(this.viewLifecycleOwner) { status ->
            when (status) {
                Status.DATA -> {
                    populateRecycler(getDoubleFromText(binding.firstRow.currencyRate.text.toString()))
                    timerProgressBar?.cancel()
                    binding.progressBar.visibility = View.GONE
                }
                Status.LOADING -> {
                    //Show progress bar only for long response
                    timerProgressBar =
                        viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Default) {
                            delay(progressBarDelay)
                            viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Main) {
                                binding.progressBar.visibility = View.VISIBLE
                            }
                        }
                }
                Status.ERROR -> {
                    timerProgressBar?.cancel()
                    binding.progressBar.visibility = View.GONE

                    errorToast?.cancel()
                    errorToast = if (viewModel.arrayRates.isEmpty()) {
                        Toast.makeText(
                            this.requireContext(),
                            getString(R.string.error),
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