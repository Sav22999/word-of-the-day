package com.saverio.wordoftheday_en

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface WordDao {
    @Insert
    suspend fun insertWord(word: Word) // Now a suspend function

    @Query("SELECT COUNT(*) FROM word_history WHERE word = :word")
    suspend fun countWord(word: String): Int // Method to count occurrences of a word

    @Query("SELECT * FROM word_history")
    suspend fun getAllWords(): List<Word> // Now a suspend function
}
