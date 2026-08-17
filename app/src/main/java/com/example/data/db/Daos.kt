package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SaveSlotDao {
    @Query("SELECT * FROM save_slots ORDER BY slotId ASC")
    fun getAllSaveSlots(): Flow<List<SaveSlotEntity>>

    @Query("SELECT * FROM save_slots WHERE slotId = :slotId LIMIT 1")
    suspend fun getSaveSlotById(slotId: Int): SaveSlotEntity?

    @Query("SELECT * FROM save_slots WHERE isPopulated = 1 ORDER BY updatedTimestamp DESC LIMIT 1")
    suspend fun getLatestSaveSlot(): SaveSlotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSlot(slot: SaveSlotEntity)

    @Query("DELETE FROM save_slots WHERE slotId = :slotId")
    suspend fun deleteSlotById(slotId: Int)

    @Query("DELETE FROM save_slots")
    suspend fun clearAllSlots()
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedTimestamp = :timestamp WHERE id = :id")
    suspend fun unlockAchievement(id: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedCount(): Flow<Int>
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemories(memories: List<MemoryEntity>)

    @Query("UPDATE memories SET isUnlocked = 1, unlockedTimestamp = :timestamp WHERE id = :id")
    suspend fun unlockMemory(id: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM memories WHERE isUnlocked = 1")
    fun getUnlockedCount(): Flow<Int>
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    fun getSettingsFlow(): Flow<SettingsEntity?>

    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    suspend fun getSettings(): SettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: SettingsEntity)
}
