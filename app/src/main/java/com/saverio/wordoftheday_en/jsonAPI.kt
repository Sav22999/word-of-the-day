package com.saverio.wordoftheday_en

import retrofit2.Call
import retrofit2.http.GET

interface jsonAPI {
    //https://www.saveriomorelli.com/api/word-of-the-day/en/
    @GET("en")
    fun getInfo(): Call<Model>
}