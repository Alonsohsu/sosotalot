package com.example.sosotalot.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sosotalot.R
import com.example.sosotalot.databinding.FragmentDashboardBinding
import com.example.sosotalot.network.OpenAIService
import com.example.sosotalot.ui.result.TarotResultActivity
import com.example.sosotalot.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val tarotCards by lazy {
        resources.getStringArray(R.array.all_tarot_cards).toList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        binding.imageButton.isEnabled = false

        binding.editTextQuestion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkEnableButton()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.radioGroup.setOnCheckedChangeListener { _, _ ->
            checkEnableButton()
        }

        binding.imageButton.setOnClickListener {
            val question = binding.editTextQuestion.text.toString().trim()
            val numCards = getSelectedCardCount()

            if (question.isNotEmpty() && numCards > 0) {
                if (NetworkUtils.isNetworkAvailable(requireContext())) {
                    val selectedCards = getRandomTarotCards(numCards)
                    fetchTarotInterpretation(question, selectedCards)
                } else {
                    Toast.makeText(requireContext(), "請連接網路後再抽卡", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "請輸入問題並選擇抽牌數量", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun checkEnableButton() {
        val questionNotEmpty = binding.editTextQuestion.text.toString().trim().isNotEmpty()
        val numCardsSelected = getSelectedCardCount() > 0
        binding.imageButton.isEnabled = questionNotEmpty && numCardsSelected
    }

    private fun getSelectedCardCount(): Int {
        return when (binding.radioGroup.checkedRadioButtonId) {
            R.id.radioOne -> 1
            R.id.radioTwo -> 2
            R.id.radioThree -> 3
            else -> 0
        }
    }

    private fun getRandomTarotCards(count: Int): List<Pair<String, String>> {
        return tarotCards.shuffled().take(count).map { card ->
            val orientation = if (Random.nextBoolean()) "正位" else "逆位"
            Pair(card, orientation)
        }
    }

    private fun fetchTarotInterpretation(question: String, tarotCards: List<Pair<String, String>>) {
        // 確保傳遞不重複的卡片數量
        val selectedCards = tarotCards.distinct().take(getSelectedCardCount())

        val intent = Intent(requireContext(), TarotResultActivity::class.java).apply {
            putExtra("question", question)
            putExtra("tarot_cards", ArrayList(selectedCards)) // ✅ 傳遞完整的卡片清單
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
