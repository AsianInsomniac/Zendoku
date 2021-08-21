package com.mobdeve.s17.group18.zendoku

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s17.group18.zendoku.util.StoragePreferences

class StartSudoku : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startsudoku)
    }

    fun toHome(view: View) {
        finish()
    }
}