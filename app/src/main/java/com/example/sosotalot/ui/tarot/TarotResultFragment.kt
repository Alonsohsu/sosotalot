package com.example.sosotalot.ui.tarot

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.sosotalot.databinding.FragmentTarotResultBinding

class TarotResultFragment : Fragment() {

    private var _binding: FragmentTarotResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTarotResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val question = arguments?.getString("question") ?: "未知问题"
        val card_name = arguments?.getString("selected_card_name") ?: "無此卡片"
        val card_pos = arguments?.getString("selected_card_position") ?: "錯誤"
        val interpretation = arguments?.getString("meaning_text") ?: "暂无解读"

        val drawable = getCardImage(card_name)
        drawable?.let {
            binding.imageViewCard.setImageDrawable(it)

            // 🔄 如果是逆位，翻轉圖片 180 度
            binding.imageViewCard.scaleY = if (card_pos == "逆位") -1f else 1f
        }

        binding.textViewCardName.text = String.format("%s（%s）", card_name, card_pos)
        binding.textViewResult.text = "解读结果: $interpretation"

        // 設置返回按鈕點擊事件
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp() // 返回上一個 Fragment
        }
    }


    private fun getCardImage(cardName: String): Drawable? {
        return try {
            val assetManager = requireContext().assets
            val formattedName = formatCardName(cardName)
            val inputStream = assetManager.open("tarot_cards/$formattedName.jpg")
            Drawable.createFromStream(inputStream, null)
        } catch (e: Exception) {
            null // 如果找不到對應的圖片，返回 null
        }
    }

    private fun formatCardName(cardName: String): String {
        val cardMap = mapOf(
            // 大阿爾克那 (Major Arcana)
            "愚者" to "00_The_Fool",
            "魔術師" to "01_The_Magician",
            "女祭司" to "02_The_High_Priestess",
            "女皇" to "03_The_Empress",
            "皇帝" to "04_The_Emperor",
            "教皇" to "05_The_Hierophant",
            "戀人" to "06_The_Lovers",
            "戰車" to "07_The_Chariot",
            "力量" to "08_Strength",
            "隱士" to "09_The_Hermit",
            "命運之輪" to "10_Wheel_of_Fortune",
            "正義" to "11_Justice",
            "倒吊人" to "12_The_Hanged_Man",
            "死神" to "13_Death",
            "節制" to "14_Temperance",
            "惡魔" to "15_The_Devil",
            "高塔" to "16_The_Tower",
            "星星" to "17_The_Star",
            "月亮" to "18_The_Moon",
            "太陽" to "19_The_Sun",
            "審判" to "20_Judgment",
            "世界" to "21_The_World",

            // 聖杯 (Cups)
            "聖杯王牌" to "Ace_of_Cups",
            "聖杯二" to "02_Two_of_Cups",
            "聖杯三" to "03_Three_of_Cups",
            "聖杯四" to "04_Four_of_Cups",
            "聖杯五" to "05_Five_of_Cups",
            "聖杯六" to "06_Six_of_Cups",
            "聖杯七" to "07_Seven_of_Cups",
            "聖杯八" to "08_Eight_of_Cups",
            "聖杯九" to "09_Nine_of_Cups",
            "聖杯十" to "10_Ten_of_Cups",
            "聖杯侍者" to "11_Page_of_Cups",
            "聖杯騎士" to "12_Knight_of_Cups",
            "聖杯皇后" to "13_Queen_of_Cups",
            "聖杯國王" to "14_King_of_Cups",

            // 寶劍 (Swords)
            "寶劍王牌" to "Ace_of_Swords",
            "寶劍二" to "02_Two_of_Swords",
            "寶劍三" to "03_Three_of_Swords",
            "寶劍四" to "04_Four_of_Swords",
            "寶劍五" to "05_Five_of_Swords",
            "寶劍六" to "06_Six_of_Swords",
            "寶劍七" to "07_Seven_of_Swords",
            "寶劍八" to "08_Eight_of_Swords",
            "寶劍九" to "09_Nine_of_Swords",
            "寶劍十" to "10_Ten_of_Swords",
            "寶劍侍者" to "11_Page_of_Swords",
            "寶劍騎士" to "12_Knight_of_Swords",
            "寶劍皇后" to "13_Queen_of_Swords",
            "寶劍國王" to "14_King_of_Swords",

            // 權杖 (Wands)
            "權杖王牌" to "Ace_of_Wands",
            "權杖二" to "02_Two_of_Wands",
            "權杖三" to "03_Three_of_Wands",
            "權杖四" to "04_Four_of_Wands",
            "權杖五" to "05_Five_of_Wands",
            "權杖六" to "06_Six_of_Wands",
            "權杖七" to "07_Seven_of_Wands",
            "權杖八" to "08_Eight_of_Wands",
            "權杖九" to "09_Nine_of_Wands",
            "權杖十" to "10_Ten_of_Wands",
            "權杖侍者" to "11_Page_of_Wands",
            "權杖騎士" to "12_Knight_of_Wands",
            "權杖皇后" to "13_Queen_of_Wands",
            "權杖國王" to "14_King_of_Wands",

            // 錢幣 (Pentacles)
            "錢幣王牌" to "Ace_of_Pentacles",
            "錢幣二" to "02_Two_of_Pentacles",
            "錢幣三" to "03_Three_of_Pentacles",
            "錢幣四" to "04_Four_of_Pentacles",
            "錢幣五" to "05_Five_of_Pentacles",
            "錢幣六" to "06_Six_of_Pentacles",
            "錢幣七" to "07_Seven_of_Pentacles",
            "錢幣八" to "08_Eight_of_Pentacles",
            "錢幣九" to "09_Nine_of_Pentacles",
            "錢幣十" to "10_Ten_of_Pentacles",
            "錢幣侍者" to "11_Page_of_Pentacles",
            "錢幣騎士" to "12_Knight_of_Pentacles",
            "錢幣皇后" to "13_Queen_of_Pentacles",
            "錢幣國王" to "14_King_of_Pentacles"
        )
        return cardMap[cardName] ?: "default_card"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


