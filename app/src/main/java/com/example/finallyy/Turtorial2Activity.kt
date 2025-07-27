package com.example.finallyy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class Turtorial2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.turtorial_2)

        val nextBtn = findViewById<ImageButton>(R.id.imageButtonNext)
        nextBtn.setOnClickListener {
            val intent = Intent(this, Turtorial3Activity::class.java)
            startActivity(intent)
        }
    }
}

