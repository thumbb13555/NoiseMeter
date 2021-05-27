package com.noahliu.noisemeter.Model.Interface

import com.noahliu.noisemeter.Model.Entity.HistoryEntity

interface ArrangeInterface {
    fun onArrangeRespond(array: MutableList<HistoryEntity>)
}