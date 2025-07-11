package com.saverio.wordoftheday_en

class allLanguages {
    //English, Italian, French, German, Spanish
    fun getLanguages(): List<String> {
        return listOf("en", "it", "fr");
    }

    fun getLanguageNames(): List<String> {
        return listOf("English", "Italian", "French")
    }

    /**
     * Get the name of language from its code (use the getLanguageNames() method to get the list of all languages)
     */
    fun getLanguageName(language: String): String {
        //if it's not a valid language, return "Unknown"
        if (!getLanguages().contains(language)) {
            return "Unknown"
        }
        return getLanguageNames()[getIndexOfLanguage(language)]
    }

    fun getIndexOfLanguage(language: String): Int {
        return getLanguages().indexOf(language)
    }
}