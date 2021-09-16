package com.mobdeve.s17.group18.zendoku.util

import android.app.Service
import android.media.MediaPlayer
import android.content.Intent
import android.os.IBinder
import com.mobdeve.s17.group18.zendoku.R
import android.os.Binder
import android.provider.MediaStore

class MediaPlayerService: Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sPref: StoragePreferences
    private val mBinder: IBinder = LocalBinder()

    inner class LocalBinder: Binder() {
        fun getMPInstance(): MediaPlayerService? {
            return this@MediaPlayerService
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()
        sPref = StoragePreferences(applicationContext)
        mediaPlayer = MediaPlayer.create(this, R.raw.gametheory)
        mediaPlayer.isLooping = true
        var nBGMVol = sPref.getIntPreferences("ZENDOKU_BGM")
            if(nBGMVol == -1) nBGMVol = 10
        setVol(nBGMVol)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mediaPlayer.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    fun setVol(vol: Int) {
        mediaPlayer.setVolume((vol.toFloat() / 10), (vol.toFloat() / 10))
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun play() {
        mediaPlayer.start()
    }
}