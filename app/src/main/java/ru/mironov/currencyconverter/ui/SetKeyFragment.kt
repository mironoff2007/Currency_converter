package ru.mironov.currencyconverter.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.mironov.currencyconverter.databinding.FragmentSetkeyBinding
import androidx.navigation.fragment.findNavController
import ru.mironov.currencyconverter.R
import ru.mironov.currencyconverter.ViewModelSetKeyFragment
import ru.mironov.currencyconverter.appComponent


class SetKeyFragment:Fragment(R.layout.fragment_setkey) {

    private lateinit var viewModel: ViewModelSetKeyFragment

    private lateinit var binding: FragmentSetkeyBinding

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

        binding = FragmentSetkeyBinding.inflate(inflater, container, false)

        val direction = R.id.action_setKeyFragment_to_tabsFragment

        binding.button.setOnClickListener { findNavController().navigate(direction) }

        viewModel = ViewModelProvider(this)[ViewModelSetKeyFragment::class.java]

        //Log.d("My_tag",viewModel.myClass.toString())

        return binding.root
    }
}