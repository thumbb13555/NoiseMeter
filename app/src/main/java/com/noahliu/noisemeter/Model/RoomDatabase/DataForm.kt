package com.noahliu.noisemeter.Model.RoomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.noahliu.noisemeter.Model.Entity.HistoryEntity
import java.io.Serializable

@Entity(tableName = RoomDateBase.TABLE_NAME)
@TypeConverters(DataForm.DataConverters::class)

class DataForm(
    var name: String,
    var time: String,
    var date: String,
    var max: String,
    var min: String,
    var avg: String,
    var data: MutableList<HistoryEntity>,
    var order:Int
) :Serializable {

    @PrimaryKey(autoGenerate = true)
    var id = 0


    class DataConverters : Serializable {

        @TypeConverter
        fun object2String(list: MutableList<HistoryEntity>): String {
            return Gson().toJson(list)
        }

        @TypeConverter
        fun string2Object(string: String): List<HistoryEntity> {
            return Gson().fromJson(string, Array<HistoryEntity>::class.java).toMutableList()
        }
    }
}


