package com.saverio.wordoftheday_en

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Add a unique index on (word, language) to avoid duplicate entries at the DB level
@Entity(tableName = "word_history", indices = [Index(value = ["word", "language"], unique = true)])
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
