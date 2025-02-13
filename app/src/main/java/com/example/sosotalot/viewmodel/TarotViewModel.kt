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

    private val _tarotInterpretation = MutableLiveData<String>()  // 🔥 新增完整解釋
    val tarotInterpretation: LiveData<String> = _tarotInterpretation

    private val _meaning1 = MutableLiveData<String>()  // 🔥 第一張牌解釋
    val meaning1: LiveData<String> = _meaning1

    private val _meaning2 = MutableLiveData<String>()  // 🔥 第二張牌解釋
    val meaning2: LiveData<String> = _meaning2

    private val _meaning3 = MutableLiveData<String>()  // 🔥 第三張牌解釋
    val meaning3: LiveData<String> = _meaning3

    private val revealedCards = mutableSetOf<String>()  // 記錄已點擊的卡牌

    /**
     * 設定塔羅牌數據
     */
    fun setTarotData(question: String, cards: List<Pair<String, String>>, interpretation: String, layoutId: Int, meaning1: String, meaning2: String, meaning3: String) {
        _question.value = question
        _drawnCards.value = cards
        _interpretation.value = interpretation
        _selectedIndex.value = layoutId

        _tarotInterpretation.value = interpretation
        _meaning1.value = meaning1
        _meaning2.value = meaning2
        _meaning3.value = meaning3
    }

    /**
     * 清除塔羅牌數據
     */
    fun clearTarotData() {
        _question.value = ""
        _drawnCards.value = emptyList()
        _interpretation.value = ""
        _selectedIndex.value = -1

        _tarotInterpretation.value = ""
        _meaning1.value = ""
        _meaning2.value = ""
        _meaning3.value = ""
    }

    /**
     * 確認卡牌是否已被點擊
     */
    fun isCardRevealed(cardName: String): Boolean {
        return revealedCards.contains(cardName)
    }
}
