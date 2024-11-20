import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import com.coding.meet.storeimagesinroomdatabase.HistoryDatabase
import java.util.concurrent.Executors

object DatabaseInstance {
    @Volatile
    private var instance: HistoryDatabase? = null

    fun getDatabase(context: Context): HistoryDatabase {
        return instance ?: synchronized(this) {
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                HistoryDatabase::class.java,
                "history_db"
            ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE) // 可选，查看写入模式
                .fallbackToDestructiveMigration()
                .setQueryCallback(
                    { sqlQuery, bindArgs -> Log.d("RoomQuery", "SQL: $sqlQuery Args: $bindArgs") },
                    Executors.newSingleThreadExecutor()
                ).build()
            instance = newInstance
            newInstance
        }
    }
}

