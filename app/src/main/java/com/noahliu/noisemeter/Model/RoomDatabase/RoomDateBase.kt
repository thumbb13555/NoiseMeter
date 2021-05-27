package com.noahliu.noisemeter.Model.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(DataForm::class)],version = 1,exportSchema = false)

abstract class RoomDateBase : RoomDatabase() {
    companion object{
        const val DB_NAME = "RecordData.db"
        const val TABLE_NAME = "SaveTable"

        @Volatile
        private var instance: RoomDateBase? = null

        @Synchronized
        fun getInstance(context: Context): RoomDateBase? {
            if (instance == null) {
                instance = create(context)
            }
            return instance
        }

        private fun create(context: Context): RoomDateBase {
            return Room.databaseBuilder(context, RoomDateBase::class.java, DB_NAME).build()
        }

    }
    abstract fun getUao(): Uao
}