package com.noahliu.noisemeter.Model.SharedPreferences

import android.app.Activity
import android.content.Context

object MySharedPreferences {

    const val PREFERENCES = "NoiseMeterCalibration"
    const val CALIBRATION = "CalibrationSaved"

    fun saveCalibration(activity: Activity,value:Int):Boolean{
        val sharedPreferences = activity.getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        edit.putInt(CALIBRATION,value)
        return edit.commit()
    }

    fun readCalibration(activity: Activity):Int{
        val sharedPreferences = activity.getSharedPreferences(
            PREFERENCES, Context.MODE_PRIVATE
        )
        return sharedPreferences.getInt(CALIBRATION,0)
    }



}