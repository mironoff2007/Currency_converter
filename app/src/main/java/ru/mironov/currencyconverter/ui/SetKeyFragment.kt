package ru.mironov.currencyconverter.ui

import android.os.Bundle
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
import ru.mironov.currencyconverter.model.Status
import ru.mironov.currencyconverter.retrofit.ErrorUtil

class SetKeyFragment : Fragment(R.layout.fragment_setkey) {

    private lateinit var viewModel: ViewModelSetKeyFragment

    private var _binding: FragmentSetkeyBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSetkeyBinding.inflate(inflater, container, false)

        binding.button.setOnClickListener { setApiKey() }

        viewModel =
            requireContext().appComponent.factory.create(ViewModelSetKeyFragment::class.java)

        setupObserver()

        return binding.root
    }

    private fun setApiKey() {
        val apiKey = binding.editText.text

        if (apiKey.isNullOrBlank()) {
            Toast.makeText(context, getString(R.string.api_key_is_empty), Toast.LENGTH_LONG).show()
        } else {
            viewModel.getCurrencyRate(apiKey.toString())
        }
    }

    private fun setupObserver() {
        viewModel.mutableStatus.observe(this.viewLifecycleOwner) { status ->
            when (status) {
                is Status.DATA -> {
                    binding.progressBar.visibility = View.GONE
                    val apiKey = binding.editText.text.toString()
                    viewModel.repository.setApiKey(apiKey)
                    navigateToTabs()
                }
                is Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        context,
                        getString(R.string.error) + " - " + ErrorUtil.getErrorMessage(
                            requireContext(),
                            status.code
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun navigateToTabs() {
        val direction = R.id.action_setKeyFragment_to_tabsFragment
        findNavController().navigate(direction)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}