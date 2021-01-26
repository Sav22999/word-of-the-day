package com.saverio.wordoftheday_en

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import androidx.lifecycle.Observer
import com.google.android.gms.ads.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    var connected = false
    val pattern = "dd/MM/yyyy"

    var attempts = 0
    val maxAttempts = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkNetwork()

        setSettingsNotChanged()

        loadWord()
        checkAds()

        val buttonCheckAgain: Button = findViewById(R.id.checkAgainButton)
        buttonCheckAgain.setOnClickListener {
            buttonCheckAgain.isGone = true
            attempts = 0
            loadWord()
        }
    }

    fun loadWord() {
        if (isConnected()) {
            attempts++

            loadingWordMessage()
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://www.saveriomorelli.com/api/word-of-the-day/v1/")
                .build()

            val jsonAPI = retrofit.create(jsonAPI::class.java)
            val mcall: Call<Model> = jsonAPI.getInfo()
            try {
                mcall.enqueue(
                    object : Callback<Model> {
                        override fun onFailure(call: Call<Model>, t: Throwable) {
                            Log.e("Error", t.message.toString())
                        }

                        override fun onResponse(
                            call: Call<Model>,
                            response: Response<Model>
                        ) {
                            hideMessage()

                            val model: Model? = response.body()

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

                            if (model != null && model.date != "null") {

                                setDataOffline("date", getTheCorrectFormatDate(model.date))
                                setDataOffline("word", model.word)
                                setDataOffline("definition", model.definition)
                                setDataOffline("type", model.word_type)
                                setDataOffline("phonetics", model.phonetics)
                                setDataOffline("etymology", model.etymology)
                                setDataOffline("source", model.source)

                                dateElement.text = getTheCorrectFormatDate(model.date)
                                wordElement.text = model.word
                                definitionElement.text = model.definition

                                if (model.definition != "") {
                                    definitionElement.text = model.definition
                                    definitionElement.isGone = false
                                    definitionTitle.isGone = false
                                }

                                if (model.word_type != "" && model.phonetics != "") {
                                    separator.isGone = false
                                }
                                typeElement.text = model.word_type
                                if (model.phonetics != "") "/${model.phonetics}/".also {
                                    phoneticsElement.text = it
                                }

                                if (model.etymology != "") {
                                    etymologyElement.text = model.etymology
                                    etymologyElement.isGone = false
                                    etymologyTitle.isGone = false
                                }

                                if (model.source != "") {
                                    sourceElement.isGone = false
                                    sourceElement.text = getString(R.string.source_from).replace(
                                        "{{*{{source}}*}}",
                                        model.source
                                    )
                                }

                                loadButtons()
                            } else if (model != null && model.date == "null") {
                                //no word
                                if (attempts <= maxAttempts) {
                                    loadWord()
                                } else {
                                    noWordOfTheDayMessage()
                                }
                            } else {
                                //null
                                if (attempts <= maxAttempts) {
                                    loadWord()
                                } else {
                                    errorNullMessage()
                                }
                            }
                        }
                    })
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
            }
        } else {
            loadDataOffline()
        }
    }

    fun loadButtons() {
        val word = findViewById<TextView>(R.id.wordElement).text

        val copyButton: ImageView = findViewById(R.id.copyButton)
        copyButton.setOnClickListener {
            copyText(word.toString())
            Toast.makeText(this, getString(R.string.word_copied), Toast.LENGTH_SHORT).show()
        }
        copyButton.isGone = false

        val shareButton: ImageView = findViewById(R.id.shareButton)
        shareButton.setOnClickListener {
            shareText(
                getString(R.string.the_word_of_the_day_is).replace(
                    "{{*{{word}}*}}",
                    word.toString()
                )
                    .replace("{{*{{link}}*}}", "https://bit.ly/2XWj2Mb")
            )
        }
        shareButton.isGone = false

        val settingsButton: ImageView = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this, Settings::class.java).apply {}
            startActivity(intent)
        }
        settingsButton.isGone = false
    }

    fun copyText(textToCopy: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data = ClipData.newPlainText("", textToCopy)
        clipboard.setPrimaryClip(data)
    }

    fun shareText(textToShare: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "type/palin"

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, textToShare)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_text)))
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

    fun checkNetwork() {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this, Observer { isConnected ->
            connected = isConnected
            hideMessage()
            if (isConnected) {
                loadWord()
                checkAds()
            } else {
                loadDataOffline()
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
        val settingsButton: ImageView = findViewById(R.id.settingsButton)
        settingsButton.isGone = true

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
    }

    @SuppressLint("SimpleDateFormat")
    fun showCurrentDate() {
        val sdf = SimpleDateFormat(pattern)
        val currentDate = sdf.format(Date())
        setTextView(findViewById(R.id.dateElement), "date", currentDate.toString(), save = false)
    }
}