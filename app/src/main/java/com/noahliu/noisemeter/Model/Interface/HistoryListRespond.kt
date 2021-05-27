package com.noahliu.noisemeter.Model.Interface

import com.noahliu.noisemeter.Model.RoomDatabase.DataForm

interface HistoryListRespond {
    fun detailInfoRespond(itemData:DataForm)
    fun deleteRespond(itemData:DataForm,position:Int)
    fun modifyRespond(itemData:DataForm)
}