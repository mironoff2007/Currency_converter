package ru.mironov.currencyconverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import ru.mironov.currencyconverter.contract.Navigator
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity(),Navigator {

    private lateinit var navController: NavController

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appComponent.inject(this)

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainer)as NavHostFragment
        navController = navHost.navController

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)

    }



    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is NavHostFragment) return
            currentFragment = f
            updateUi()
        }
    }

    private fun updateUi() {
        val fragment = currentFragment

        if (navController.currentDestination?.id == navController.graph.startDestination) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

    }

    override fun showOptionsScreen() {
        //
    }
}
