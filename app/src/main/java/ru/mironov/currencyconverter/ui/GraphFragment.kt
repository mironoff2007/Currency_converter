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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.mironov.currency_converter.util.FormatNumbers
import ru.mironov.currencyconverter.R
import ru.mironov.currencyconverter.appComponent
import ru.mironov.currencyconverter.databinding.FragmentGraphBinding
import ru.mironov.currencyconverter.model.Status
import ru.mironov.currencyconverter.model.ViewModelGraphFragment
import ru.mironov.currencyconverter.ui.spinner.CustomAdapter
import java.text.SimpleDateFormat
import java.util.*

class GraphFragment : Fragment() {

    companion object {
        private const val UI_DATE_FORMAT = "dd-MM-yyyy"
        private const val PATTERN_DATE_FORMAT = "yyyy-MM-dd"
        private const val MIN_API_DATE = "1999-1-1"
    }

    private lateinit var viewModel: ViewModelGraphFragment

    private var _binding: FragmentGraphBinding? = null

    private val binding get() = _binding!!

    private var datePickerDialogTo: DatePickerDialog? = null
    private var datePickerDialogFrom: DatePickerDialog? = null

    private var dateSetListenerTo: OnDateSetListener? = null
    private var dateSetListenerFrom: OnDateSetListener? = null

    private lateinit var chart: LineChart

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
        setupChart()
        setupObserver()
        initSpinner()

        return binding.root
    }

    fun initSpinner() {
        val mCustomAdapter =
            CustomAdapter(requireContext(), viewModel.getCurrenciesNames())
        //Spinner From
        binding.spinner.adapter = mCustomAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                viewModel.getCurrencyHistory(
                    viewModel.getCurrenciesNames()[i],
                    "RUB",
                    makeRequestDateString(binding.dateFromButton.text.toString()),
                    makeRequestDateString(binding.dateToButton.text.toString())
                )
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
        binding.dateToButton.text = makeUiDateString(day, month + 1, year)

        dateSetListenerTo =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month += 1
                val date: String = makeUiDateString(day, month, year)
                binding.dateToButton.text = date
                datePickerDialogFrom?.datePicker?.maxDate = getDate(day - 1, month, year).time
            }
        datePickerDialogTo =
            DatePickerDialog(requireContext(), style, dateSetListenerTo, year, month, day)
        datePickerDialogTo?.datePicker?.maxDate = System.currentTimeMillis()


        //Set from date
        dateSetListenerFrom =
            OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month += 1
                val date: String = makeUiDateString(day, month, year)
                binding.dateFromButton.text = date
                datePickerDialogTo?.datePicker?.minDate = getDate(day + 1, month, year).time
            }

        binding.dateFromButton.text = makeUiDateString(day, month, year)

        datePickerDialogFrom =
            DatePickerDialog(requireContext(), style, dateSetListenerFrom, year, month - 1, day)
        datePickerDialogFrom?.datePicker?.minDate =
            SimpleDateFormat(PATTERN_DATE_FORMAT).parse(MIN_API_DATE).time
        datePickerDialogFrom?.datePicker?.maxDate = System.currentTimeMillis().minus(86400000)

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
    fun setupChart() {
        /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
             WindowManager.LayoutParams.FLAG_FULLSCREEN);
         setContentView(R.layout.activity_linechart);
         */
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
        val mv = MyMarkerView(requireContext(), R.layout.custom_marker_view);

        // Set the marker to the chart
        mv.setChartView(chart);
        chart.setMarker(mv);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(true);


        // // X-Axis Style // //
        var xAxis: XAxis = chart.xAxis

        // vertical grid lines
        xAxis.enableGridDashedLine(10f, 10f, 0f)


        // // Y-Axis Style // //
        var yAxis: YAxis = chart.axisLeft

        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = false

        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f)

        // axis range
        yAxis.axisMinimum = 0f


        // // Create Limit Lines // //
        val tfRegular = Typeface.createFromAsset(requireContext().assets, "OpenSans-Regular.ttf");
        val tfLight = Typeface.createFromAsset(requireContext().assets, "OpenSans-Light.ttf");

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
        chart.animateX(1500);

        // get the legend (only possible after setting data)
        val l: Legend = chart.legend;

        // draw legend entries as lines
        l.form = Legend.LegendForm.LINE;
    }

    private fun setData() {
        val values: ArrayList<Entry> = ArrayList()

        var i=0f

        var maxValue=0f
        viewModel.arrayHistory.forEach(){ it->
            if(it.rate>maxValue){
                maxValue=it.rate.toFloat()
            }
            values.add(Entry(i ,it.rate.toFloat()))
            i++
        }

        chart.axisLeft.axisMaximum=maxValue*1.1f

        val set1: LineDataSet
        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, " to ")
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
                val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_red)
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
        }
    }

    @SuppressLint("ShowToast")
    private fun setupObserver() {
        viewModel.mutableStatus.observe(this.viewLifecycleOwner) { status ->
            when (status) {
                Status.DATA -> {
                    setData()
                    binding.progressBar.visibility = View.GONE
                }
                Status.LOADING -> {
                    //Show progress bar only for long response
                    binding.progressBar.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    Toast.makeText(
                        this.requireContext(),
                        getString(R.string.error),
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
    }
}