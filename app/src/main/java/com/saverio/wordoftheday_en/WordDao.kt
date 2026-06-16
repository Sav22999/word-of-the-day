package com.saverio.wordoftheday_en

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordDao {
    // Use IGNORE to avoid inserting duplicates if the unique index detects a conflict
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWord(word: Word) // Now a suspend function

    @Query("SELECT COUNT(*) FROM word_history WHERE word = :word AND language = :language")
    suspend fun countWord(word: String, language: String): Int // Method to count occurrences of a word

    // Order by id DESC so the newest inserted items appear first (more robust than ordering by a formatted date string)
    @Query("SELECT * FROM word_history WHERE language = :language ORDER BY id DESC")
    suspend fun getAllWords(language: String): List<Word> // Now a suspend function
}