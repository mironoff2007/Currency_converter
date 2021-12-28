package ru.mironov.currencyconverter.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import ru.mironov.currencyconverter.appComponent
import ru.mironov.currencyconverter.databinding.FragmentGraphBinding
import ru.mironov.currencyconverter.model.ViewModelCurrenciesFragment
import ru.mironov.currencyconverter.model.ViewModelGraphFragment
import ru.mironov.currencyconverter.model.ViewModelSetKeyFragment
import ru.mironov.currencyconverter.repository.Repository
import ru.mironov.currencyconverter.ui.spinner.CustomAdapter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class GraphFragment : Fragment() {

    companion object {
        private const val CUSTOM_DATE_FORMAT = "dd-MM-yyyy"
        private const val PATTERN_DATE_FORMAT = "yyyy-MM-dd"
        private const val MIN_API_DATE = "1999-1-1"
    }


    private lateinit var viewModel: ViewModelGraphFragment

    private var _binding: FragmentGraphBinding? = null

    private val binding get() = _binding!!

    private var datePickerDialogTo: DatePickerDialog? = null
    private var datePickerDialogFrom: DatePickerDialog? = null

    private var dateSetListenerTo:OnDateSetListener?=null
    private var dateSetListenerFrom:OnDateSetListener?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGraphBinding.inflate(inflater, container, false)

        viewModel = requireContext().appComponent.factory.create(ViewModelGraphFragment::class.java)

        binding.dateToButton.setOnClickListener { datePickerDialogTo?.show() }
        binding.dateFromButton.setOnClickListener { datePickerDialogFrom?.show() }

        initDatePickers()
        initSpinner()

        return binding.root
    }

   fun initSpinner(){
        val mCustomAdapter =
            CustomAdapter(requireContext(),  viewModel.getCurrenciesNames())
        //Spinner From
        binding.spinner.adapter = mCustomAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }
    }

    private fun initDatePickers() {
        //Current date
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val style: Int = AlertDialog.THEME_HOLO_LIGHT

        //Set to date
        binding.dateToButton.text =  makeDateString(day,month+1,year)

       dateSetListenerTo =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month += 1
                val date: String = makeDateString(day, month, year)
                binding.dateToButton.text = date
                datePickerDialogFrom?.datePicker?.maxDate =getDate(day-1,month,year).time
            }
        datePickerDialogTo =
            DatePickerDialog(requireContext(), style, dateSetListenerTo, year, month, day)
        datePickerDialogTo?.datePicker?.maxDate = System.currentTimeMillis()


        //Set from date
        dateSetListenerFrom =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month += 1
                val date: String = makeDateString(day, month, year)
                binding.dateFromButton.text = date
                datePickerDialogTo?.datePicker?.minDate =getDate(day+1,month,year).time
            }

        binding.dateFromButton.text =  makeDateString(day,month,year)

        datePickerDialogFrom =
            DatePickerDialog(requireContext(), style, dateSetListenerFrom, year, month-1, day)
        datePickerDialogFrom?.datePicker?.minDate = SimpleDateFormat(PATTERN_DATE_FORMAT).parse(MIN_API_DATE).time
        datePickerDialogFrom?.datePicker?.maxDate = System.currentTimeMillis().minus(86400000)

    }

    @SuppressLint("SimpleDateFormat")
    private fun makeDateString(day: Int, month: Int, year: Int): String {
        val date=SimpleDateFormat(PATTERN_DATE_FORMAT).parse("$year-$month-$day")
        return SimpleDateFormat(CUSTOM_DATE_FORMAT).format(date)
    }

    private fun getDate(day: Int, month: Int, year: Int): Date {
        return SimpleDateFormat(PATTERN_DATE_FORMAT).parse("$year-$month-$day")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        datePickerDialogTo=null
        datePickerDialogFrom=null
        dateSetListenerTo=null
        dateSetListenerFrom=null
    }
}