package com.mobdeve.s17.group18.zendoku.views

import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mobdeve.s17.group18.zendoku.R
import com.mobdeve.s17.group18.zendoku.databinding.ActivityMainBinding
import com.mobdeve.s17.group18.zendoku.util.MediaPlayerService
import com.mobdeve.s17.group18.zendoku.util.StoragePreferences

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var sPref: StoragePreferences ?= null
    private var isBound = false
    private var mediaPlayer: MediaPlayerService ?= null
    private lateinit var imageView : ImageView

    private var mConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mediaPlayer = (service as MediaPlayerService.LocalBinder).getMPInstance()
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            mediaPlayer = null
        }
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

        var intent = Intent(applicationContext, MediaPlayerService::class.java)
        startService(intent)

        imageView = findViewById(R.id.bg1)
        Glide.with(this)
            .load(R.drawable.bg1)
            .into(imageView)
    }

    override fun onDestroy() {
        super.onDestroy()
        var intent = Intent(applicationContext, MediaPlayerService::class.java)
        stopService(intent)
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
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun toRecords(view: View) {
        val intent = Intent(this, Records::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun toCredits(view: View) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.credits_popup)
        dialog.show()
    }
}