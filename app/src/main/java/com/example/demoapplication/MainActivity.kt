package com.example.demoapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * Main Activity simply allowing for demo to be started
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStartDemo = findViewById<Button>(R.id.btn_start_demo)
        btnStartDemo.setOnClickListener {
            startActivity(Intent(this, RecordingActivity::class.java))
        }

    }
}
