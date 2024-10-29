package com.saverio.wordoftheday_it

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.saverio.wordoftheday_it.R

class MigrateToTheNewApp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.switch_to_the_new_app)

        /*Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 400)*/

        val button = findViewById<Button>(R.id.downloadTheNewApp)
        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse("https://play.google.com/store/apps/details?id=com.saverio.wordoftheday_en")
            startActivity(intent)
        }
    }
}