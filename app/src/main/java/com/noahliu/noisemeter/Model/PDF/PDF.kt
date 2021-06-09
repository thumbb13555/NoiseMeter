package com.noahliu.noisemeter.Model.PDF

import com.noahliu.noisemeter.Model.FileExport.FileExport
import com.noahliu.noisemeter.Model.RoomDatabase.DataForm

class PDF() : FileExport() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg p0: DataForm?): String {

        return ""
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
    }
}