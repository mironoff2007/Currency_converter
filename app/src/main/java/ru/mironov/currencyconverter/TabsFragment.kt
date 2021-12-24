package ru.mironov.currencyconverter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

class TabsFragment:Fragment(R.layout.tabs_fragment) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
        Log.d("My_tag",this.toString())
    }
}