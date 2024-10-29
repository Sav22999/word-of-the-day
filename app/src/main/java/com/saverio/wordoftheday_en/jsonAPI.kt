package com.saverio.wordoftheday_en

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface jsonAPI {
    //https://www.saveriomorelli.com/api/word-of-the-day/v1/<LANGUAGE>/
    @GET("{language}")
    fun getInfo(@Path("language") language: String): Call<Model>
}