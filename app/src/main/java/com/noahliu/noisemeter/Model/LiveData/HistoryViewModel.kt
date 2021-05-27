package com.noahliu.noisemeter.Model.LiveData

import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.noahliu.noisemeter.Controller.HistoryRecord.Companion.TAG
import com.noahliu.noisemeter.Model.RoomDatabase.DataForm

class HistoryViewModel : ViewModel(), LifecycleObserver {

    private val mRepository : HistoryRepository = HistoryRepository(MyApplication.instance)
    private var mData:LiveData<MutableList<DataForm>>
    init {
        mData = mRepository.searchAll()
    }
    fun searchAllData():LiveData<MutableList<DataForm>>{
        return mData
    }

    fun deleteData(dataForm: DataForm){
         mRepository.deleteData(dataForm)
    }

    fun insertData(dataForm: DataForm){mRepository.insertData(dataForm)}

    fun updateData(dataForm: DataForm){mRepository.updateData(dataForm)}

    fun getDataBaseSize():LiveData<Int>{return mRepository.tableCountSize()}

}