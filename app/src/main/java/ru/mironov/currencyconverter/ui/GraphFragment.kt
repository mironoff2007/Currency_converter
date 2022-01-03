package ru.mironov.currencyconverter.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.mironov.currencyconverter.R
import ru.mironov.currencyconverter.appComponent
import ru.mironov.currencyconverter.databinding.FragmentGraphBinding
import ru.mironov.currencyconverter.model.Status
import ru.mironov.currencyconverter.model.ViewModelGraphFragment
import ru.mironov.currencyconverter.ui.mpchart.DateXAxisValueFormatter
import ru.mironov.currencyconverter.ui.mpchart.MyMarkerView
import ru.mironov.currencyconverter.ui.spinner.CustomAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GraphFragment : Fragment() {

    companion object {
        private const val UI_DATE_FORMAT = "dd-MM-yyyy"
        private const val PATTERN_DATE_FORMAT = "yyyy-MM-dd"
        private const val MIN_API_DATE = "1999-1-1"
        private const val DAY_MILLIS = 86400000
        private const val scaleAxisLimits=1.1f
    }


    private lateinit var viewModel: ViewModelGraphFragment

    private var _binding: FragmentGraphBinding? = null

    private val binding get() = _binding!!

    private var datePickerDialogTo: DatePickerDialog? = null
    private var datePickerDialogFrom: DatePickerDialog? = null

    private var dateSetListenerTo: OnDateSetListener? = null
    private var dateSetListenerFrom: OnDateSetListener? = null

    private var dateFromString: String = ""
    private var dateToString: String = ""
    private var currencyFrom: String = ""
    private var currencyTo: String = ""

    private var spinnerFromAdapter:CustomAdapter?=null
    private var spinnerToAdapter:CustomAdapter?=null

    private lateinit var currenciesNames: ArrayList<String>

    private lateinit var chart: LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGraphBinding.inflate(inflater, container, false)

        viewModel = requireContext().appComponent.factory.create(ViewModelGraphFragment::class.java)

        binding.dateToButton.setOnClickListener { datePickerDialogTo?.show() }
        binding.dateFromButton.setOnClickListener { datePickerDialogFrom?.show() }

        initDatePickers()
        setupChart()
        setupObserver()
        initSpinners()

        return binding.root
    }

    private fun initSpinners() {

        currenciesNames = viewModel.getCurrenciesNames()

        spinnerFromAdapter =
            CustomAdapter(requireContext(), currenciesNames)
        //Spinner From
        binding.spinnerFrom.adapter = spinnerFromAdapter
        binding.spinnerFrom.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                currencyFrom = currenciesNames[i]
                updateGraph()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }

        spinnerToAdapter =
            CustomAdapter(requireContext(), currenciesNames)
        //Spinner To
        binding.spinnerTo.adapter = spinnerToAdapter
        binding.spinnerTo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                currencyTo = currenciesNames[i]
                updateGraph()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }
    }

    private fun updateGraph() {
        if (currencyFrom != currencyTo && currencyFrom != "" && currencyTo != "") {
            viewModel.getCurrencyHistory(
                currencyFrom,
                currencyTo,
                dateFromString,
                dateToString
            )
        }
    }

    private fun initDatePickers() {
        //Current date
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val style: Int = AlertDialog.THEME_HOLO_LIGHT

        //Set "From" date listener
        dateSetListenerFrom =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month += 1
                val date: String = makeUiDateString(day, month, year)
                binding.dateFromButton.text = date
                datePickerDialogTo?.datePicker?.minDate = getDate(day + 1, month, year).time
                dateFromString = makeRequestDateString(date)
                updateGraph()
            }


        //Set "To" date listener


        dateSetListenerTo =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month += 1
                val date: String = makeUiDateString(day, month, year)
                binding.dateToButton.text = date

                datePickerDialogFrom?.datePicker?.maxDate = getDate(day - 1, month, year).time
                dateToString = makeRequestDateString(binding.dateToButton.text.toString())
                updateGraph()
            }


        //Set initial "From" date
        binding.dateFromButton.text = makeUiDateString(day, month, year - 1)
        dateFromString = makeRequestDateString("$day-$month-" + (year - 1))

        //Limit "From" date
        datePickerDialogFrom =
            DatePickerDialog(requireContext(), style, dateSetListenerFrom, year, month, day)
        datePickerDialogFrom?.datePicker?.minDate =
            SimpleDateFormat(PATTERN_DATE_FORMAT).parse(MIN_API_DATE).time
        datePickerDialogFrom?.datePicker?.maxDate = System.currentTimeMillis().minus(DAY_MILLIS)

        //Set current date
        binding.dateToButton.text = makeUiDateString(day, month + 1, year)
        dateToString = makeRequestDateString(binding.dateToButton.text.toString())

        //Limit "To" date
        datePickerDialogTo =
            DatePickerDialog(requireContext(), style, dateSetListenerTo, year, month, day)
        datePickerDialogTo?.datePicker?.maxDate = System.currentTimeMillis()

    }

    @SuppressLint("SimpleDateFormat")
    private fun makeUiDateString(day: Int, month: Int, year: Int): String {
        val date = SimpleDateFormat(PATTERN_DATE_FORMAT).parse("$year-$month-$day")
        return SimpleDateFormat(UI_DATE_FORMAT).format(date)
    }

    @SuppressLint("SimpleDateFormat")
    private fun makeRequestDateString(date: String): String {
        val date = SimpleDateFormat(UI_DATE_FORMAT).parse(date)
        return SimpleDateFormat(PATTERN_DATE_FORMAT).format(date)
    }

    private fun getDate(day: Int, month: Int, year: Int): Date {
        return SimpleDateFormat(PATTERN_DATE_FORMAT).parse("$year-$month-$day")
    }

    //MPChart
    private fun setupChart() {
        // // Chart Style // //
        chart = binding.chart1

        // background color
        chart.setBackgroundColor(Color.WHITE);

        // disable description text
        chart.description.isEnabled = false;

        // enable touch gestures
        chart.setTouchEnabled(true);

        // set listeners
        //chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);

        // create marker to display box when values are selected
        val mv = MyMarkerView(
            requireContext(),
            R.layout.custom_marker_view
        );

        // Set the marker to the chart
        mv.chartView = chart;
        chart.marker = mv;

        // enable scaling and dragging
        chart.isDragEnabled = true;
        chart.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(true);


        // // X-Axis Style // //
        var xAxis: XAxis = chart.xAxis

        // vertical grid lines
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.granularity=1f
        xAxis.labelCount=4


        // Formatter to adjust epoch time to readable date
        chart.xAxis.valueFormatter = DateXAxisValueFormatter()

        // // Y-Axis Style // //
        var yAxis: YAxis = chart.axisLeft

        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false

        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f)

        // axis range
        yAxis.axisMinimum = 0f


        // // Create Limit Lines // //
        val tfRegular = Typeface.createFromAsset(requireContext().assets, "OpenSans-Regular.ttf")
        val tfLight = Typeface.createFromAsset(requireContext().assets, "OpenSans-Light.ttf")

        val llXAxis = LimitLine(9f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f
        llXAxis.typeface = tfRegular

        /*
        val ll1 = LimitLine(150f, "Upper Limit")
        ll1.lineWidth = 4f
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f
        ll1.typeface = tfRegular
        */

        // draw limit lines behind data instead of on top

        // draw limit lines behind data instead of on top
        //yAxis.setDrawLimitLinesBehindData(true)
        //xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines

        // add limit lines
        //yAxis.addLimitLine(ll1)
        //yAxis.addLimitLine(ll2)
        //xAxis.addLimitLine(llXAxis);

        // draw points over time
        chart.animateX(1500)

        // get the legend (only possible after setting data)
        val l: Legend = chart.legend

        // draw legend entries as lines
        l.form = Legend.LegendForm.LINE
    }

    private fun setData() {
        //Prepare data for UI
        viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Default) {
            val values: ArrayList<Entry> = ArrayList()

            var i = 0f

            var maxValue = 0f
            var minValue = viewModel.arrayHistory[0].rate
            viewModel.arrayHistory.forEach() { it ->
                if (it.rate > maxValue) {
                    maxValue = it.rate.toFloat()
                }
                if (it.rate < minValue) {
                    minValue = it.rate
                }
                values.add(Entry(it.date.time.toFloat(), it.rate.toFloat()))
                i++
            }

            //Update UI
            viewLifecycleOwner.lifecycle.coroutineScope.launch(Dispatchers.Main) {

                chart.axisLeft.axisMaximum = maxValue * scaleAxisLimits
                chart.axisLeft.axisMinimum = (minValue / scaleAxisLimits).toFloat()

                val set1: LineDataSet
                val label="${resources.getString(R.string.from)} $currencyFrom ${resources.getString(R.string.to)} $currencyTo"
                if (chart.data != null &&
                    chart.data.dataSetCount > 0
                ) {

                    set1 = chart.data.getDataSetByIndex(0) as LineDataSet
                    set1.label =label
                    chart.invalidate()
                    set1.values = values
                    set1.notifyDataSetChanged()
                    chart.data.notifyDataChanged()
                    chart.notifyDataSetChanged()

                } else {
                    // create a dataset and give it a type
                    set1 = LineDataSet(values,  label)
                    set1.setDrawIcons(false)

                    // draw dashed line
                    set1.enableDashedLine(10f, 5f, 0f)

                    // black lines and points
                    set1.color = Color.BLACK
                    set1.setCircleColor(Color.BLACK)

                    // line thickness and point size
                    set1.lineWidth = 1f
                    set1.circleRadius = 3f

                    // draw points as solid circles
                    set1.setDrawCircleHole(false)
                    set1.setDrawCircles(false)

                    // customize legend entry
                    set1.formLineWidth = 1f
                    set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                    set1.formSize = 15f

                    // text size of values

                    set1.setDrawValues(false)
                    //set1.valueTextSize = 0f

                    // draw selection line as dashed
                    set1.enableDashedHighlightLine(10f, 5f, 0f)

                    // set the filled area
                    set1.setDrawFilled(true)
                    set1.fillFormatter =
                        IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }

                    // set color of filled area
                    if (Utils.getSDKInt() >= 18) {
                        // drawables only supported on api level 18 and above
                        val drawable =
                            ContextCompat.getDrawable(requireContext(), R.drawable.fade_red)
                        set1.fillDrawable = drawable
                    } else {
                        set1.fillColor = Color.BLACK
                    }
                    val dataSets: ArrayList<ILineDataSet> = ArrayList()
                    dataSets.add(set1) // add the data sets

                    // create a data object with the data sets
                    val data = LineData(dataSets)

                    // set data
                    chart.data = data
                    chart.invalidate()
                }
            }
        }
    }

    @SuppressLint("ShowToast")
    private fun setupObserver() {
        viewModel.mutableStatus.observe(this.viewLifecycleOwner) { status ->
            when (status) {
                is Status.DATA -> {
                    setData()
                    binding.progressBar.visibility = View.GONE
                }
                is Status.LOADING -> {
                    //Show progress bar only for long response
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Status.ERROR -> {
                    Toast.makeText(
                        this.requireContext(),
                        getString(R.string.error)+"-"+status.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        datePickerDialogTo = null
        datePickerDialogFrom = null
        dateSetListenerTo = null
        dateSetListenerFrom = null
        spinnerToAdapter=null
        spinnerFromAdapter=null
    }
}