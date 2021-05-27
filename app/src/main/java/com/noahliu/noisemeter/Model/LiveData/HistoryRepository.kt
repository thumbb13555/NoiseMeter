package com.noahliu.noisemeter.Model.LiveData

import android.util.Log
import androidx.lifecycle.LiveData
import com.noahliu.noisemeter.Controller.HistoryRecord.Companion.TAG
import com.noahliu.noisemeter.Model.RoomDatabase.DataForm
import com.noahliu.noisemeter.Model.RoomDatabase.RoomDateBase
import com.noahliu.noisemeter.Model.RoomDatabase.Uao
import org.jetbrains.anko.doAsync

class HistoryRepository(myApplication: MyApplication) {

    private val uao:Uao
    private var mData:LiveData<MutableList<DataForm>>

    init {
        val db = RoomDateBase.getInstance(myApplication.applicationContext)
        uao = db!!.getUao()
        mData = uao.searchAll()

    }
    fun searchAll():LiveData<MutableList<DataForm>>{
        return mData
    }
    fun deleteData(dataForm: DataForm){
        doAsync{
            uao.deleteData(dataForm)
        }
    }
    fun insertData(dataForm: DataForm){
        doAsync {
            uao.insertData(dataForm)
        }
    }
    fun updateData(dataForm: DataForm){
        doAsync {
            uao.updateData(dataForm)
        }
    }

    fun tableCountSize():LiveData<Int>{
        return uao.getLiveTableSize()
    }

}