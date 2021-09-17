package com.mobdeve.s17.group18.zendoku.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.mobdeve.s17.group18.zendoku.R
import com.mobdeve.s17.group18.zendoku.databinding.ActivityRecordsBinding
import com.mobdeve.s17.group18.zendoku.util.StoragePreferences

class Records : AppCompatActivity() {
    private lateinit var binding: ActivityRecordsBinding
    private var strDiff: String ?= null
    private var tvDiff: TextView ?= null
    private var tvSkipRecVal: TextView ?= null
    private var tvClearRecVal: TextView ?= null
    private var sPref: StoragePreferences ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sPref = StoragePreferences(applicationContext)

        tvDiff = findViewById(R.id.tvDiff)
        tvSkipRecVal = findViewById(R.id.tvSkipRecVal)
        tvClearRecVal = findViewById(R.id.tvClearRecVal)
        strDiff = sPref!!.getStringPreferences("ZENDOKU_DIFF")
        if(strDiff == "") strDiff = "Med"
        tvDiff!!.text = strDiff
        updateRec()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
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
        tvDiff!!.text = strDiff
        updateRec()
    }

    fun diffDown(view: View) {
        when(strDiff) {
            "Med" -> strDiff = "Easy"
            "Hard" -> strDiff = "Med"
            "Evil" -> strDiff = "Hard"
        }
        tvDiff!!.text = strDiff
        updateRec()
    }


    private fun updateRec() {
        var nSkipUsed = 0
        var nClear = 0
        var strSkipUsedKey = "ZENDOKU_GRID_SKIPUSED_"
        var strClearKey = "ZENDOKU_GRID_CLEAR_"

        when(strDiff) {
            "Easy" -> {
                strSkipUsedKey += "EASY"
                strClearKey += "EASY"
            }
            "Med" -> {
                strSkipUsedKey += "MED"
                strClearKey += "MED"
            }
            "Hard" -> {
                strSkipUsedKey += "HARD"
                strClearKey += "HARD"
            }
            "Evil" -> {
                strSkipUsedKey += "EVIL"
                strClearKey += "EVIL"
            }
        }

        nSkipUsed = sPref?.getIntPreferences(strSkipUsedKey)!!.toInt()
        if(nSkipUsed == -1) nSkipUsed = 0
        nClear = sPref?.getIntPreferences(strClearKey)!!.toInt()
        if(nClear == -1) nClear = 0

        tvSkipRecVal!!.text = nSkipUsed.toString()
        tvClearRecVal!!.text = nClear.toString()
    }
}