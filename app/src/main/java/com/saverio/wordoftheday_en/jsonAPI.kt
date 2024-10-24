package com.saverio.wordoftheday_fr

import retrofit2.Call
import retrofit2.http.GET

interface jsonAPI {
    //https://www.saveriomorelli.com/api/word-of-the-day/v1/en/
    @GET("fr")
    fun getInfo(): Call<Model>
}