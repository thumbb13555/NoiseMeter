package com.noahliu.noisemeter.Controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.facebook.stetho.Stetho
import com.github.mikephil.charting.charts.LineChart
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.noahliu.noisemeter.Model.MediaRec.VolumeRecorder
import com.noahliu.noisemeter.Model.Activity.BaseActivity
import com.noahliu.noisemeter.Model.AsyncTask.ArrangeDataAsync
import com.noahliu.noisemeter.Model.Entity.HistoryEntity
import com.noahliu.noisemeter.Model.Interface.ArrangeInterface
import com.noahliu.noisemeter.Model.Interface.MeasureInterface
import com.noahliu.noisemeter.Model.Model.Utils.MyUtils
import com.noahliu.noisemeter.Model.RoomDatabase.DataForm
import com.noahliu.noisemeter.Model.RoomDatabase.RoomDateBase
import com.noahliu.noisemeter.Model.SharedPreferences.MySharedPreferences
import com.noahliu.noisemeter.R
import com.noahliu.noisemeter.View.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_record_detail.*
import kotlinx.android.synthetic.main.dialog_save.*
import kotlinx.android.synthetic.main.dialog_save.view.*
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class MainActivity : BaseActivity(), MeasureInterface, BottomNavigationView.OnNavigationItemSelectedListener{

    companion object {
        val TAG = MainActivity::class.java.simpleName + "My"
        var calibration = 0
        const val ROTATE_SAVE = "ROTATE_SAVE"
    }
    private var tvCalRes:TextView? = null
    private var externalHasGone = false
    private var recordMediaHasGone = false
    private lateinit var realtimeChartSetting: RealtimeChart
    private var record: VolumeRecorder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        init()
        val chart = findViewById<LineChart>(R.id.lineChart_CurrentValue)

        realtimeChartSetting = if (savedInstanceState != null){
            val dataSave = savedInstanceState.getFloatArray(ROTATE_SAVE)
            RealtimeChart(this, chart,dataSave!!)
        }else{
            RealtimeChart(this, chart)
        }
        realtimeChartSetting = RealtimeChart(this, chart)

        realtimeChartSetting.respond = this
    }

    private fun init(){
        Stetho.initializeWithDefaults(this)
        val bottomView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomView.menu.setGroupCheckable(0,false,false)
        bottomView.setOnNavigationItemSelectedListener(this)
        val btRecord = findViewById<DraggingButton>(R.id.button_Reset)
        calibration = MySharedPreferences.readCalibration(this)
        btRecord.setOnClickListener {
            realtimeChartSetting.clearChart()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val dataArray = ArrayList<Float>()
        val chartItem = realtimeChartSetting.chart.data.dataSets[0]
        for (i in 0 until chartItem.entryCount) {
            dataArray.add(chartItem.getEntryForIndex(i).y)
        }
        outState.putFloatArray(ROTATE_SAVE,dataArray.toFloatArray())




    }

    private fun checkPermission(): Boolean {
        externalHasGone = checkSelfPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        recordMediaHasGone = checkSelfPermission(
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var permissions = arrayOfNulls<String>(0)
            if (!externalHasGone && !recordMediaHasGone) {
                permissions = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
                )
                requestPermissions(permissions, 102)
            } else if (recordMediaHasGone && !externalHasGone) {
                permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissions, 101)
            } else if (externalHasGone && !recordMediaHasGone) {
                permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
                requestPermissions(permissions, 100)
            } else if (externalHasGone && recordMediaHasGone){
                initVolumeRecord()
            }
        }
        return false
    }

    private fun initVolumeRecord() {
        if (record == null){
            record = VolumeRecorder(this, 100)
            record!!.respond = this
            if (!record!!.isRecoding)record!!.startRecord()
        }
    }

    override fun onStart() {
        super.onStart()
        if (record == null || record!!.isRecoding)return
        if (!record!!.isRecoding)record!!.startRecord()
    }


    override fun onStop() {
        super.onStop()
        if (record == null)return
        if (record!!.isRecoding)record!!.stopRecord()
    }

    @SuppressLint("SetTextI18n")
    override fun dbRespond(db: Int) {

        val volBar = findViewById<VolumeBar>(R.id.volumeBar)
        val meter = findViewById<Meter>(R.id.meter)
        val inMin = findViewById<InfoBoard>(R.id.infoBoard_Min)
        val inAvg = findViewById<InfoBoard>(R.id.infoBoard_Avg)
        val inMax = findViewById<InfoBoard>(R.id.infoBoard_Max)
        volBar.setVolumeBar(db)
        meter.setValue(db)

        realtimeChartSetting.createData(db.toFloat())

        if (tvCalRes!= null){
            tvCalRes!!.text = "$db($calibration)db"
        }
        val cData = realtimeChartSetting.chart
        inMin.setValue("${cData.data.yMin.roundToInt()}db")
        inMax.setValue("${cData.data.yMax.roundToInt()}db")
        var avgValue = 0f

        for (i in 0 until cData.data.entryCount) {
            val nowData = cData.data.dataSets[0].getEntryForIndex(i).y
            avgValue += nowData
        }

        avgValue /= cData.data.entryCount
        inAvg.setValue("${avgValue.roundToInt()}db")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        externalHasGone = checkSelfPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        recordMediaHasGone = checkSelfPermission(
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (!recordMediaHasGone && !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            goPermissionPage()
        } else if (!externalHasGone && !shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
            goPermissionPage()
        } else checkPermission()
    }
    private fun goPermissionPage(){
        val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.fromParts("package",this.packageName,null)
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_History->{
                val intent = Intent(this@MainActivity,HistoryRecord::class.java)
                startActivity(intent)
            }
            R.id.action_Calibration -> {
                val saveCal = calibration
                val baseView = layoutInflater.inflate(R.layout.dialog_calibration, null)
                val dialog = setBaseDialog(baseView, this, getString(R.string.calibration))
                val btOK = baseView.findViewById<Button>(R.id.button_Ok)
                tvCalRes = baseView.findViewById(R.id.textView_reslltInfo)
                val btPlus = baseView.findViewById<ImageButton>(R.id.imageButton_Plus)
                val btMinus = baseView.findViewById<ImageButton>(R.id.imageButton_minus)
                val btCancel = baseView.findViewById<Button>(R.id.button_Cancel)
                btPlus.setOnClickListener { calibration++ }
                btMinus.setOnClickListener { calibration-- }
                var checkSave = false
                btOK.setOnClickListener {
                    MySharedPreferences.saveCalibration(this, calibration)
                    checkSave = true
                    dialog.dismiss()
                }
                dialog.setOnDismissListener {
                    if (!checkSave) calibration = saveCal
                }
                btCancel.setOnClickListener {
                    calibration = saveCal
                    dialog.dismiss()
                }
                dialog.show()
            }

            R.id.action_Save -> {
                val chartData = realtimeChartSetting.chart.data
                val arrayList = ArrayList<Float>()
                for (i in 0 until chartData.entryCount) arrayList.add(
                    chartData.dataSets[0].getEntryForIndex(i).y
                )
                arrayList.reverse()
                val task = ArrangeDataAsync(this)
                task.execute(arrayList)
                task.onRespond = object : ArrangeInterface {
                    @SuppressLint("SimpleDateFormat", "SetTextI18n")
                    override fun onArrangeRespond(array: MutableList<HistoryEntity>) {

                        val view = layoutInflater.inflate(R.layout.dialog_save, null)
                        val saveDialog =
                            setBaseDialog(view, this@MainActivity, getString(R.string.save_data))
                        val btOK = view.findViewById<Button>(R.id.button_Ok)
                        val duration = MyUtils.timeCalculate(array.size / 10)
                        val max = infoBoard_Max.getText()
                        val min = infoBoard_Min.getText()
                        val avg = infoBoard_Avg.getText()
                        val date = SimpleDateFormat("yyyy/MM/dd").format(Date())
                        val time = SimpleDateFormat("HH:mm:ss").format(Date())
                        view.textView_ActivityDuration.text =
                            getString(R.string.active_duration) + " " + duration
                        view.textView_MaxDialogTag.text =
                            getString(R.string.max_sound_intensity) + " " + max
                        view.textView_MinDialogTag.text =
                            getString(R.string.min_sound_intensity) + " " + min
                        view.textView_AvgDialogTag.text =
                            getString(R.string.average_sound_intensity) + " " + avg
                        view.textView_SaveTime.text = "${getString(R.string.save_time)} $date $time"

                        btOK.setOnClickListener {
                            val name = view.editText_Name.text.toString()
                            if (name.isEmpty()) {
                                showToast(this@MainActivity, getString(R.string.dialog_name_setting))
                                return@setOnClickListener
                            }
                            doAsync {
                                val dataCount =
                                    RoomDateBase.getInstance(this@MainActivity)!!.getUao()
                                        .getTableSize()
                                viewModel.insertData(
                                    DataForm(
                                        name, time, date, max, min, avg, array, dataCount)
                                )
                                runOnUiThread {
                                    saveDialog.dismiss()
                                }
                            }
                        }
                    }
                }
            }
        }
        return true
    }
}