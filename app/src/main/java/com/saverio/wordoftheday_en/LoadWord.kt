package com.saverio.wordoftheday_en

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LoadWord {
    lateinit var mainActivity: MainActivity
    lateinit var notificationReceiver: NotificationReceiver

    val app_url = "word-of-the-day"

    fun loadWord(
        context: Context,
        attempts: Int,
        maxAttempts: Int,
        pattern: String,
        mainActivity: MainActivity? = null,
        notificationReceiver: NotificationReceiver? = null
    ): Int {
        if (mainActivity != null) {
            this.mainActivity = mainActivity
        }

        var returnCode = 0
        var attempsTemp = attempts

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://www.saveriomorelli.com/api/$app_url/v1/")
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

                        if (model != null && model.date != "null") {

                            setDataOffline(
                                context,
                                "date",
                                getTheCorrectFormatDate(context, model.date, pattern)
                            )
                            setDataOffline(context, "word", model.word)
                            setDataOffline(context, "definition", model.definition)
                            setDataOffline(context, "type", model.word_type)
                            setDataOffline(context, "phonetics", model.phonetics)
                            setDataOffline(context, "etymology", model.etymology)
                            setDataOffline(context, "source", model.source)

                            if (mainActivity != null) {
                                mainActivity.hideMessage()
                                mainActivity.setAllFields(
                                    model.date,
                                    model.word,
                                    model.definition,
                                    model.phonetics,
                                    model.word_type,
                                    model.etymology,
                                    model.source
                                )
                            }

                            if (notificationReceiver != null) {
                                //get notification number
                                var notificationNumber = context.getSharedPreferences(
                                    "notificationNumber",
                                    Context.MODE_PRIVATE
                                ).getInt("notificationNumber", 0)

                                //send the push notification
                                notificationReceiver.sendNow(
                                    model.word,
                                    context.getString(R.string.open_the_app_to_learn_more),
                                    notificationNumber
                                )
                            }
                        } else if (model != null && model.date == "null") {
                            //no word
                            if (attempsTemp <= maxAttempts) {
                                loadWord(
                                    context,
                                    attempsTemp + 1,
                                    maxAttempts,
                                    pattern,
                                    mainActivity
                                )
                            } else {
                                if (mainActivity != null) {
                                    noWordOfTheDayMessage()
                                }
                            }
                        } else {
                            //null
                            if (attempsTemp <= maxAttempts) {
                                loadWord(
                                    context,
                                    attempsTemp + 1,
                                    maxAttempts,
                                    pattern,
                                    mainActivity
                                )
                            } else {
                                if (mainActivity != null) {
                                    errorNullMessage()
                                }
                            }
                        }
                    }
                })
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
            returnCode = -1
        }

        return returnCode
    }

    fun setDataOffline(context: Context, data: String, value: String) {
        context.getSharedPreferences(data, Context.MODE_PRIVATE).edit().putString(data, value)
            .apply()
    }

    @SuppressLint("SimpleDateFormat")
    fun getTheCorrectFormatDate(context: Context, date: String, pattern: String): String {
        val newDate: String =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate.parse(date, DateTimeFormatter.ISO_DATE)
                    .format(DateTimeFormatter.ofPattern(pattern))
            } else {
                SimpleDateFormat(pattern).format(SimpleDateFormat("yyyy-MM-dd").parse(date)!!)
            }

        return newDate
    }

    fun noWordOfTheDayMessage() {
        mainActivity.noWordOfTheDayMessage()
    }

    fun errorNullMessage() {
        mainActivity.errorNullMessage()
    }

    fun loadingWordMessage() {
        mainActivity.loadingWordMessage()
    }
}