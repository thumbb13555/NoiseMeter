package com.noahliu.noisemeter.Model.MediaRec

import android.annotation.SuppressLint
import android.app.Activity
import android.media.MediaRecorder
import android.os.Handler
import android.os.Message
import android.util.Log
import com.noahliu.noisemeter.Controller.MainActivity.Companion.calibration
import com.noahliu.noisemeter.Model.Interface.MeasureInterface
import java.io.IOException
import kotlin.Exception
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.roundToInt

/**@param activity
 * @param interval 採樣時間*/
class VolumeRecorder(private val activity: Activity, private val interval: Long) {

    lateinit var respond: MeasureInterface
    private lateinit var recorder: MediaRecorder
    var isRecoding = false
    var isPause = false
    fun startRecord(){
        recorder = MediaRecorder()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile("/dev/null")
        try {
            isRecoding = true
            recorder.prepare()
            recorder.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        handler.post(task)
    }

    fun pauseRecord(){
        if (!isRecoding)return
        handler.removeCallbacks(task)
        isPause = true
    }


    fun resumeRecord(){
        if (!isRecoding)return
        handler.post(task)
        isPause = false
    }

    fun stopRecord(){
        handler.removeCallbacks(task)
        isRecoding = false
        try {
            recorder.stop()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private val handler:Handler = @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: Message) {
            when(msg.what){
                1 -> {
                    try {
                        val amplitude = recorder.maxAmplitude
                        val amplitudeDb = 20 * log10(abs(amplitude).toDouble())
                        var db = if (amplitudeDb.roundToInt() == -2147483648) {
                            return
                        } else {
                            amplitudeDb.roundToInt() + calibration
                        }
                        if (db > 120) db = 120
                        else if (db < -1) db = 0
                        respond.dbRespond(db)
                    } catch (e: Exception) {

                    }
                }
            }
            super.handleMessage(msg)
        }
    }
    private val task: Runnable = object : Runnable {
        override fun run() {
            handler.sendEmptyMessage(1)
            handler.postDelayed(this, interval)
        }
    }
}