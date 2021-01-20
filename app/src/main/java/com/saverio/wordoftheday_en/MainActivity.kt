package com.saverio.wordoftheday_en

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import android.R.string
import com.google.android.gms.ads.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadWord()
        loadAds()
    }

    fun loadWord() {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://www.saveriomorelli.com/api/word-of-the-day/")
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


                        if (model != null && model.date != "null") {
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
                            phoneticsElement.text = "/${model.phonetics}/"

                            if (model.etymology != "") {
                                etymologyElement.text = model.etymology
                                etymologyElement.isGone = false
                                etymologyTitle.isGone = false
                            }

                        } else if (model != null && model.date == "null") {
                            //no word
                            println("no word")
                        } else {
                            //null
                            println("null")
                        }

                        loadButtons()
                    }
                })
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
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

        val settingsButton: ImageView = findViewById(R.id.settingsSettings)
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

    fun getTheCorrectFormatDate(date: String): String {
        val patter = "dd/MM/yyyy"
        val newDate: String =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
                    .format(DateTimeFormatter.ofPattern(patter))
            } else {
                SimpleDateFormat(patter).format(SimpleDateFormat("yyyy-MM-dd").parse(date))
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
}