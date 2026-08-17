package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SaveSlotEntity::class,
        AchievementEntity::class,
        MemoryEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class UnsavedDatabase : RoomDatabase() {
    abstract fun saveSlotDao(): SaveSlotDao
    abstract fun achievementDao(): AchievementDao
    abstract fun memoryDao(): MemoryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: UnsavedDatabase? = null

        fun getInstance(context: Context): UnsavedDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UnsavedDatabase::class.java,
                    "unsaved_game.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
