package com.example.sosotalot.ui.dashboard

import HistoryItem
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sosotalot.databinding.FragmentDashboardBinding
import com.example.sosotalot.ui.history.HistoryActivity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.random.Random

class DashboardFragment : Fragment() {
    // 定义一个全局的历史记录列表
    private val historyList = mutableListOf<HistoryItem>()

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

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

    // TensorFlow Lite 模型
    private lateinit var tflite: Interpreter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // 禁用抽牌按钮，直到输入问题
        binding.imageButton.isEnabled = false

        // 初始化本地模型
        try {
            tflite = Interpreter(loadModelFile("model.tflite"))
        } catch (e: Exception) {
            Log.e("TFLite", "加载模型失败: ${e.localizedMessage}")
            Toast.makeText(requireContext(), "加载模型失败", Toast.LENGTH_SHORT).show()
        }

        // 监听输入框内容变化
        binding.editTextQuestion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.imageButton.isEnabled = s.toString().trim().isNotEmpty()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 设置 ImageButton 点击事件
        binding.imageButton.setOnClickListener {
            val question = binding.editTextQuestion.text.toString().trim()
            if (question.isNotEmpty()) {
                val (randomCard, orientation) = getRandomTarotCardWithOrientation()
                val result = inferWithLocalModel(question, "$randomCard ($orientation)")
                binding.textView.text =
                    "问题：$question\n抽到的塔罗牌：$randomCard\n正逆位：$orientation\n解答：$result"

                // 通过 Intent 传递数据到 HistoryActivity
                val intent = Intent(requireContext(), HistoryActivity::class.java)
                intent.putExtra("question", question)
                intent.putExtra("tarotCard", randomCard)
                intent.putExtra("orientation", orientation)
                intent.putExtra("answer", result)
                startActivity(intent)
            } else {
                Toast.makeText(requireContext(), "请先输入问题再抽牌", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 随机选择一张塔罗牌并决定正逆位
    private fun getRandomTarotCardWithOrientation(): Pair<String, String> {
        val card = tarotCards[Random.nextInt(tarotCards.size)]
        val orientation = if (Random.nextBoolean()) "正位" else "逆位" // 随机正逆位
        return Pair(card, orientation)
    }

    // 加载 TensorFlow Lite 模型文件
    private fun loadModelFile(modelFileName: String): MappedByteBuffer {
        val fileDescriptor = requireContext().assets.openFd(modelFileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }

    // 使用本地模型进行推理
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
