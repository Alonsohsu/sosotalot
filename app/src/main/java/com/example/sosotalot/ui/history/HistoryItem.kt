data class HistoryItem(
    val time: Long,               // 时间戳
    val question: String,         // 用户提问
    val tarotCard: String, // 塔罗牌和方向
    val answer: String            // 解答
)
