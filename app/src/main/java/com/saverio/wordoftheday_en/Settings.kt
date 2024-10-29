package com.saverio.wordoftheday_en

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.content.res.Configuration
import android.os.Build
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.multidex.BuildConfig
import com.saverio.wordoftheday_en.allLanguages
import java.util.Locale

class Settings : AppCompatActivity() {

    var ableToChangeLanguage = false

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

        val saverioMorelliImage: ImageView = findViewById(R.id.imageDeveloper)
        saverioMorelliImage.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://www.saveriomorelli.com/")
            startActivity(openURL)
        }
        val saverioMorelliText: TextView = findViewById(R.id.textSaverioMorelli)
        saverioMorelliImage.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://www.saveriomorelli.com/")
            startActivity(openURL)
        }

        val releaseNumber: TextView = findViewById(R.id.settingsReleaseNumber)
        releaseNumber.text = BuildConfig.VERSION_NAME + " (build#" + BuildConfig.VERSION_CODE + ")"

        checkSettingsChanged()

        loadButtons()

        loadLanguages()
    }

    fun loadButtons() {
        val backButton: ImageView = findViewById(R.id.backSettings)
        backButton.setOnClickListener {
            finish()
        }
    }

    fun loadLanguages() {
        this.ableToChangeLanguage = false
        // Initialize spinner and language data
        val languageContainer: Spinner = findViewById(R.id.settingsLanguageSpinner)
        val allLanguages = allLanguages()
        val languages = allLanguages.getLanguages()           // List of all languages
        val currentLanguage = getCurrentLanguage()             // Current language code
        val languageNames = allLanguages.getLanguageNames()    // List of all language names
        val languageName =
            allLanguages.getLanguageName(currentLanguage) // Display name of the current language
        val languageNameIndex =
            allLanguages.getIndexOfLanguage(currentLanguage) // Index of the current language

        // Set up the ArrayAdapter for the Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageContainer.adapter = adapter

        // Set Spinner to display current language
        if (languageNameIndex >= 0) {
            this.ableToChangeLanguage = true
            languageContainer.setSelection(languageNameIndex)
        } else {
            //println("Language name index not found")
            languageContainer.setSelection(0)
            this.ableToChangeLanguage = true
        }

        // Listen for spinner selection changes
        languageContainer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: android.view.View,
                position: Int,
                id: Long
            ) {
                if (ableToChangeLanguage) {
                    // Check if the language has changed
                    if (languages[position] != currentLanguage) {
                        setSettingsChanged()
                        checkSettingsChanged()

                        // Save selected language to SharedPreferences
                        val selectedLanguage = languages[position]
                        getSharedPreferences("language", Context.MODE_PRIVATE).edit()
                            .putString("language", selectedLanguage).apply()

                        // Set also the app language
                        setAppLocale(this@Settings, selectedLanguage)

                        // Set variable "restartRequiredMain" to true
                        setSettingsChanged()

                        // Restart the activity
                        finish()
                        startActivity(intent)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle case when nothing is selected if needed
            }
        }
    }

    fun setAppLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        // Update the configuration for the app's resources
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context.createConfigurationContext(config) // For API 24+
        } else {
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }

    fun getCurrentLanguage(): String {
        return getSharedPreferences("language", Context.MODE_PRIVATE).getString("language", "en")!!
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