package com.saverio.wordoftheday_en

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_history")
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val word: String,
    val definition: String,
    val wordType: String,
    val phonetics: String,
    val etymology: String,
    val source: String,
    val language: String
)
