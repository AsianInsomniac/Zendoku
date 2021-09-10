package com.mobdeve.s17.group18.zendoku.views

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mobdeve.s17.group18.zendoku.R
import com.mobdeve.s17.group18.zendoku.databinding.ActivityMainBinding
import com.mobdeve.s17.group18.zendoku.util.StoragePreferences

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var sPref: StoragePreferences ?= null
    companion object {
        var mediaPlayer: MediaPlayer ?= null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sPref = StoragePreferences(applicationContext)
        var nBGMVol = sPref!!.getIntPreferences("ZENDOKU_BGM")
        if(nBGMVol == -1) nBGMVol = 10
        /*var nSFXVol = sPref!!.getIntPreferences("ZENDOKU_SFX")
        if(nSFXVol == -1) nSFXVol = 10*/

        mediaPlayer = MediaPlayer.create(this, R.raw.gametheory)
        Settings.setVol(nBGMVol)
        mediaPlayer?.isLooping = true
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    fun toStart(view: View) {
        val prevDiff = sPref!!.getStringPreferences("ZENDOKU_GRID_DIFF") // Gets previously-saved difficulty setting from sharedPreferences
        val currDiff = sPref!!.getStringPreferences("ZENDOKU_DIFF") // Gets current difficulty setting from sharedPreferences

        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val intent = Intent(this, StartSudokuActivity::class.java)
                    startActivity(intent)
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    null
                }
            }
        }
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Overwrite Current Board?")

        if(prevDiff != currDiff && prevDiff != "") {
            builder.setMessage("Previous Board Difficulty: $prevDiff\nCurrent Board Difficulty: $currDiff\n\nStarting a new board with the current difficulty setting.\nAre you sure you want to proceed?")
                .setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show()
        }

        else {
            val intent = Intent(this, StartSudokuActivity::class.java)
            startActivity(intent)
        }
    }

    fun toSettings(view: View) {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
    }

    fun toRecords(view: View) {
        val intent = Intent(this, Records::class.java)
        startActivity(intent)
    }

    fun toCredits(view: View) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle("Credits")
            .setMessage("Backgrounds (Unsplash):\n" +
                        "\"Balance\" by Bekir Dönmez\n" +
                        "\"Pebble Tower\" by Jeremy Thomas\n" +
                        "\"Peaceful Garden\" by Sarah Ball\n" +
                        "\"Pristine Water Lily\" by Jay Castor\n\n" +
                        "Music:\n\"Game Theory\" by Masayoshi Soken")
        builder.show()
    }
}