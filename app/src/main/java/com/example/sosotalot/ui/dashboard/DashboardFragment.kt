package com.example.sosotalot.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.coding.meet.storeimagesinroomdatabase.ImageDatabase
import com.example.sosotalot.databinding.FragmentDashboardBinding

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import kotlin.random.Random

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // Room 数据库实例
    private lateinit var tflite: Interpreter

    // 塔罗牌列表
    private val tarotCards = listOf(
        "The Fool - 愚者",
        "The Magician - 魔术师",
        "The High Priestess - 女祭司",
        "The Empress - 女皇",
        "The Emperor - 皇帝",
        "The Hierophant - 教皇",
        "The Lovers - 恋人",
        "The Chariot - 战车",
        "Strength - 力量",
        "The Hermit - 隐士",
        "Wheel of Fortune - 命运之轮",
        "Justice - 正义",
        "The Hanged Man - 倒吊人",
        "Death - 死神",
        "Temperance - 节制",
        "The Devil - 恶魔",
        "The Tower - 高塔",
        "The Star - 星星",
        "The Moon - 月亮",
        "The Sun - 太阳",
        "Judgement - 审判",
        "The World - 世界"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        val imageDatabase = Room.databaseBuilder(
            requireContext().applicationContext,
            ImageDatabase::class.java,
            "images_db"
        ).build()

//        var context = requireContext().applicationContext
//        // 初始化 Room 数据库
//        try {
//            database = DatabaseInstance.getDatabase(context)
//        } catch (e: Exception) {
//            Log.e("DatabaseInstance", "Error initializing Room database", e)
//        }

//        val db = Room.databaseBuilder(
//            requireContext().applicationContext,
//            AppDatabase::class.java, "app_database"
//        ).build()




        // 禁用抽牌按钮，直到输入问题
        binding.imageButton.isEnabled = false

        // 监听输入框内容变化
        binding.editTextQuestion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.imageButton.isEnabled = s.toString().trim().isNotEmpty()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 随机选择一张塔罗牌并决定正逆位
        fun getRandomTarotCardWithOrientation(): Pair<String, String> {
            val card = tarotCards[Random.nextInt(tarotCards.size)]
            val orientation = if (Random.nextBoolean()) "正位" else "逆位" // 随机正逆位
            return Pair(card, orientation)
        }

        // 设置 ImageButton 点击事件
        binding.imageButton.setOnClickListener {
            val question = binding.editTextQuestion.text.toString().trim()
            if (question.isNotEmpty()) {
                val (randomCard, orientation) = getRandomTarotCardWithOrientation()
                val result = inferWithLocalModel(question, "$randomCard ($orientation)")

                binding.textView.text =
                    "问题：$question\n抽到的塔罗牌：$randomCard\n正逆位：$orientation\n解答：$result"

                // 保存记录到数据库
                saveRecordToDatabase(question, randomCard, orientation, result)
            } else {
                Toast.makeText(requireContext(), "请先输入问题再抽牌", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    // 保存记录到 Room 数据库
    private fun saveRecordToDatabase(question: String, card: String, orientation: String, result: String) {
//        val record = HistoryItem(
//            time = System.currentTimeMillis(),
//            tarotCard = card,
//            orientation = orientation,
//            answer = result,
//            question = question
//        )

        // 异步保存记录到数据库
//        CoroutineScope(Dispatchers.IO).launch {
//            database.historyDao().insertAll(record)
//        }
        Toast.makeText(requireContext(), "记录已保存", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 避免内存泄漏
    }

    private fun inferWithLocalModel(question: String, tarotCardWithOrientation: String): String {
        // 模拟输入和输出
        val input = Array(1) { FloatArray(128) } // 模拟输入
        val output = Array(1) { FloatArray(128) } // 模拟输出

        try {
            tflite.run(input, output)
        } catch (e: Exception) {
            Log.e("TFLite", "推理失败: ${e.localizedMessage}")
            return "本地模型解答失败，请检查模型设置。"
        }

        // 转换输出结果为字符串（需要自定义处理）
        return "结合 $tarotCardWithOrientation 的模拟塔罗牌解答。"
    }
}


