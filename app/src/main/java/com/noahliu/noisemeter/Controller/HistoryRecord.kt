package com.noahliu.noisemeter.Controller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.noahliu.noisemeter.Model.Activity.BaseActivity
import com.noahliu.noisemeter.Model.Adapter.DisplayAdapter
import com.noahliu.noisemeter.Model.Interface.HistoryListRespond
import com.noahliu.noisemeter.Model.LiveData.HistoryViewModel
import com.noahliu.noisemeter.Model.Model.Utils.MyUtils
import com.noahliu.noisemeter.Model.ProgressDialog.loadingDialog
import com.noahliu.noisemeter.Model.RoomDatabase.DataForm
import com.noahliu.noisemeter.Model.RoomDatabase.RoomDateBase
import com.noahliu.noisemeter.R
import kotlinx.android.synthetic.main.activity_history_record.*
import kotlinx.android.synthetic.main.dialog_save.view.*
import org.jetbrains.anko.doAsync

class HistoryRecord : BaseActivity(), HistoryListRespond {
    companion object {
        val TAG = HistoryRecord::class.java.simpleName + "My"
    }
    var isOrder = false
    private lateinit var progressDialog: loadingDialog

    lateinit var mAdapter: DisplayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_record)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setToolbar(toolbar, getString(R.string.record_data))
        progressDialog = loadingDialog(this)
        progressDialog.show()
        setRecyclerViewList()
        
    }



    private fun setRecyclerViewList() {

        mAdapter = DisplayAdapter(this)
        recyclerView_HistoryDisplay.layoutManager = LinearLayoutManager(this)
        recyclerView_HistoryDisplay.adapter = mAdapter
        viewModel.searchAllData().observe(this){
            for (item in it){
                Log.d(TAG, "setRecyclerViewList: ${item.name}")
            }
            mAdapter.setData(it)
            progressDialog.dismiss()
        }
        mAdapter.respond = this

    }

    override fun detailInfoRespond(itemData: DataForm) {
        val intent = Intent(this,RecordDetail::class.java)
        intent.putExtra(RecordDetail.RECORD_ACTIVITY,itemData.id)
        startActivity(intent)
    }

    override fun deleteRespond(itemData: DataForm, position: Int) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Check")
        alertDialog.setMessage("Are you sure to delete the data?")
        alertDialog.setPositiveButton(android.R.string.ok) { p0, p1 ->
            viewModel.deleteData(itemData)
        }
        alertDialog.setNegativeButton(android.R.string.cancel, null)
        alertDialog.show()
    }

    @SuppressLint("SetTextI18n")
    override fun modifyRespond(itemData: DataForm) {
        val view = layoutInflater.inflate(R.layout.dialog_save,null)
        val dialog = setBaseDialog(view,this,"ModifyName")
        val duration = MyUtils.timeCalculate(itemData.data.size / 10)
        view.textView_ActivityDuration.text =
            getString(R.string.active_duration) + " " + duration
        view.textView_MaxDialogTag.text =
            getString(R.string.max_sound_intensity) + " " + itemData.max
        view.textView_MinDialogTag.text =
            getString(R.string.min_sound_intensity) + " " + itemData.min
        view.textView_AvgDialogTag.text =
            getString(R.string.average_sound_intensity) + " " + itemData.avg
        view.textView_SaveTime.text =
            "${getString(R.string.save_time)} ${itemData.date} ${itemData.time}"
        view.editText_Name.setText(itemData.name)
        view.button_Ok.setOnClickListener {
            val newName = view.editText_Name.text.toString()
            if (newName.isEmpty()) {
                showToast(this, getString(R.string.dialog_name_setting))
                return@setOnClickListener
            }
            itemData.name = newName
            viewModel.updateData(itemData)
            dialog.dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        doAsync {
            val data = mAdapter.getData()
            for (i in 0 until data.size) {
                RoomDateBase.getInstance(this@HistoryRecord)!!
                    .getUao().updateOrders(data[i].id, i)
            }
        }
    }
}