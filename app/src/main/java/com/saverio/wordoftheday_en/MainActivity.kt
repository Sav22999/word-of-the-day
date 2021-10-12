package com.saverio.wordoftheday_en

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import com.saverio.wordoftheday_en.BuildConfig.SOURCE_STORE
import com.saverio.wordoftheday_en.MainActivity.Companion.SOURCE_STORE
//import com.google.android.gms.ads.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    var connected = false
    val pattern = "dd/MM/yyyy"

    var attempts = 0
    val maxAttempts = 5

    companion object {
        const val SOURCE_STORE: String = BuildConfig.SOURCE_STORE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkNetwork()

        setSettingsNotChanged()

        loadWord()
        //checkAds()
        setNotification()

        val buttonCheckAgain: Button = findViewById(R.id.checkAgainButton)
        buttonCheckAgain.setOnClickListener {
            buttonCheckAgain.isGone = true
            attempts = 0
            loadWord()
        }

        val settingsButton: ImageView = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this, Settings::class.java).apply {}
            startActivity(intent)
        }
        settingsButton.isGone = false
    }

    fun loadWord() {
        if (isConnected()) {
            loadingWordMessage()
            var dailyWord: LoadWord = LoadWord()
            if (dailyWord.loadWord(
                    this,
                    attempts,
                    maxAttempts,
                    pattern,
                    mainActivity = this
                ) == -1
            ) {
                errorNullMessage()
            }
            val c = Calendar.getInstance()
            val currentDate =
                "${c.get(Calendar.YEAR)}-${c.get(Calendar.MONTH + 1)}-${c.get(Calendar.DAY_OF_MONTH)}"
            setNotificationDate(this, currentDate)
        } else {
            loadDataOffline()
        }
    }

    fun loadButtons() {
        val word = findViewById<TextView>(R.id.wordElement).text

        val copyButton: ImageView = findViewById(R.id.copyButton)
        copyButton.setOnClickListener {
            copyText(word.toString())
            val messageContainer: ConstraintLayout = findViewById(R.id.dialogMessageTop)
            val messageText: TextView = findViewById(R.id.dialogMessageTopText)
            messageText.text = getString(R.string.word_copied)
            messageContainer.isGone = false
            Handler().postDelayed({
                messageContainer.isGone = true
                messageText.text = ""
            }, 2000)
        }
        copyButton.isGone = false

        var url = "https://play.google.com/store/apps/details?id=com.saverio.wordoftheday_en"
        if (SOURCE_STORE == "FD-GH") {
            url = "https://f-droid.org/packages/com.saverio.wordoftheday_en"
        }

        val shareButton: ImageView = findViewById(R.id.shareButton)
        shareButton.setOnClickListener {
            shareText(
                getString(R.string.the_word_of_the_day_is).replace(
                    "{{*{{word}}*}}",
                    word.toString()
                )
                    .replace("{{*{{link}}*}}", url)
            )
        }
        shareButton.isGone = false
    }

    fun copyText(textToCopy: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data = ClipData.newPlainText("", textToCopy)
        clipboard.setPrimaryClip(data)
    }

    fun shareText(textToShare: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare)
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_text)))
    }

    /*
    fun loadAds() {
        try {
            MobileAds.initialize(this) {}
            val adRequest = AdRequest.Builder().build()
            val adView: AdView = findViewById(R.id.adView)
            adView.loadAd(adRequest)

            adView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    adView.isGone = false
                    super.onAdLoaded()
                }

                override fun onAdFailedToLoad(p0: LoadAdError?) {
                    Log.e("Error", p0?.message.toString())
                    super.onAdFailedToLoad(p0)
                }
            }


        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
        }
    }

    fun getAds(): Boolean {
        return getSharedPreferences("ads", Context.MODE_PRIVATE).getBoolean("ads", true)
    }

    fun checkAds() {
        if (getAds()) loadAds()
    }
    */

    fun getPushNotifications(): Boolean {
        return getSharedPreferences(
            "pushNotifications",
            Context.MODE_PRIVATE
        ).getBoolean("pushNotifications", true)
    }

    fun checkNetwork() {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->
            connected = isConnected
            hideMessage()
            if (isConnected) {
                loadWord()
                //checkAds()
            } else {
                loadDataOffline()
                val offlineModeMessage = findViewById<ConstraintLayout>(R.id.dialogOfflineMode)
                offlineModeMessage.isGone = false
            }
        })
    }

    fun setSettingsNotChanged() {
        getSharedPreferences("settings_changes", Context.MODE_PRIVATE).edit()
            .putBoolean("settings_changes", false).apply()
    }

    fun isConnected(): Boolean {
        return connected
    }

    fun setDataOffline(data: String, value: String) {
        getSharedPreferences(data, Context.MODE_PRIVATE).edit().putString(data, value).apply()
    }

    fun getDataOffline(data: String): String {
        return getSharedPreferences(data, Context.MODE_PRIVATE).getString(data, "")!!
    }

    @SuppressLint("SimpleDateFormat")
    fun loadDataOffline() {
        val sdf = SimpleDateFormat(pattern)
        val currentDate = sdf.format(Date())
        if (!isConnected() || getDataOffline("date") == currentDate) {
            if (getDataOffline("date") == currentDate) {
                //restore data saved
                setTextView(
                    findViewById(R.id.dateElement),
                    "date",
                    getDataOffline("date"),
                    save = false
                )
                setTextView(
                    findViewById(R.id.wordElement),
                    "word",
                    getDataOffline("word"),
                    save = false
                )
                setTextView(
                    findViewById(R.id.definitionElement),
                    "definition",
                    getDataOffline("definition"),
                    save = false
                )
                setTextView(
                    findViewById(R.id.typeElement),
                    "type",
                    getDataOffline("type"),
                    save = false
                )
                setTextView(
                    findViewById(R.id.phoneticsElement),
                    "phonetics",
                    getDataOffline("phonetics"),
                    save = false
                )
                setTextView(
                    findViewById(R.id.etymologyElement),
                    "etymology",
                    getDataOffline("etymology"),
                    save = false
                )
                setTextView(
                    findViewById(R.id.sourceElement),
                    "source",
                    getDataOffline("source"),
                    save = false
                )

                val separator: TextView = findViewById(R.id.separator)
                if (getDataOffline("type") != "" && getDataOffline("phonetics") != "") {
                    separator.isGone = false
                }
            } else {
                //internet isn't available and there aren't data saved
                resetDataOffline()

                noInternetConnectionMessage()
            }
        } else {
            loadWord()
        }
    }

    fun setAllFields(
        date: String,
        word: String,
        definition: String,
        phonetics: String,
        word_type: String,
        etymology: String,
        source: String
    ) {
        val dateElement: TextView = findViewById(R.id.dateElement)
        val wordElement: TextView = findViewById(R.id.wordElement)

        val definitionTitle: TextView = findViewById(R.id.titleDefinition)
        val definitionElement: TextView = findViewById(R.id.definitionElement)

        val separator: TextView = findViewById(R.id.separator)
        val typeElement: TextView = findViewById(R.id.typeElement)
        val phoneticsElement: TextView = findViewById(R.id.phoneticsElement)

        val etymologyTitle: TextView = findViewById(R.id.titleEtymology)
        val etymologyElement: TextView = findViewById(R.id.etymologyElement)

        val sourceElement: TextView = findViewById(R.id.sourceElement)

        dateElement.text = getTheCorrectFormatDate(date)
        wordElement.text = word
        definitionElement.text = definition

        if (definition != "") {
            definitionElement.text = definition
            definitionElement.isGone = false
            definitionTitle.isGone = false
        }

        if (word_type != "" && phonetics != "") {
            separator.isGone = false
        }
        typeElement.text = word_type
        if (phonetics != "") "/${phonetics}/".also {
            phoneticsElement.text = it
        }

        if (etymology != "") {
            etymologyElement.text = etymology
            etymologyElement.isGone = false
            etymologyTitle.isGone = false
        }

        if (source != "") {
            sourceElement.isGone = false
            sourceElement.text = getString(R.string.source_from).replace(
                "{{*{{source}}*}}",
                source
            )
        }

        loadButtons()
        setNotification()
    }

    fun resetDataOffline() {
        setTextView(findViewById(R.id.dateElement), "date", "")
        setTextView(findViewById(R.id.wordElement), "word", "")
        setTextView(findViewById(R.id.definitionElement), "definition", "")
        setTextView(findViewById(R.id.typeElement), "type", "")
        setTextView(findViewById(R.id.phoneticsElement), "phonetics", "")
        setTextView(findViewById(R.id.etymologyElement), "etymology", "")
        setTextView(findViewById(R.id.sourceElement), "source", "")

        val separator: TextView = findViewById(R.id.separator)
        if (getDataOffline("type") != "" && getDataOffline("phonetics") != "") {
            separator.isGone = false
        } else {
            separator.isGone = true
        }
    }

    fun hideAllElements() {
        val copyButton: ImageView = findViewById(R.id.copyButton)
        copyButton.isGone = true
        val shareButton: ImageView = findViewById(R.id.shareButton)
        shareButton.isGone = true

        setTextView(findViewById(R.id.dateElement), "date", "", false)
        setTextView(findViewById(R.id.wordElement), "word", "", false)
        setTextView(findViewById(R.id.definitionElement), "definition", "", false)
        setTextView(findViewById(R.id.typeElement), "type", "", false)
        setTextView(findViewById(R.id.phoneticsElement), "phonetics", "", false)
        setTextView(findViewById(R.id.etymologyElement), "etymology", "", false)
        setTextView(findViewById(R.id.sourceElement), "source", "", false)

        val separator: TextView = findViewById(R.id.separator)
        separator.isGone = true
    }

    fun setTextView(textView: TextView, data: String, value: String, save: Boolean = true) {
        //if "save" is "true", the function saves also to sharedPreferences
        if (save) setDataOffline(data, value)

        if (data == "phonetics" && value != "") "/${value}/".also { textView.text = it }
        else textView.text = value


        val definitionTitle: TextView = findViewById(R.id.titleDefinition)
        if (data == "definition" && value != "") {
            textView.isGone = false
            definitionTitle.isGone = false
        } else if (data == "definition") {
            textView.isGone = true
            definitionTitle.isGone = true
        }

        val etymologyTitle: TextView = findViewById(R.id.titleEtymology)
        if (data == "etymology" && value != "") {
            textView.isGone = false
            etymologyTitle.isGone = false
        } else if (data == "etymology") {
            textView.isGone = true
            etymologyTitle.isGone = true
        }

        val sourceElement: TextView = findViewById(R.id.sourceElement)
        if (data == "source" && value != "") {
            sourceElement.isGone = false
            sourceElement.text = getString(R.string.source_from).replace("{{*{{source}}*}}", value)
        }
    }

    fun noInternetConnectionMessage() {
        val messageMainText: TextView = findViewById(R.id.messageMainText)
        messageMainText.text = getString(R.string.internet_not_available)

        hideAllElements()
        showMessage()
    }

    fun noWordOfTheDayMessage() {
        val messageMainText: TextView = findViewById(R.id.messageMainText)
        messageMainText.text = getString(R.string.no_daily_word_try_later)

        val buttonCheckAgain: Button = findViewById(R.id.checkAgainButton)
        buttonCheckAgain.isGone = false

        hideAllElements()
        showMessage()
    }

    fun errorNullMessage() {
        val messageMainText: TextView = findViewById(R.id.messageMainText)
        messageMainText.text = getString(R.string.error_try_later)

        val buttonCheckAgain: Button = findViewById(R.id.checkAgainButton)
        buttonCheckAgain.isGone = false

        hideAllElements()
        showMessage()
    }

    fun loadingWordMessage() {
        val messageMainText: TextView = findViewById(R.id.messageMainText)
        messageMainText.text = getString(R.string.loading_word)

        val buttonCheckAgain: Button = findViewById(R.id.checkAgainButton)
        buttonCheckAgain.isGone = true

        hideAllElements()
        showMessage()
    }

    fun showMessage() {
        val messageMain: ConstraintLayout = findViewById(R.id.messageMainContainer)
        messageMain.isGone = false

        showCurrentDate()
    }

    fun hideMessage() {
        val messageMain: ConstraintLayout = findViewById(R.id.messageMainContainer)
        messageMain.isGone = true

        val buttonCheckAgain: Button = findViewById(R.id.checkAgainButton)
        buttonCheckAgain.isGone = true

        val offlineModeMessage = findViewById<ConstraintLayout>(R.id.dialogOfflineMode)
        offlineModeMessage.isGone = true
    }

    @SuppressLint("SimpleDateFormat")
    fun showCurrentDate() {
        val sdf = SimpleDateFormat(pattern)
        val currentDate = sdf.format(Date())
        setTextView(findViewById(R.id.dateElement), "date", currentDate.toString(), save = false)
    }

    @SuppressLint("SimpleDateFormat")
    fun setNotification() {
        if (getPushNotifications()) {
            val sdf = SimpleDateFormat(pattern)
            val currentDate = sdf.format(Date())
            if (getDataOffline("date") == currentDate) {
                checkNotification(0, 0, 1)
            } else {
                loadWord()
            }
        }
    }

    fun checkNotification(hour: Int, minute: Int, second: Int) {
        try {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, second)

            val notificationIntent = Intent(this, NotificationReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                100,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                10000,
                pendingIntent
            )
        } catch (e: Exception) {
            //Exception
        }
    }

    fun setNotificationDate(context: Context, currentDate: String) {
        context.getSharedPreferences("lastNotificationDate", Context.MODE_PRIVATE).edit()
            .putString("lastNotificationDate", currentDate).apply()
    }

    @SuppressLint("SimpleDateFormat")
    fun getTheCorrectFormatDate(date: String): String {
        val newDate: String =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
                    .format(DateTimeFormatter.ofPattern(pattern))
            } else {
                SimpleDateFormat(pattern).format(SimpleDateFormat("yyyy-MM-dd").parse(date)!!)
            }

        return newDate
    }
}