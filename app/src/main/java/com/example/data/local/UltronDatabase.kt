package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long = 1L,
    val role: String, // "user" or "assistant"
    val content: String,
    val intentCategory: String = "CONVERSATION",
    val modelUsed: String = "gemini-3.5-flash",
    val latencyMs: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String, // "Preference", "Goal", "Context", "Fact"
    val reasonSaved: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val tag: String = "General",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "dream_entries")
data class DreamEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val transcript: String,
    val jungianReflection: String,
    val symbolsDetected: String,
    val generatedImageBase64: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "mood_entries")
data class MoodEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moodEmoji: String,
    val moodLabel: String, // "Focused", "Calm", "Energized", "Tired", "Stressed"
    val score: Int, // 1..5
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val timeLabel: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "avatar_models")
data class AvatarModelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val styleType: String, // "PRIME_CORE", "SENTINEL_MECH", "CYBER_ORB", "VALKYRIE_GRID"
    val format: String, // "Procedural 3D + GLB Ready" or "GLB 2.0"
    val fileSizeLabel: String,
    val license: String,
    val attribution: String,
    val sourceUrl: String,
    val primaryHex: String,
    val secondaryHex: String,
    val downloadState: String, // "INSTALLED", "AVAILABLE", "DOWNLOADING", "FAILED"
    val downloadProgress: Int = 100,
    val localPath: String? = null
)

@Dao
interface UltronDao {
    // Conversations & Messages
    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun observeConversations(): Flow<List<ConversationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: Long)

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun observeMessages(conversationId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 50")
    fun observeRecentMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessage(id: Long)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()

    // Yaadein (Memories)
    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    fun observeMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("DELETE FROM memories")
    suspend fun clearAllMemories()

    // Journal
    @Query("SELECT * FROM journal_entries ORDER BY updatedAt DESC")
    fun observeJournalEntries(): Flow<List<JournalEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntryEntity): Long

    @Update
    suspend fun updateJournalEntry(entry: JournalEntryEntity)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournalEntry(id: Long)

    @Query("DELETE FROM journal_entries")
    suspend fun clearAllJournalEntries()

    // Dream Journal
    @Query("SELECT * FROM dream_entries ORDER BY createdAt DESC")
    fun observeDreamEntries(): Flow<List<DreamEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDreamEntry(entry: DreamEntryEntity): Long

    @Query("DELETE FROM dream_entries WHERE id = :id")
    suspend fun deleteDreamEntry(id: Long)

    // Mood
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun observeMoodEntries(): Flow<List<MoodEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(entry: MoodEntryEntity): Long

    @Query("DELETE FROM mood_entries WHERE id = :id")
    suspend fun deleteMoodEntry(id: Long)

    @Query("DELETE FROM mood_entries")
    suspend fun clearAllMoodEntries()

    // Reminders (Aaj)
    @Query("SELECT * FROM reminders ORDER BY isCompleted ASC, createdAt DESC")
    fun observeReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminder(id: Long)

    // Avatar Models
    @Query("SELECT * FROM avatar_models")
    fun observeAvatars(): Flow<List<AvatarModelEntity>>

    @Query("SELECT COUNT(*) FROM avatar_models")
    suspend fun getAvatarCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAvatars(models: List<AvatarModelEntity>)

    @Update
    suspend fun updateAvatar(model: AvatarModelEntity)
}

@Database(
    entities = [
        ConversationEntity::class,
        ChatMessageEntity::class,
        MemoryEntity::class,
        JournalEntryEntity::class,
        DreamEntryEntity::class,
        MoodEntryEntity::class,
        ReminderEntity::class,
        AvatarModelEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class UltronDatabase : RoomDatabase() {
    abstract fun ultronDao(): UltronDao

    companion object {
        @Volatile
        private var INSTANCE: UltronDatabase? = null

        fun getInstance(context: Context): UltronDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UltronDatabase::class.java,
                    "ultron_ai_database.db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
