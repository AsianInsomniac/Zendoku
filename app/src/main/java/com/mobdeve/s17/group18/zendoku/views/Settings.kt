package com.mobdeve.s17.group18.zendoku.views

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s17.group18.zendoku.R
import com.mobdeve.s17.group18.zendoku.databinding.ActivitySettingsBinding
import com.mobdeve.s17.group18.zendoku.util.StoragePreferences

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var strDiff: String ?= null
    private var nSkip: Int ?= null
    private var nBGMVol: Int ?= null
    private var nSFXVol: Int ?= null
    private var sPref: StoragePreferences?= null
    private var tvDiffCon: TextView ?= null
    private var tvSkipCon: TextView ?= null
    private var tvBGMCon: TextView ?= null
    private var tvSFXCon: TextView ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sPref = StoragePreferences(applicationContext)
        tvDiffCon = findViewById<TextView>(R.id.tvDiffCon)
        tvSkipCon = findViewById<TextView>(R.id.tvSkipCon)
        tvBGMCon = findViewById<TextView>(R.id.tvBGMCon)
        tvSFXCon = findViewById<TextView>(R.id.tvSFXCon)
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
        tvDiffCon!!.setText(strDiff)
    }

    fun diffDown(view: View) {
        when(strDiff) {
            "Med" -> strDiff = "Easy"
            "Hard" -> strDiff = "Med"
            "Evil" -> strDiff = "Hard"
        }
        tvDiffCon!!.setText(strDiff)
    }

    fun skipUp(view: View) {
        if(nSkip!! < 5) {
            nSkip = nSkip!! + 1
        }
        tvSkipCon!!.setText(nSkip.toString())

    }

    fun skipDown(view: View) {
        if(nSkip!! > 1) {
            nSkip = nSkip!! - 1
        }
        tvSkipCon!!.setText(nSkip.toString())
    }

    fun bgmUp(view: View) {
        if(nBGMVol!! < 10) {
            nBGMVol = nBGMVol!! + 1
            
        }
        tvBGMCon!!.setText(nBGMVol.toString())
    }

    fun bgmDown(view: View) {
        if(nBGMVol!! > 1) {
            nBGMVol = nBGMVol!! - 1
        }
        tvBGMCon!!.setText(nBGMVol.toString())
    }

    fun sfxUp(view: View) {
        if(nSFXVol!! < 10) {
            nSFXVol = nSFXVol!! + 1
        }
        tvSFXCon!!.setText(nSFXVol.toString())
    }

    fun sfxDown(view: View) {
        if(nSFXVol!! > 1) {
            nSFXVol = nSFXVol!! - 1
        }
        tvSFXCon!!.setText(nSFXVol.toString())
    }

    private fun setPrefs() {
        strDiff = sPref!!.getStringPreferences("ZENDOKU_DIFF")
        if(strDiff == "") strDiff = "Med"
        nSkip = sPref!!.getIntPreferences("ZENDOKU_SKIP")
        if(nSkip == -1) nSkip = 3
        nBGMVol = sPref!!.getIntPreferences("ZENDOKU_BGM")
        if(nBGMVol == -1) nBGMVol = 10
        nSFXVol = sPref!!.getIntPreferences("ZENDOKU_SFX")
        if(nSFXVol == -1) nSFXVol = 10

        tvDiffCon!!.setText(strDiff)
        tvSkipCon!!.setText(nSkip.toString())
        tvBGMCon!!.setText(nBGMVol.toString())
        tvSFXCon!!.setText(nSFXVol.toString())

    }

    private fun savePrefs() {
        sPref!!.saveStringPreferences("ZENDOKU_DIFF", strDiff)
        sPref!!.saveIntPreferences("ZENDOKU_SKIP", nSkip!!.toInt())
        sPref!!.saveIntPreferences("ZENDOKU_BGM", nBGMVol!!.toInt())
        sPref!!.saveIntPreferences("ZENDOKU_SFX", nSFXVol!!.toInt())
    }

    override fun onResume() {
        super.onResume()
        setPrefs()
    }

    override fun onPause() {
        super.onPause()
        savePrefs()
    }
}