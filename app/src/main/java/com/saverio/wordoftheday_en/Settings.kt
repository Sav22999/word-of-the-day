package com.saverio.wordoftheday_en

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone

class Settings : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        /*
        val adsSwitch: Switch = findViewById(R.id.settingsAdsSwitch)
        adsSwitch.setOnClickListener {
            getSharedPreferences("ads", Context.MODE_PRIVATE).edit()
                .putBoolean("ads", adsSwitch.isChecked).apply()
            setSettingsChanged()
            checkSettingsChanged()
        }
        val settingsAdsContainer: ConstraintLayout =
            findViewById(R.id.settingsAdsContainer)
        settingsAdsContainer.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                adsSwitch.isChecked = !adsSwitch.isChecked
                getSharedPreferences("ads", Context.MODE_PRIVATE).edit()
                    .putBoolean("ads", adsSwitch.isChecked).apply()
                setSettingsChanged()
                checkSettingsChanged()
            }
            true
        }
        adsSwitch.isChecked = getAds()
        */

        val pushNotificationsSwitch: Switch = findViewById(R.id.settingsNotificationSwitch)
        pushNotificationsSwitch.setOnClickListener {
            getSharedPreferences("pushNotifications", Context.MODE_PRIVATE).edit()
                .putBoolean("pushNotifications", pushNotificationsSwitch.isChecked).apply()
            setSettingsChanged()
            checkSettingsChanged()
        }
        val settingsNotificationContainer: ConstraintLayout =
            findViewById(R.id.settingsNotificationContainer)
        settingsNotificationContainer.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                pushNotificationsSwitch.isChecked = !pushNotificationsSwitch.isChecked
                getSharedPreferences("pushNotifications", Context.MODE_PRIVATE).edit()
                    .putBoolean("pushNotifications", pushNotificationsSwitch.isChecked).apply()
            }
            setSettingsChanged()
            checkSettingsChanged()
            true
        }
        pushNotificationsSwitch.isChecked = getPushNotifications()

        val releaseNumber: TextView = findViewById(R.id.settingsReleaseNumber)
        releaseNumber.text = BuildConfig.VERSION_NAME + " (build#" + BuildConfig.VERSION_CODE + ")"

        checkSettingsChanged()

        loadButtons()
    }

    fun loadButtons() {
        val backButton: ImageView = findViewById(R.id.backSettings)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    /*
    fun getAds(): Boolean {
        return getSharedPreferences("ads", Context.MODE_PRIVATE).getBoolean("ads", true)
    }
    */

    fun getPushNotifications(): Boolean {
        return getSharedPreferences(
            "pushNotifications",
            Context.MODE_PRIVATE
        ).getBoolean("pushNotifications", true)
    }

    fun setSettingsChanged() {
        getSharedPreferences("settings_changes", Context.MODE_PRIVATE).edit()
            .putBoolean("settings_changes", true).apply()
    }

    fun getSettingsChanged(): Boolean {
        return getSharedPreferences(
            "settings_changes",
            Context.MODE_PRIVATE
        ).getBoolean("settings_changes", false)
    }

    fun checkSettingsChanged() {
        val messageSettings: ConstraintLayout = findViewById(R.id.settingsMessageRestart)
        messageSettings.isGone = !getSettingsChanged()
    }
}