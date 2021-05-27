package com.noahliu.noisemeter.Model.Chart

import com.github.mikephil.charting.formatter.ValueFormatter
import com.noahliu.noisemeter.Model.Entity.HistoryEntity

class XLabel(private val array: MutableList<HistoryEntity>) : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return try {
            array[value.toInt()].time
        }catch (e:Exception){
            value.toInt().toString()
        }
    }
}