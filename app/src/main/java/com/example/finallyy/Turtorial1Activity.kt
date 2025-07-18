package com.example.finallyy

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class Turtorial1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.turtorial_1)

        val nextBtn = findViewById<ImageButton>(R.id.imageButtonNext)
        nextBtn.setOnClickListener {
            val intent = Intent(this, Turtorial2Activity::class.java)
            startActivity(intent)
        }
    }
}
//debugged1