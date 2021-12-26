package ru.mironov.currencyconverter.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

import ru.mironov.currencyconverter.databinding.FragmentSetkeyBinding
import androidx.navigation.fragment.findNavController
import ru.mironov.currencyconverter.R
import ru.mironov.currencyconverter.model.ViewModelSetKeyFragment
import ru.mironov.currencyconverter.appComponent


class SetKeyFragment:Fragment(R.layout.fragment_setkey) {

    private lateinit var viewModel: ViewModelSetKeyFragment

    private var _binding: FragmentSetkeyBinding?=null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        Log.d("My_tag",this.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSetkeyBinding.inflate(inflater, container, false)

        binding.button.setOnClickListener { setApiKey() }

        viewModel = requireContext().appComponent.factory.create(ViewModelSetKeyFragment::class.java)

        return binding.root
    }

    private fun setApiKey() {
        val apiKey=binding.editText.text

        if(!apiKey.isNullOrBlank()){
            viewModel.repository.setApiKey(apiKey.toString())
            val direction = R.id.action_setKeyFragment_to_tabsFragment
            findNavController().navigate(direction)
        }
        else{
            Toast.makeText(context,getString(R.string.api_key_is_empty),Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}