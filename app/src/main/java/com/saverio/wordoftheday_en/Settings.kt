package com.saverio.wordoftheday_en

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val adsSwitch: Switch = findViewById(R.id.settingsAdsSwitch)
        adsSwitch.setOnClickListener {
            getSharedPreferences("ads", Context.MODE_PRIVATE).edit()
                .putBoolean("ads", adsSwitch.isChecked).apply()

            setSettingsChanged()
            checkSettingsChanged()
        }
        adsSwitch.isChecked = getAds()

        val pushNotificationsSwitch: Switch = findViewById(R.id.settingsNotificationSwitch)
        pushNotificationsSwitch.setOnClickListener {
            getSharedPreferences("pushNotifications", Context.MODE_PRIVATE).edit()
                .putBoolean("pushNotifications", pushNotificationsSwitch.isChecked).apply()
        }
        pushNotificationsSwitch.isChecked = getPushNotifications()

        checkSettingsChanged()

        loadButtons()
    }

    fun loadButtons() {
        val backButton: ImageView = findViewById(R.id.backSettings)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    fun getAds(): Boolean {
        return getSharedPreferences("ads", Context.MODE_PRIVATE).getBoolean("ads", true)
    }

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