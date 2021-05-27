package com.noahliu.noisemeter.Model.AsyncTask

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import com.noahliu.noisemeter.Model.Entity.HistoryEntity
import com.noahliu.noisemeter.Model.Interface.ArrangeInterface
import com.noahliu.noisemeter.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ArrangeDataAsync(val activity: Activity) : AsyncTask<ArrayList<Float>,Int,MutableList<HistoryEntity>>() {

    lateinit var onRespond:ArrangeInterface
    val progress = ProgressDialog(activity)

    override fun onPreExecute() {
        super.onPreExecute()
        progress.setTitle(activity.getString(R.string.loading))
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progress.max = 100
        progress.show()
    }

    @SuppressLint("SimpleDateFormat")
    override fun doInBackground(vararg p0: ArrayList<Float>): MutableList<HistoryEntity> {
        val floatArray = p0[0]
        val arrayList = ArrayList<HistoryEntity>()
        val sdf = SimpleDateFormat("HH:mm:ss:SSS")
        var time = sdf.format(Date())
        for (i in 0 until floatArray.size) {
            arrayList.add(HistoryEntity(time,floatArray[i].toString()))
            progress.progress = ((i.toDouble()/floatArray.size.toDouble())*100).toInt()
            val calender = Calendar.getInstance()
            calender.time = sdf.parse(time)
            calender.add(Calendar.MILLISECOND,-100)
            time = sdf.format(calender.time)

        }
        return arrayList
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        progress.progress = values[0]!!
    }

    override fun onPostExecute(result: MutableList<HistoryEntity>) {
        super.onPostExecute(result)
        progress.dismiss()
        onRespond.onArrangeRespond(result)
    }

}