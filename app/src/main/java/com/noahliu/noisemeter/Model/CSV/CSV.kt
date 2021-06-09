package com.noahliu.noisemeter.Model.CSV

import android.app.Activity
import android.app.ProgressDialog
import android.util.Log
import com.noahliu.noisemeter.Controller.RecordDetail
import com.noahliu.noisemeter.Model.FileExport.FileExport
import com.noahliu.noisemeter.Model.RoomDatabase.DataForm
import com.noahliu.noisemeter.R

class CSV(val activity:Activity) : FileExport() {
    var fileName = ""
    val progress = ProgressDialog(activity)


    override fun onPreExecute() {
        super.onPreExecute()
        progress.setTitle(R.string.exporting)
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progress.max = 100
        progress.show()

    }

    override fun doInBackground(vararg p0: DataForm): String {
        val data = p0[0]
        fileName = data.name
        val content = StringBuffer()

        for (i in 0 until data.data.size) {
            val num = data.data[i]
            content.append("${num.time},${num.data}\n")
            publishProgress(getPercent(i,data.data.size))
        }
        return content.toString()
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        progress.progress = values[0]!!
    }


    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        progress.dismiss()
        saveCSV(activity,fileName,result!!)
    }
    private fun getPercent(progress:Int,totalSize:Int):Int{
        val progressDouble = progress.toDouble()
        val totalDouble = totalSize.toDouble()
        return ((progressDouble/totalDouble)*100).toInt()
    }
}