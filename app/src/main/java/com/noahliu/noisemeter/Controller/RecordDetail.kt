package com.noahliu.noisemeter.Controller

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.noahliu.noisemeter.Model.Activity.BaseActivity
import com.noahliu.noisemeter.Model.Chart.CustomMakerView
import com.noahliu.noisemeter.Model.Chart.XLabel
import com.noahliu.noisemeter.Model.Entity.HistoryEntity
import com.noahliu.noisemeter.Model.Model.Utils.MyUtils
import com.noahliu.noisemeter.Model.ProgressDialog.loadingDialog
import com.noahliu.noisemeter.Model.RoomDatabase.DataForm
import com.noahliu.noisemeter.Model.RoomDatabase.RoomDateBase
import com.noahliu.noisemeter.R
import kotlinx.android.synthetic.main.activity_record_detail.*

class RecordDetail : BaseActivity() {
    private lateinit var progressDialog: loadingDialog
    companion object{
        val TAG = RecordDetail::class.java.simpleName+"My"
        val RECORD_ACTIVITY = "GoRecordActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_detail)
        val dataID = intent.getIntExtra(RECORD_ACTIVITY, 60)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_RecordDetail)
        setToolbar(toolbar, "Detail data")
        progressDialog = loadingDialog(this)
        progressDialog.show()
        Thread{
            val data = RoomDateBase.getInstance(this@RecordDetail)!!.getUao().findDataById(dataID)
            val a = MyUtils.dataFilter(data.data.size / 10, data.data)
            runOnUiThread {
                setBaseUI(data)
                setChart(a)
            }
            progressDialog.dismiss()
        }.start()


    }

    @SuppressLint("SetTextI18n")
    private fun setBaseUI(dataForm: DataForm){
        val duration = MyUtils.timeCalculate(dataForm.data.size / 10)
        textView_Name.text = dataForm.name
        textView_ActivityDuration.text = getString(R.string.active_duration)+" $duration"
        textView_SaveTime.text = getString(R.string.save_time)+" ${dataForm.date}, ${dataForm.time}"
        textView_Max.text = getString(R.string.max_sound_intensity)+"\n${dataForm.max}"
        textView_Min.text = getString(R.string.min_sound_intensity)+"\n${dataForm.min}"
        textView_Average.text = getString(R.string.avg_tag)+"\n${dataForm.avg}"

    }
    private fun setChart(data: MutableList<HistoryEntity>){
        val chart = findViewById<LineChart>(R.id.lineChart_DetailData)
        chart.setViewPortOffsets(0f, 0f, 0f, 0f)
        chart.setBackgroundColor(Color.WHITE)
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(false)
        val x = chart.xAxis
        x.isEnabled = false
        x.valueFormatter = XLabel(data)

        val y = chart.axisLeft
        y.setLabelCount(6, false)
        y.textColor = Color.GRAY
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        y.setDrawGridLines(false)
        y.axisLineColor = Color.WHITE
        y.axisMaximum = 125f
        y.axisMinimum = -5f

        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.maxHighlightDistance = 300f
        val values = ArrayList<Entry>()
        for (i in 0 until data.size) {
            val float = data[i].data.toFloat()
            values.add(Entry(i.toFloat(), float))
        }

        val set1 = LineDataSet(values, getString(R.string.noise))
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER
        set1.cubicIntensity = 0.2f
        set1.setDrawFilled(true)
        set1.setDrawCircles(false)
        set1.lineWidth = 1.8f

        set1.setCircleColor(Color.WHITE)
        set1.highLightColor = Color.parseColor("#81E7EAF3")
        set1.highlightLineWidth = 3f
        set1.color = Color.parseColor("#3C8ECD")
        set1.fillColor = Color.parseColor("#97CDFF")
        set1.fillAlpha = 100
        set1.setDrawHorizontalHighlightIndicator(false)

        val chartData = LineData(set1)
        chartData.setValueTextSize(9f)
        chartData.setDrawValues(false)

        val makerView = CustomMakerView(this,R.layout.chart_makerviw).apply {
            chartView = chart
        }
        chart.marker = makerView
        chart.data = chartData
    }

}