package ru.mironov.currencyconverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.mironov.currencyconverter.model.viewmodels.ViewModelMainActivity

class MainActivity : AppCompatActivity() {


    private lateinit var viewModel: ViewModelMainActivity

    private lateinit var navController: NavController

    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        //Debug.waitForDebugger()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appComponent.inject(this)

        viewModel = appComponent.factory.create(ViewModelMainActivity::class.java)
        val navController = getRootNavController()
        prepareRootNavController(viewModel.isApiKeySaved(), navController)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)
    }

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is NavHostFragment) return
            currentFragment = f
        }
    }

    private fun getRootNavController(): NavController {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainer)as NavHostFragment
        return navHost.navController
    }


    private fun updateUi() {
        val fragment = currentFragment

        if (navController.currentDestination?.id == navController.graph.startDestination) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun prepareRootNavController(isSignedIn: Boolean, navController: NavController) {
        val graph = navController.navInflater.inflate(getMainNavigationGraphId())
        graph.startDestination = if (isSignedIn) {
            getTabsDestination()
        } else {
            getSetKeyDestination()
        }
        navController.graph = graph
    }

    private fun getSetKeyDestination(): Int = R.id.setKeyFragment

    private fun getMainNavigationGraphId(): Int = R.navigation.main_graph

    private fun getTabsDestination(): Int = R.id.tabsFragment



}
