package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "save_slots")
data class SaveSlotEntity(
    @PrimaryKey val slotId: Int, // 0 = AutoSave, 1..5 = Manual Save Slots
    val slotName: String,
    val locationId: String,
    val locationName: String,
    val storyProgressTitle: String,
    val playTimeSeconds: Long,
    val updatedTimestamp: Long,
    val empathyScore: Int,
    val courageScore: Int,
    val playerX: Float,
    val playerY: Float,
    val currentDialogueId: String?,
    val discoveredMemoryIds: String, // comma separated IDs
    val inventoryIds: String, // comma separated IDs
    val completedObjectiveIds: String, // comma separated IDs
    val choicesMade: String, // key:value pairs
    val isPopulated: Boolean = true
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val isUnlocked: Boolean,
    val unlockedTimestamp: Long?
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val isUnlocked: Boolean,
    val unlockedTimestamp: Long?
)

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val musicVolume: Float = 0.8f,
    val sfxVolume: Float = 0.9f,
    val ambienceVolume: Float = 0.7f,
    val textSpeed: Int = 2,
    val joystickSensitivity: Float = 1.0f,
    val hapticFeedback: Boolean = true,
    val puzzleHints: Boolean = true,
    val cinematicSubtitles: Boolean = true
)

