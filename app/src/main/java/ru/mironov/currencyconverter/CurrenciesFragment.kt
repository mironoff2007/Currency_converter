package ru.mironov.currencyconverter


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.mironov.currencyconverter.databinding.FragmentCurrenciesBinding

class CurrenciesFragment: Fragment() {
    private lateinit var binding: FragmentCurrenciesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        Log.d("My_tag",this.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCurrenciesBinding.inflate(inflater, container, false)

        return binding.root
    }
}