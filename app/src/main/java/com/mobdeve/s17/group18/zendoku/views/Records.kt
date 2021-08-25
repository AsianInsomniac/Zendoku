package com.mobdeve.s17.group18.zendoku.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.mobdeve.s17.group18.zendoku.R
import com.mobdeve.s17.group18.zendoku.util.StoragePreferences

class Records : AppCompatActivity() {
    private var strDiff: String ?= null
    private var tvDiff: TextView ?= null
    private var tvSkipRecVal: TextView ?= null
    private var tvClearRecVal: TextView ?= null
    private var sPref: StoragePreferences ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)

        sPref = StoragePreferences(applicationContext)

        tvDiff = findViewById<TextView>(R.id.tvDiff)
        tvSkipRecVal = findViewById<TextView>(R.id.tvSkipRecVal)
        tvClearRecVal = findViewById<TextView>(R.id.tvClearRecVal)
        strDiff = sPref!!.getStringPreferences("ZENDOKU_DIFF")
        tvDiff!!.setText(strDiff)
    }

    fun toHome(view: View) {
        finish()
    }

    fun diffUp(view: View) {
        when(strDiff) {
            "Easy" -> strDiff = "Med"
            "Med" -> strDiff = "Hard"
            "Hard" -> strDiff = "Evil"
        }
        tvDiff!!.setText(strDiff)
    }

    fun diffDown(view: View) {
        when(strDiff) {
            "Med" -> strDiff = "Easy"
            "Hard" -> strDiff = "Med"
            "Evil" -> strDiff = "Hard"
        }
        tvDiff!!.setText(strDiff)
    }

    /*
    *** To be implemented when database is implemented ***

    private fun updateRec() {
        *** Updates records when difficulty is changed ***
    }
     */
}