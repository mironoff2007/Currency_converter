package ru.mironov.currencyconverter.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import ru.mironov.currencyconverter.R
import ru.mironov.currencyconverter.appComponent
import ru.mironov.currencyconverter.databinding.FragmentTabsBinding
import ru.mironov.currencyconverter.model.ViewModelMainActivity


class TabsFragment:Fragment(R.layout.fragment_tabs) {

    private var _binding: FragmentTabsBinding?=null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTabsBinding.bind(view)

        val navHost = childFragmentManager.findFragmentById(R.id.tabsContainer) as NavHostFragment
        val navController = navHost.navController
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}