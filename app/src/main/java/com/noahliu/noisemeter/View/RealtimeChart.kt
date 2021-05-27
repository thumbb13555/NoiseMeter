package com.noahliu.noisemeter.View

import android.app.Activity
import android.graphics.Color
import android.os.Parcelable
import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.noahliu.noisemeter.Controller.MainActivity
import com.noahliu.noisemeter.Model.Interface.MeasureInterface
import com.noahliu.noisemeter.Model.Model.Utils.MyUtils
import com.noahliu.noisemeter.R
import java.io.Serializable
import kotlin.math.roundToInt

class RealtimeChart {
    companion object{
        val TAG = MainActivity.TAG
    }
    private var thread = Thread()
    lateinit var respond: MeasureInterface
    private val activity: Activity
    var chart: LineChart

    constructor(activity: Activity, chart: LineChart) {
        this.activity = activity
        this.chart = chart
        initChart()
    }

    constructor(activity: Activity, chart: LineChart, originData:FloatArray) {
        this.activity = activity
        this.chart = chart
        initChart()
        for (data in originData) {
            createData(data)
        }
    }

    fun clearChart(){
        chart.clear()
        initChart()
    }

    private fun initChart() {
        chart.description.isEnabled = false
        chart.setTouchEnabled(false)
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)

        val data = LineData()
        data.setValueTextColor(Color.BLACK)
        chart.data = data
        val l = chart.legend
        l.form = Legend.LegendForm.LINE
        l.textColor = Color.RED

        val xl = chart.xAxis
        xl.textColor = Color.BLACK
        xl.setDrawGridLines(true)
        xl.setAvoidFirstLastClipping(true)
        xl.isEnabled = true
        xl.position = XAxis.XAxisPosition.BOTTOM
        xl.setLabelCount(4, true)
        xl.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return MyUtils.timeCalculate((value / 10).roundToInt())
            }
        }

        val leftAxis = chart.axisLeft
        leftAxis.textColor = Color.BLACK
        leftAxis.axisMaximum = 120f
        leftAxis.axisMinimum = 0f
        leftAxis.setDrawGridLines(true)

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
        chart.legend.isEnabled = false
    }


    private fun createSet(): LineDataSet {
        val set = LineDataSet(null, "Noise")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.color = activity.getColor(R.color.colorChartLine)
        set.lineWidth = 2f
        set.setDrawCircles(false)
        set.fillAlpha = 0
        set.fillColor = activity.getColor(R.color.colorChartLine)
        set.highLightColor = Color.rgb(244, 117, 117)
        set.valueTextColor = Color.BLACK
        set.valueTextSize = 9f
        set.setDrawValues(false)
        return set
    }

    fun createData(db: Float) {
        thread.interrupt()
        val runnable = Runnable { addData(db) }
        thread = Thread {
            activity.runOnUiThread(runnable)
        }
        thread.start()
    }

    private fun addData(db: Float) {
        val data = chart.data ?: return

        var set = data.getDataSetByIndex(0)
        if (set == null) {
            set = createSet()
            data.addDataSet(set)
        }

        data.addEntry(Entry(set.entryCount.toFloat(), db), 0)
        data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.setVisibleXRange(0f,300f)
        chart.moveViewToX(data.entryCount.toFloat())
    }
}