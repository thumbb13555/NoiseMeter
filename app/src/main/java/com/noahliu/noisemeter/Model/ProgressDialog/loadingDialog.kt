package com.noahliu.noisemeter.Model.ProgressDialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import com.noahliu.noisemeter.R

class loadingDialog(private var activity: Activity) {

    private val dialog:AlertDialog
    init {
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setView(LayoutInflater.from(activity).inflate(R.layout.dialog_loading,null))
        dialog = alertDialog.create()


    }

    fun show(){
        dialog.show()
        val dm = DisplayMetrics() //取得螢幕解析度
        activity.windowManager.defaultDisplay.getMetrics(dm) //取得螢幕寬度值
        dialog.window
            ?.setLayout(dm.widthPixels - 230, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun dismiss(){
        dialog.dismiss()
    }


}