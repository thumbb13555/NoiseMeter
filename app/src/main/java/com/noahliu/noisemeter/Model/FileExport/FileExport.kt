package com.noahliu.noisemeter.Model.FileExport

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import com.noahliu.noisemeter.Controller.RecordDetail
import com.noahliu.noisemeter.Model.RoomDatabase.DataForm
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset

abstract class FileExport : AsyncTask<DataForm, Int, String>() {
    val TAG = RecordDetail.TAG

    companion object {
        val absoluteFilePath = "/storage/emulated/0/NoiseMeter"
    }

    fun saveCSV(activity: Activity, fileName: String, content: String): Boolean {
        try {
            addFile()
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            builder.detectFileUriExposure()
            val out: FileOutputStream = activity.openFileOutput(fileName, Context.MODE_PRIVATE)
            out.write(content.toByteArray(Charset.forName("UTF-8")))
            out.close()

            val fileLocation =
                File("$absoluteFilePath/$fileName.csv")
            val fos = FileOutputStream(fileLocation)
            fos.write(content.toByteArray(Charset.forName("UTF-8")))
            val path: Uri = Uri.fromFile(fileLocation)
            val fileIntent = Intent(Intent.ACTION_SEND)
            fileIntent.type = "text/csv"
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, fileName)
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
            activity.startActivity(Intent.createChooser(fileIntent, "輸出檔案"))
            return true
        } catch (e: Exception) {
            Log.w(TAG, "saveCSV: $e")
            return false
        }
    }

    private fun savePDF(activity: Activity, fileName: String) {
        try {
            addFile()
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            builder.detectFileUriExposure()
            val filelocation = File("$absoluteFilePath/$fileName.pdf")
            val path = Uri.fromFile(filelocation)
            val fileIntent = Intent(Intent.ACTION_SEND)
            fileIntent.type = "text/plain"
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, fileName)
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            fileIntent.putExtra(Intent.EXTRA_STREAM, path)
            activity.startActivity(Intent.createChooser(fileIntent, "輸出檔案"))
        } catch (e: Exception) {
            Toast.makeText(activity, "輸出失敗", Toast.LENGTH_SHORT).show();
        }
    }

    private fun addFile() {
        val file = File(absoluteFilePath)
        if (file.mkdir()) {
            Log.d(TAG, "已新增資料夾")
        } else {
            Log.d(TAG, "資料夾已存在")
        }
    }
}