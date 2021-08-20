package com.mobdeve.s17.group18.zendoku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.mobdeve.s17.group18.zendoku.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
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