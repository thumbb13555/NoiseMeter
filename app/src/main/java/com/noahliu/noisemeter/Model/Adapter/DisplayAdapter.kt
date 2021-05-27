package com.noahliu.noisemeter.Model.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noahliu.noisemeter.Controller.HistoryRecord
import com.noahliu.noisemeter.Model.Interface.HistoryListRespond
import com.noahliu.noisemeter.Model.LiveData.HistoryViewModel
import com.noahliu.noisemeter.Model.Model.Utils.MyUtils
import com.noahliu.noisemeter.Model.RoomDatabase.DataForm
import com.noahliu.noisemeter.Model.RoomDatabase.RoomDateBase
import com.noahliu.noisemeter.R
import org.jetbrains.anko.doAsync

class DisplayAdapter(val activity: Activity) : RecyclerView.Adapter<DisplayAdapter.ViewHolder>() {

    lateinit var respond: HistoryListRespond
    private var data: MutableList<DataForm> = ArrayList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById<TextView>(R.id.textView_Title)
        val btDetail = itemView.findViewById<TextView>(R.id.button_Detail)
        val tvSaveTime = itemView.findViewById<TextView>(R.id.textView_SaveTimeTag)
        val tvDuration = itemView.findViewById<TextView>(R.id.textView_ActiveTTag)
        val btDelete = itemView.findViewById<ImageButton>(R.id.button_Delete)
        val btEdit = itemView.findViewById<ImageButton>(R.id.button_Edit)
    }

    fun setData(data: MutableList<DataForm>) {
        this.data = data
        notifyDataSetChanged()
    }
    fun removeData(data: MutableList<DataForm>, position: Int){
        this.data = data
        notifyItemRemoved(position)
    }
    fun getData():MutableList<DataForm>{
        return this.data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_history_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemData = data[position]
        holder.tvTitle.text = itemData.name
        holder.tvDuration.text =
            "${activity.getString(R.string.active_duration)} ${MyUtils.timeCalculate(itemData.data.size / 10)}"
        holder.tvSaveTime.text =
            "${activity.getString(R.string.save_time)} ${itemData.date} ${itemData.time}"

        holder.btDetail.setOnClickListener {
            respond.detailInfoRespond(itemData)
        }

        holder.btDelete.setOnClickListener {
            respond.deleteRespond(itemData, position)
        }

        holder.btEdit.setOnClickListener {
            respond.modifyRespond(itemData)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
