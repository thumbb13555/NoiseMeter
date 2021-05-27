package com.noahliu.noisemeter.Model.Chart

import android.content.Context
import android.util.Log
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.noahliu.noisemeter.Controller.RecordDetail
import com.noahliu.noisemeter.R

class CustomMakerView(context: Context, layout:Int) : MarkerView(context,layout){

    val TAG = RecordDetail.TAG

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val tvData = findViewById<TextView>(R.id.textView_Data)
        val tvTitle = findViewById<TextView>(R.id.textView_Title)

        val stringBuffer = StringBuffer()
        val list=  chartView.data.dataSets
        for (i in 0 until list.size) {
            stringBuffer.append(list[i].label+": ${list[i].getEntryForIndex(e!!.x.toInt()).y}")
            stringBuffer.append(context.getString(R.string.db))
        }
        tvTitle.text = chartView.xAxis.valueFormatter.getAxisLabel(e!!.x,chartView.xAxis)
        tvData.text = stringBuffer

        super.refreshContent(e, highlight)
    }

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        val yPosition = -height-100f

        return if (posX<300) MPPointF(-width / 2f+180, yPosition)
        else MPPointF(-width / 2f-180, yPosition)
    }
}