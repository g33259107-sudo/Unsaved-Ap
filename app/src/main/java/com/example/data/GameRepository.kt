package com.example.data

import android.content.Context
import com.example.data.db.*
import com.example.story.StoryData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameRepository(context: Context) {

    private val db = UnsavedDatabase.getInstance(context)
    private val saveSlotDao = db.saveSlotDao()
    private val achievementDao = db.achievementDao()
    private val memoryDao = db.memoryDao()
    private val settingsDao = db.settingsDao()

    val allSaveSlots: Flow<List<SaveSlotEntity>> = saveSlotDao.getAllSaveSlots()
    val allAchievements: Flow<List<Achievement>> = achievementDao.getAllAchievements().map { entities ->
        val entityMap = entities.associateBy { it.id }
        StoryData.ALL_ACHIEVEMENTS.map { template ->
            val entity = entityMap[template.id]
            template.copy(
                isUnlocked = entity?.isUnlocked ?: false,
                unlockedDate = entity?.unlockedTimestamp?.let { java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(it)) }
            )
        }
    }

    val unlockedAchievementCount: Flow<Int> = achievementDao.getUnlockedCount()
    val unlockedMemoryCount: Flow<Int> = memoryDao.getUnlockedCount()

    val settingsFlow: Flow<GameSettings> = settingsDao.getSettingsFlow().map { entity ->
        if (entity != null) {
            GameSettings(
                musicVolume = entity.musicVolume,
                sfxVolume = entity.sfxVolume,
                ambienceVolume = entity.ambienceVolume,
                textSpeed = entity.textSpeed,
                joystickSensitivity = entity.joystickSensitivity,
                hapticFeedback = entity.hapticFeedback,
                puzzleHints = entity.puzzleHints,
                cinematicSubtitles = entity.cinematicSubtitles
            )
        } else {
            GameSettings()
        }
    }

    init {
        // Initialize template achievements and memories if not present
        CoroutineScope(Dispatchers.IO).launch {
            val initialAchievements = StoryData.ALL_ACHIEVEMENTS.map {
                AchievementEntity(it.id, it.title, it.description, it.category, false, null)
            }
            achievementDao.insertAchievements(initialAchievements)

            val initialMemories = StoryData.ALL_MEMORIES.map {
                MemoryEntity(it.id, false, null)
            }
            memoryDao.insertMemories(initialMemories)
        }
    }

    suspend fun getLatestSaveSlot(): SaveSlotEntity? = withContext(Dispatchers.IO) {
        saveSlotDao.getLatestSaveSlot()
    }

    suspend fun getSaveSlotById(slotId: Int): SaveSlotEntity? = withContext(Dispatchers.IO) {
        saveSlotDao.getSaveSlotById(slotId)
    }

    suspend fun saveGame(
        slotId: Int,
        slotName: String,
        locationId: LocationId,
        locationName: String,
        storyProgressTitle: String,
        playTimeSeconds: Long,
        empathyScore: Int,
        courageScore: Int,
        playerX: Float,
        playerY: Float,
        currentDialogueId: String?,
        discoveredMemoryIds: Set<String>,
        inventoryIds: Set<String>,
        completedObjectiveIds: Set<String>,
        choicesMade: Map<String, String>
    ) = withContext(Dispatchers.IO) {
        val entity = SaveSlotEntity(
            slotId = slotId,
            slotName = slotName,
            locationId = locationId.name,
            locationName = locationName,
            storyProgressTitle = storyProgressTitle,
            playTimeSeconds = playTimeSeconds,
            updatedTimestamp = System.currentTimeMillis(),
            empathyScore = empathyScore,
            courageScore = courageScore,
            playerX = playerX,
            playerY = playerY,
            currentDialogueId = currentDialogueId,
            discoveredMemoryIds = discoveredMemoryIds.joinToString(","),
            inventoryIds = inventoryIds.joinToString(","),
            completedObjectiveIds = completedObjectiveIds.joinToString(","),
            choicesMade = choicesMade.entries.joinToString(";") { "${it.key}:${it.value}" },
            isPopulated = true
        )
        saveSlotDao.insertOrUpdateSlot(entity)
    }

    suspend fun deleteSaveSlot(slotId: Int) = withContext(Dispatchers.IO) {
        saveSlotDao.deleteSlotById(slotId)
    }

    suspend fun unlockAchievement(id: String) = withContext(Dispatchers.IO) {
        achievementDao.unlockAchievement(id, System.currentTimeMillis())
    }

    suspend fun unlockMemory(id: String) = withContext(Dispatchers.IO) {
        memoryDao.unlockMemory(id, System.currentTimeMillis())
    }

    suspend fun saveSettings(settings: GameSettings) = withContext(Dispatchers.IO) {
        settingsDao.saveSettings(
            SettingsEntity(
                id = 1,
                musicVolume = settings.musicVolume,
                sfxVolume = settings.sfxVolume,
                ambienceVolume = settings.ambienceVolume,
                textSpeed = settings.textSpeed,
                joystickSensitivity = settings.joystickSensitivity,
                hapticFeedback = settings.hapticFeedback,
                puzzleHints = settings.puzzleHints,
                cinematicSubtitles = settings.cinematicSubtitles
            )
        )
    }
}
