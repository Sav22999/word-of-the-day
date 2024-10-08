package com.saverio.wordoftheday_en
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WordHistoryAdapter(private val words: List<Word>) : RecyclerView.Adapter<WordHistoryAdapter.WordViewHolder>() {

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordView: TextView = itemView.findViewById(R.id.wordView)
        val wordMeaningView: TextView = itemView.findViewById(R.id.wordMeaningView)
        val wordDateView: TextView = itemView.findViewById(R.id.wordDateView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_word_history, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.wordDateView.text = word.date // Assuming `date` holds the date
        holder.wordView.text = word.word // Assuming `word` has a `word` property
        holder.wordMeaningView.text = word.definition // Assuming `definition` holds the meaning
    }

    override fun getItemCount(): Int {
        return words.size
    }
}
