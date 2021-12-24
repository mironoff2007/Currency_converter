package ru.mironov.currencyconverter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.mironov.currencyconverter.databinding.SetkeyFragmentBinding
import androidx.navigation.fragment.findNavController

class SetKeyFragment:Fragment(R.layout.setkey_fragment) {

    private lateinit var binding: SetkeyFragmentBinding

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

        binding = SetkeyFragmentBinding.inflate(inflater, container, false)

        val direction = R.id.action_setKeyFragment_to_tabsFragment

        binding.button.setOnClickListener { findNavController().navigate(direction) }

        return binding.root
    }
}