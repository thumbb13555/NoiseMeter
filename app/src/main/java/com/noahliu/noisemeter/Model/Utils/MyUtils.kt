package com.noahliu.noisemeter.Model.Model.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import com.noahliu.noisemeter.Controller.RecordDetail.Companion.TAG
import com.noahliu.noisemeter.Model.Entity.HistoryEntity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

object MyUtils {
    @JvmStatic
    fun main(args: Array<String>) {

    }
    fun timeCalculate(sec: Int):String{
        val second = sec % 60
        val minute = (sec % 3600) / 60
        val hour = sec/3600
        return when {
            sec<60 -> "${second}s"
            sec in 60..3599 -> "${minute}m ${second}s"
            else -> "${hour}h ${minute}m ${second}s"
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun dataFilter(sec: Int, originArray:MutableList<HistoryEntity>):MutableList<HistoryEntity>{
        if (sec<=480) return originArray//小於8分鐘直接return
        val sdf = SimpleDateFormat("HH:mm:ss:SSS")
        val arrayList = ArrayList<HistoryEntity>()
        var firstTime = sdf.parse(originArray[0].time)!!.time/1000
        var count = 0
        var total = 0.0f
        val interval = if(sec in 481..7200) {10}else{60}
        for (i in 0 until originArray.size) {
            val item = originArray[i]
            val nowTime = (sdf.parse(item.time)!!.time)/1000
            when {
                i == 0 -> {
                    arrayList.add(item)
                }
                firstTime - nowTime >= interval -> {
                    firstTime = nowTime
                    arrayList.add(HistoryEntity(item.time, (total/count).roundToInt().toFloat().toString()))
                    total = 0f
                    count = 0
                }
                else -> {
                    total += item.data.toFloat()
                    count ++
                }
            }
        }
        return arrayList
    }
    fun measureSize(measureSpec: Int, defaultSize: Int):Int{
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)
        var result = defaultSize
        if (specMode == View.MeasureSpec.AT_MOST) {
            result = defaultSize
        } else if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        }
        return result
    }
    fun dp2px(dp: Float): Float {
        val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
        return dp * metrics.density
    }
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * getDensity(context)
    }

    /**
     * Covert px to dp
     * @param px
     * @param context
     * @return dp
     */
    fun convertPixelToDp(px: Float, context: Context): Float {
        return px / getDensity(context)
    }

    /**
     * 取得螢幕密度
     * 120dpi = 0.75
     * 160dpi = 1 (default)
     * 240dpi = 1.5
     * @param context
     * @return
     */
    fun getDensity(context: Context): Float {
        val metrics: DisplayMetrics = context.resources.displayMetrics
        return metrics.density
    }
}