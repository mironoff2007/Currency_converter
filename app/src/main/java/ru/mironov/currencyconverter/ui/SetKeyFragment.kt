package ru.mironov.currencyconverter.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import ru.mironov.currencyconverter.databinding.FragmentSetkeyBinding
import androidx.navigation.fragment.findNavController
import ru.mironov.currencyconverter.R
import ru.mironov.currencyconverter.ViewModelSetKeyFragment
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

        val direction = R.id.action_setKeyFragment_to_tabsFragment

        binding.button.setOnClickListener { findNavController().navigate(direction) }

        viewModel = requireContext().appComponent.factory.create(ViewModelSetKeyFragment::class.java)

        Log.d("My_tag","viewModel context -"+viewModel.context.toString())
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}