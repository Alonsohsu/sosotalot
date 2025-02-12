package com.example.sosotalot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TarotViewModel : ViewModel() {
    private val _drawnCards = MutableLiveData<List<Pair<String, String>>>()
    val drawnCards: LiveData<List<Pair<String, String>>> = _drawnCards

    private val _selectedIndex = MutableLiveData<Int>()
    val selectedIndex: LiveData<Int> = _selectedIndex

    private val _question = MutableLiveData<String>()
    val question: LiveData<String> = _question

    private val _interpretation = MutableLiveData<String>()
    val interpretation: LiveData<String> = _interpretation

    private val revealedCards = mutableSetOf<String>()  // 記錄已點擊的卡牌

    fun setTarotData(question: String, cards: List<Pair<String, String>>, interpretation: String, index: Int) {
        _question.value = question
        _drawnCards.value = cards
        _interpretation.value = interpretation
        _selectedIndex.value = index
    }

    fun clearTarotData() {
        _question.value = ""
        _drawnCards.value = emptyList()
        _interpretation.value = ""
        _selectedIndex.value = -1
    }


    fun isCardRevealed(cardName: String): Boolean {
        return revealedCards.contains(cardName)
    }
}

