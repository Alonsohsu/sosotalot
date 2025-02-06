package com.coding.meet.storeimagesinroomdatabase

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * @author Coding Meet
 * Created 28-11-2023 at 04:02 pm
 */

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(historyModel: HistoryModel)

    @Query("SELECT * FROM history")
    fun getAllHistory(): LiveData<List<HistoryModel>>

    @Query("DELETE FROM history")
    suspend fun deleteAllHistory()

}