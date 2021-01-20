package com.saverio.wordoftheday_en

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        loadButtons()
    }

    fun loadButtons() {
        val backButton: ImageView = findViewById(R.id.backSettings)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}