package com.noahliu.noisemeter.Model.Activity


import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.charts.LineChart
import com.noahliu.noisemeter.Model.LiveData.HistoryViewModel
import com.noahliu.noisemeter.R

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var chart: LineChart
    val viewModel: HistoryViewModel by lazy {
        ViewModelProviders.of(this).get(HistoryViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.statusBarColor = getColor(R.color.cloudSystemThemeColor)
    }

   fun setToolbar(toolbar: Toolbar, title: String): ActionBar {
       setSupportActionBar(toolbar)
       supportActionBar!!.title = title
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
       toolbar.contentInsetStartWithNavigation = 0
       toolbar.setNavigationOnClickListener { finish() }
       return supportActionBar!!
    }

    fun showToast(context: Context,word:String){
        Toast.makeText(context, word, Toast.LENGTH_SHORT).show()
    }

    fun setBaseDialog(view:View,activity: Activity,title: String):AlertDialog{
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setView(view)
        val dialog = alertDialog.create()
        val btCancel = view.findViewById<Button>(R.id.button_Cancel)
        val tvTitle = view.findViewById<TextView>(R.id.textView_Title)
        tvTitle.text = title
        btCancel?.setOnClickListener { dialog.dismiss() }
        dialog.show()
        val dm = DisplayMetrics() //取得螢幕解析度
        windowManager.defaultDisplay.getMetrics(dm) //取得螢幕寬度值
        dialog.window
            ?.setLayout(dm.widthPixels - 230, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window
            ?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

}