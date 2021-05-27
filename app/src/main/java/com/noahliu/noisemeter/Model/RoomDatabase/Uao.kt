package com.noahliu.noisemeter.Model.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface Uao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertData(dataForm: DataForm)

    @Delete
    fun deleteData(dataForm: DataForm)

    @Update
    fun updateData(dataForm: DataForm)

    @Query("SELECT * FROM savetable ORDER BY `order` ASC")
    fun searchAll():LiveData<MutableList<DataForm>>

    @Query("SELECT COUNT(*) AS NumberOfOrders FROM savetable")
    fun getTableSize():Int

    @Query("SELECT COUNT(*) AS NumberOfOrders FROM savetable")
    fun getLiveTableSize():LiveData<Int>

    @Query("UPDATE savetable set `order` = :order WHERE id = :id")
    fun updateOrders(id:Int, order:Int)

    @Query("SELECT * FROM savetable WHERE id = :id")
    fun findDataById(id:Int):DataForm
//    @Query("SELECT * FROM mytable ORDER BY `order` ASC")
//    fun getAllData():MutableList<MainData>
//
//    @Query("Update mytable set `order` = :order where id = :id")
//    fun updateOrder(id:Int,order:Int)
}