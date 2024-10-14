package com.saverio.wordoftheday_it

import retrofit2.Call
import retrofit2.http.GET

interface jsonAPI {
    //https://www.saveriomorelli.com/api/word-of-the-day/v1/it/
    @GET("it")
    fun getInfo(): Call<Model>
}