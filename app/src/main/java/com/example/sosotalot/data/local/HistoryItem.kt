package com.coding.meet.storeimagesinroomdatabase

import androidx.room.*

@Entity(tableName = "history")
data class HistoryModel(
    @PrimaryKey(autoGenerate = false)
    val time: Long,  // 如果 time 是主键且为 String 类型
    @ColumnInfo(name = "tarotCard")
    val tarotCard: String,
    @ColumnInfo(name = "orientation")
    val orientation: String,
    @ColumnInfo(name = "question")
    val question: String,
    @ColumnInfo(name = "answer")
    val answer: String
)
