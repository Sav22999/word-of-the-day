package com.saverio.wordoftheday_en

import com.google.gson.annotations.SerializedName

class Model(
    @SerializedName("date")
    val date: String,

    @SerializedName("word")
    val word: String,

    @SerializedName("definition")
    val definition: String,

    @SerializedName("type")
    val word_type: String,

    @SerializedName("phonetics")
    val phonetics: String,

    @SerializedName("etymology")
    val etymology: String,

    @SerializedName("source")
    val source: String
)