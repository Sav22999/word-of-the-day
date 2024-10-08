package com.saverio.wordoftheday_en

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordHistoryActivity : AppCompatActivity() {
    private lateinit var wordHistoryAdapter: WordHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_word_history)

        val wordHistoryRecyclerView = findViewById<RecyclerView>(R.id.wordHistoryRecyclerView)

        // Load words using coroutines
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val words = loadWords() // Fetch words in the background

                // Toast to display fetched words
                val wordsText = words.joinToString(", ") { "${it.word}: ${it.definition}" } // Creating a string of words
                Log.d("WordHistoryActivity", "Fetched words: $wordsText") // Log the words fetched

                withContext(Dispatchers.Main) {
                    // Now we're on the main thread, so we can update the UI
                    if (words.isNotEmpty()) {
                        wordHistoryAdapter = WordHistoryAdapter(words)
                        wordHistoryRecyclerView.adapter = wordHistoryAdapter
                        wordHistoryRecyclerView.layoutManager = LinearLayoutManager(this@WordHistoryActivity)
                        wordHistoryAdapter.notifyDataSetChanged() // Notify the adapter of data changes

                        // Show a Toast message with the words
                        Toast.makeText(this@WordHistoryActivity, "Words loaded successfully: $wordsText", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@WordHistoryActivity, "No words found!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // Handle the exception (e.g., log it)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@WordHistoryActivity, "Error loading words: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private suspend fun loadWords(): List<Word> {
        val wordDao = WordDatabase.getDatabase(this).wordDao()
        return wordDao.getAllWords() // This is now a suspend call
    }


}
