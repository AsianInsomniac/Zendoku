package com.mobdeve.s17.group18.zendoku.views

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.mobdeve.s17.group18.zendoku.databinding.ActivityMainBinding
import com.mobdeve.s17.group18.zendoku.util.StoragePreferences
import java.lang.IllegalStateException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var sPref: StoragePreferences ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun toStart(view: View) {
        sPref = StoragePreferences(applicationContext)
        val prevDiff = sPref!!.getStringPreferences("ZENDOKU_GRID_DIFF") // Gets previously-saved difficulty setting from sharedPreferences
        val currDiff = sPref!!.getStringPreferences("ZENDOKU_DIFF") // Gets current difficulty setting from sharedPreferences

        Log.i("init", sPref!!.getStringPreferences("ZENDOKU_GRID").toString())
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

        if(prevDiff != currDiff) {
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
}