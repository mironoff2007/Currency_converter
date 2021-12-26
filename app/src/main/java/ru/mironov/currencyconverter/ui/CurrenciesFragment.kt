package ru.mironov.currencyconverter.ui


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.mironov.currencyconverter.appComponent
import ru.mironov.currencyconverter.databinding.FragmentCurrenciesBinding
import ru.mironov.currencyconverter.model.ViewModelCurrenciesFragment
import ru.mironov.currencyconverter.model.ViewModelMainActivity
import ru.mironov.currencyconverter.repository.Repository
import javax.inject.Inject

class CurrenciesFragment: Fragment() {

    @Inject
    lateinit var repository: Repository

    private lateinit var viewModel: ViewModelCurrenciesFragment

    private var _binding: FragmentCurrenciesBinding?=null

    private val binding get() = _binding!!

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

        return binding.root
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}