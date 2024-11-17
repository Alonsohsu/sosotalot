import android.os.Parcelable

data class HistoryItem(
    val time: Long,        // 时间戳，记录抽牌的时间
    val question: String,  // 用户提出的问题
    val tarotCard: String, // 抽到的塔罗牌（包含正逆位）
    val answer: String     // 对问题的解答
)
