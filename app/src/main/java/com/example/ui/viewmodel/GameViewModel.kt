package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngine
import com.example.data.*
import com.example.data.db.SaveSlotEntity
import com.example.story.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppScreen {
    MAIN_MENU,
    OPENING_CINEMATIC,
    PLAYING,
    SAVE_LOAD,
    INVENTORY,
    MEMORY_ARCHIVE,
    MAP,
    PAUSE_MENU,
    SETTINGS,
    ACHIEVEMENTS,
    CREDITS,
    PUZZLE,
    ENDING_CINEMATIC
}

data class GameUiState(
    val currentScreen: AppScreen = AppScreen.MAIN_MENU,
    val currentLocationScene: LocationScene = LocationSceneRepository.getScene(LocationId.NOCTURNE_STATION),
    val playerX: Float = 0f,
    val playerY: Float = 0f,
    val isRunning: Boolean = false,
    val isCrouched: Boolean = false,
    val isHiding: Boolean = false,
    val keeperTension: Float = 0f,
    val currentDialogue: DialogueLine? = null,
    val dialogueTextProgress: Int = 0,
    val isDialogueComplete: Boolean = false,
    val activePuzzleId: String? = null,
    val puzzleFeedback: String? = null,
    val activeEnding: EndingType? = null,
    val inventory: List<InventoryItem> = listOf(StoryData.ALL_ITEMS[0], StoryData.ALL_ITEMS[1]),
    val discoveredMemoryIds: Set<String> = emptySet(),
    val completedObjectiveIds: Set<String> = emptySet(),
    val choicesMade: Map<String, String> = emptyMap(),
    val empathyScore: Int = 0,
    val courageScore: Int = 0,
    val playTimeSeconds: Long = 0L,
    val isAutoSaving: Boolean = false,
    val autoSaveMessage: String? = null,
    val activeNearbyEntity: InteractiveEntity? = null,
    val memoryInspectModal: MemoryFragment? = null,
    val itemInspectModal: InventoryItem? = null,
    val activeFlashbackMemory: MemoryFragment? = null,
    val toastNotification: String? = null,
    val saveSlots: List<SaveSlotEntity> = emptyList(),
    val achievements: List<Achievement> = emptyList(),
    val settings: GameSettings = GameSettings()
) {
    val inventoryItems: List<InventoryItem> get() = inventory
    val endingReached: EndingType get() = activeEnding ?: EndingType.ENDING_A
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)
    val audioEngine = AudioEngine()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val saveSlots: StateFlow<List<SaveSlotEntity>> = repository.allSaveSlots
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val achievements: StateFlow<List<Achievement>> = repository.allAchievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<GameSettings> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GameSettings())

    private var previousScreen: AppScreen = AppScreen.MAIN_MENU
    private var playTimerJob: Job? = null
    private var dialogueTypewriterJob: Job? = null

    init {
        audioEngine.start()
        viewModelScope.launch {
            repository.settingsFlow.collect { currentSettings ->
                _uiState.update { it.copy(settings = currentSettings) }
                audioEngine.updateVolumes(
                    currentSettings.musicVolume,
                    currentSettings.sfxVolume,
                    currentSettings.ambienceVolume
                )
            }
        }
        viewModelScope.launch {
            repository.allSaveSlots.collect { slots ->
                _uiState.update { it.copy(saveSlots = slots) }
            }
        }
        viewModelScope.launch {
            repository.allAchievements.collect { achs ->
                _uiState.update { it.copy(achievements = achs) }
            }
        }
        startPlayTimer()
    }

    private fun startPlayTimer() {
        playTimerJob?.cancel()
        playTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (_uiState.value.currentScreen == AppScreen.PLAYING) {
                    _uiState.update { it.copy(playTimeSeconds = it.playTimeSeconds + 1) }
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        val current = _uiState.value.currentScreen
        if (current != screen) {
            previousScreen = current
        }
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun navigateBack() {
        val current = _uiState.value.currentScreen
        val target = when (current) {
            AppScreen.PAUSE_MENU -> AppScreen.PLAYING
            AppScreen.INVENTORY, AppScreen.MAP, AppScreen.MEMORY_ARCHIVE, AppScreen.SETTINGS -> {
                if (previousScreen == AppScreen.PAUSE_MENU || previousScreen == AppScreen.PLAYING) AppScreen.PLAYING
                else AppScreen.MAIN_MENU
            }
            AppScreen.SAVE_LOAD, AppScreen.ACHIEVEMENTS, AppScreen.CREDITS -> {
                if (previousScreen == AppScreen.PLAYING || previousScreen == AppScreen.PAUSE_MENU) AppScreen.PAUSE_MENU
                else AppScreen.MAIN_MENU
            }
            else -> AppScreen.MAIN_MENU
        }
        _uiState.update { it.copy(currentScreen = target) }
    }

    fun continueGame() = continueLatestGame()
    fun movePlayer(dx: Float, dy: Float, running: Boolean) = updatePlayerMovement(dx, dy, running)
    fun interactWithNearby() = interactWithNearbyEntity()
    fun advanceDialogue() = advanceDialogueOrSkip()
    fun selectDialogueChoice(choice: DialogueChoice) = makeChoice(choice)
    fun dismissMemoryInspectModal() = inspectMemory(null)
    fun saveGame(slotId: Int, name: String) = saveGameToSlot(slotId, name)
    fun loadGame(slot: SaveSlotEntity) = loadGameFromSlot(slot)
    fun resumeGame() = navigateTo(AppScreen.PLAYING)
    fun quickSave() = triggerAutoSave("Manual Quick Save")
    fun returnToMainMenu() = navigateTo(AppScreen.MAIN_MENU)
    fun onPuzzleSolved() = solvePuzzleSuccess(_uiState.value.activePuzzleId ?: "")

    fun startNewGame() {
        val startScene = LocationSceneRepository.getScene(LocationId.NOCTURNE_STATION)
        _uiState.update {
            GameUiState(
                currentScreen = AppScreen.PLAYING,
                currentLocationScene = startScene,
                playerX = startScene.playerStartX,
                playerY = startScene.playerStartY,
                inventory = listOf(StoryData.ALL_ITEMS[0], StoryData.ALL_ITEMS[1]), // Phone & Pendant
                discoveredMemoryIds = emptySet(),
                completedObjectiveIds = emptySet(),
                choicesMade = emptyMap(),
                empathyScore = 0,
                courageScore = 0,
                playTimeSeconds = 0L,
                toastNotification = "CHAPTER 1: The First Memory • Nocturne Station"
            )
        }
        audioEngine.setMood("mystery")
        startDialogue("C1_INTRO_1")
        triggerAutoSave("Awakened at Nocturne Station...")
        viewModelScope.launch {
            repository.unlockAchievement("AWAKENED")
        }
    }

    fun finishOpeningCinematic() {
        _uiState.update { it.copy(currentScreen = AppScreen.PLAYING) }
        startDialogue("C1_INTRO_1")
        triggerAutoSave("Awakened at Nocturne Station...")
    }

    fun transitionToLocation(locationId: LocationId, customStartX: Float? = null, customStartY: Float? = null) {
        val newScene = LocationSceneRepository.getScene(locationId)
        val startX = customStartX ?: newScene.playerStartX
        val startY = customStartY ?: newScene.playerStartY

        _uiState.update {
            it.copy(
                currentLocationScene = newScene,
                playerX = startX,
                playerY = startY,
                currentDialogue = null,
                activeNearbyEntity = null
            )
        }
        audioEngine.setMood(newScene.musicMood)
        showToast("Entered: ${locationId.displayName}")
        triggerAutoSave("Arrived at ${locationId.displayName}")
    }

    fun updateStealthState(tension: Float, noise: Float, isCrouched: Boolean, isHiding: Boolean) {
        audioEngine.keeperTensionLevel = tension
        audioEngine.isPlayerCrouching = isCrouched

        _uiState.update {
            it.copy(
                keeperTension = tension,
                isCrouched = isCrouched,
                isHiding = isHiding
            )
        }

        if (tension > 0.6f && isHiding) {
            viewModelScope.launch {
                repository.unlockAchievement("STEALTH_SURVIVOR")
            }
        }
    }

    fun updatePlayerMovement(dx: Float, dy: Float, running: Boolean) {
        val isMoving = dx != 0f || dy != 0f
        audioEngine.isPlayerMoving = isMoving
        audioEngine.isPlayerRunning = running

        _uiState.update {
            it.copy(isRunning = running)
        }
    }

    fun interactWithNearbyEntity() {
        val entity = _uiState.value.activeNearbyEntity ?: return

        when (entity.type) {
            EntityType.NPC, EntityType.INTERACTABLE -> {
                entity.targetDialogueId?.let { startDialogue(it) }
            }
            EntityType.MEMORY_SHARD -> {
                entity.targetMemoryId?.let { memId ->
                    unlockMemoryShard(memId)
                }
            }
            EntityType.PUZZLE_STATION -> {
                entity.targetPuzzleId?.let { puzzleId ->
                    _uiState.update { it.copy(activePuzzleId = puzzleId, currentScreen = AppScreen.PUZZLE) }
                    audioEngine.setMood("suspense")
                }
            }
            EntityType.DOOR_PORTAL -> {
                if (entity.requiredItemId != null && _uiState.value.inventory.none { it.id == entity.requiredItemId }) {
                    showToast("Locked: Required key item not found.")
                } else {
                    entity.targetDialogueId?.let { startDialogue(it) }
                }
            }
            EntityType.ITEM_PICKUP, EntityType.AUDIO_RECORDER -> {
                showToast("Acquired: ${entity.name}")
            }
            EntityType.HIDING_SPOT -> {
                // Handled directly in 3D GL Controller
            }
            else -> {
                // Default fallback
            }
        }
    }

    fun startDialogue(dialogueId: String) {
        val line = DialogueRepository.getDialogue(dialogueId) ?: return
        audioEngine.setMood(line.soundMood)
        _uiState.update {
            it.copy(
                currentDialogue = line,
                dialogueTextProgress = 0,
                isDialogueComplete = false
            )
        }

        dialogueTypewriterJob?.cancel()
        val textSpeed = when (settings.value.textSpeed) {
            1 -> 45L
            2 -> 25L
            3 -> 12L
            else -> 0L
        }

        if (textSpeed == 0L) {
            _uiState.update { it.copy(dialogueTextProgress = line.text.length, isDialogueComplete = true) }
        } else {
            dialogueTypewriterJob = viewModelScope.launch {
                for (i in 1..line.text.length) {
                    _uiState.update { it.copy(dialogueTextProgress = i) }
                    delay(textSpeed)
                }
                _uiState.update { it.copy(isDialogueComplete = true) }
            }
        }
    }

    fun advanceDialogueOrSkip() {
        val state = _uiState.value
        val line = state.currentDialogue ?: return

        if (!state.isDialogueComplete) {
            dialogueTypewriterJob?.cancel()
            _uiState.update { it.copy(dialogueTextProgress = line.text.length, isDialogueComplete = true) }
            return
        }

        if (line.choices.isNotEmpty()) {
            return
        }

        if (line.triggersPuzzleId != null) {
            _uiState.update { it.copy(activePuzzleId = line.triggersPuzzleId, currentScreen = AppScreen.PUZZLE, currentDialogue = null) }
            return
        }

        if (line.triggersCutsceneId != null) {
            handleCutsceneTrigger(line.triggersCutsceneId)
            return
        }

        if (line.nextLineId != null) {
            startDialogue(line.nextLineId)
        } else {
            _uiState.update { it.copy(currentDialogue = null) }
            audioEngine.setMood(state.currentLocationScene.musicMood)
        }
    }

    fun makeChoice(choice: DialogueChoice) {
        val state = _uiState.value

        val newEmpathy = state.empathyScore + choice.empathyDelta
        val newCourage = state.courageScore + choice.courageDelta
        val newChoices = state.choicesMade.toMutableMap().apply {
            put(choice.choiceId, choice.text)
        }

        var newInventory = state.inventory
        if (choice.grantItemId != null) {
            val itemToAdd = StoryData.ALL_ITEMS.find { it.id == choice.grantItemId }
            if (itemToAdd != null && newInventory.none { it.id == itemToAdd.id }) {
                newInventory = newInventory + itemToAdd
                showToast("Obtained: ${itemToAdd.name}")
            }
        }

        _uiState.update {
            it.copy(
                empathyScore = newEmpathy,
                courageScore = newCourage,
                choicesMade = newChoices,
                inventory = newInventory
            )
        }

        if (choice.choiceId == "CH_ENDING_A") {
            _uiState.update { it.copy(activeEnding = EndingType.ENDING_A) }
            viewModelScope.launch { repository.unlockAchievement("ENDING_A") }
        } else if (choice.choiceId == "CH_ENDING_B") {
            _uiState.update { it.copy(activeEnding = EndingType.ENDING_B) }
            viewModelScope.launch { repository.unlockAchievement("ENDING_B") }
        } else if (choice.choiceId == "CH_ENDING_C") {
            _uiState.update { it.copy(activeEnding = EndingType.ENDING_C) }
            viewModelScope.launch { repository.unlockAchievement("ENDING_C") }
        }

        if (choice.nextDialogueId != null) {
            startDialogue(choice.nextDialogueId)
        } else {
            _uiState.update { it.copy(currentDialogue = null) }
        }
    }

    fun dismissFlashback() {
        _uiState.update { it.copy(activeFlashbackMemory = null) }
        audioEngine.setMood(_uiState.value.currentLocationScene.musicMood)
    }

    private fun handleCutsceneTrigger(cutsceneId: String) {
        when (cutsceneId) {
            "ENDING_A" -> {
                _uiState.update { it.copy(activeEnding = EndingType.ENDING_A, currentScreen = AppScreen.ENDING_CINEMATIC, currentDialogue = null) }
                audioEngine.setMood("ending")
            }
            "ENDING_B" -> {
                _uiState.update { it.copy(activeEnding = EndingType.ENDING_B, currentScreen = AppScreen.ENDING_CINEMATIC, currentDialogue = null) }
                audioEngine.setMood("ending")
            }
            "ENDING_C" -> {
                _uiState.update { it.copy(activeEnding = EndingType.ENDING_C, currentScreen = AppScreen.ENDING_CINEMATIC, currentDialogue = null) }
                audioEngine.setMood("ending")
            }
        }
    }

    fun unlockMemoryShard(memoryId: String) {
        val memory = StoryData.ALL_MEMORIES.find { it.id == memoryId } ?: return
        val currentDiscovered = _uiState.value.discoveredMemoryIds
        val isFirstDiscovery = !currentDiscovered.contains(memoryId)

        val updatedSet = currentDiscovered + memoryId
        val isCrucial = memory.isCrucial || memory.id == "MEM_01" || memory.id == "MEM_07"

        _uiState.update {
            it.copy(
                discoveredMemoryIds = updatedSet,
                activeFlashbackMemory = if (isCrucial) memory else null,
                memoryInspectModal = if (!isCrucial) memory else null
            )
        }

        if (isCrucial) {
            audioEngine.setMood("emotional")
        }

        if (isFirstDiscovery) {
            viewModelScope.launch {
                repository.unlockMemory(memoryId)
                repository.unlockAchievement("FIRST_SHARD")
                if (updatedSet.size >= 7) {
                    repository.unlockAchievement("COLLECTOR_OF_TEARS")
                }
            }
            triggerAutoSave("Saved Memory: ${memory.title}")
        }
    }

    fun solvePuzzleSuccess(puzzleId: String) {
        when (puzzleId) {
            "PUZZLE_FUSE_1" -> {
                viewModelScope.launch { repository.unlockAchievement("CIRCUIT_BREAKER") }
                val keyItem = StoryData.ALL_ITEMS.find { it.id == "ITEM_CAFE_KEY" }
                val updatedInventory = if (keyItem != null && _uiState.value.inventory.none { it.id == keyItem.id }) {
                    _uiState.value.inventory + keyItem
                } else _uiState.value.inventory
                val updatedObjectives = _uiState.value.completedObjectiveIds + "OBJ_2"
                _uiState.update { it.copy(inventory = updatedInventory, completedObjectiveIds = updatedObjectives) }
                showToast("Station Breaker Restored! Turnstile Key acquired.")
            }
            "PUZZLE_COFFEE_2" -> {
                viewModelScope.launch { repository.unlockAchievement("CAFE_NOCTURNE") }
                val cardItem = StoryData.ALL_ITEMS.find { it.id == "ITEM_CATACOMB_CARD" }
                val updatedInventory = if (cardItem != null && _uiState.value.inventory.none { it.id == cardItem.id }) {
                    _uiState.value.inventory + cardItem
                } else _uiState.value.inventory
                val updatedObjectives = _uiState.value.completedObjectiveIds + "OBJ_4"
                _uiState.update { it.copy(inventory = updatedInventory, completedObjectiveIds = updatedObjectives) }
                showToast("Unlocked Safe! Acquired Catacombs Access Card.")
            }
            "PUZZLE_RADIO_3" -> {
                viewModelScope.launch { repository.unlockAchievement("RADIO_FREQUENCY") }
                val tapeItem = StoryData.ALL_ITEMS.find { it.id == "ITEM_TAPE_1" }
                val updatedInventory = if (tapeItem != null && _uiState.value.inventory.none { it.id == tapeItem.id }) {
                    _uiState.value.inventory + tapeItem
                } else _uiState.value.inventory
                val updatedObjectives = _uiState.value.completedObjectiveIds + "OBJ_5"
                _uiState.update { it.copy(inventory = updatedInventory, completedObjectiveIds = updatedObjectives) }
                showToast("Broadcast Decrypted: 'The Busker's Melody' Cassette Tape acquired.")
            }
            "PUZZLE_STEAM_4" -> {
                viewModelScope.launch { repository.unlockAchievement("STEAM_EQUALIZER") }
                val gearItem = StoryData.ALL_ITEMS.find { it.id == "ITEM_CLOCK_GEAR" }
                val updatedInventory = if (gearItem != null && _uiState.value.inventory.none { it.id == gearItem.id }) {
                    _uiState.value.inventory + gearItem
                } else _uiState.value.inventory
                val updatedObjectives = _uiState.value.completedObjectiveIds + "OBJ_7"
                _uiState.update { it.copy(inventory = updatedInventory, completedObjectiveIds = updatedObjectives) }
                showToast("Steam Bypassed! Chrono Brass Gear acquired.")
            }
            "PUZZLE_CLOCK_3" -> {
                viewModelScope.launch { repository.unlockAchievement("TICKING_HOURS") }
                val updatedObjectives = _uiState.value.completedObjectiveIds + "OBJ_8"
                _uiState.update { it.copy(completedObjectiveIds = updatedObjectives) }
                showToast("Clock hands reset to 07:18! Archive Portal activated.")
            }
        }
        _uiState.update { it.copy(activePuzzleId = null, currentScreen = AppScreen.PLAYING) }
    }

    fun closePuzzle() {
        _uiState.update { it.copy(activePuzzleId = null, currentScreen = AppScreen.PLAYING) }
        audioEngine.setMood(_uiState.value.currentLocationScene.musicMood)
    }

    fun inspectMemory(memory: MemoryFragment?) {
        _uiState.update { it.copy(memoryInspectModal = memory) }
    }

    fun inspectItem(item: InventoryItem?) {
        _uiState.update { it.copy(itemInspectModal = item) }
    }

    fun saveGameToSlot(slotId: Int, slotName: String) {
        val state = _uiState.value
        viewModelScope.launch {
            repository.saveGame(
                slotId = slotId,
                slotName = slotName,
                locationId = state.currentLocationScene.locationId,
                locationName = state.currentLocationScene.locationId.displayName,
                storyProgressTitle = "Memories: ${state.discoveredMemoryIds.size}/7",
                playTimeSeconds = state.playTimeSeconds,
                empathyScore = state.empathyScore,
                courageScore = state.courageScore,
                playerX = state.playerX,
                playerY = state.playerY,
                currentDialogueId = state.currentDialogue?.id,
                discoveredMemoryIds = state.discoveredMemoryIds,
                inventoryIds = state.inventory.map { it.id }.toSet(),
                completedObjectiveIds = state.completedObjectiveIds,
                choicesMade = state.choicesMade
            )
            showToast("Saved to $slotName")
        }
    }

    fun loadGameFromSlot(slot: SaveSlotEntity) {
        val locId = try {
            LocationId.valueOf(slot.locationId)
        } catch (e: Exception) {
            LocationId.NOCTURNE_STATION
        }
        val scene = LocationSceneRepository.getScene(locId)

        val restoredInventory = slot.inventoryIds.split(",")
            .mapNotNull { id -> StoryData.ALL_ITEMS.find { it.id == id } }

        val restoredChoices = slot.choicesMade.split(";")
            .mapNotNull { pair ->
                val parts = pair.split(":")
                if (parts.size == 2) parts[0] to parts[1] else null
            }.toMap()

        _uiState.update {
            it.copy(
                currentScreen = AppScreen.PLAYING,
                currentLocationScene = scene,
                playerX = slot.playerX,
                playerY = slot.playerY,
                inventory = if (restoredInventory.isEmpty()) listOf(StoryData.ALL_ITEMS[0], StoryData.ALL_ITEMS[1]) else restoredInventory,
                discoveredMemoryIds = slot.discoveredMemoryIds.split(",").filter { id -> id.isNotBlank() }.toSet(),
                completedObjectiveIds = slot.completedObjectiveIds.split(",").filter { id -> id.isNotBlank() }.toSet(),
                choicesMade = restoredChoices,
                empathyScore = slot.empathyScore,
                courageScore = slot.courageScore,
                playTimeSeconds = slot.playTimeSeconds,
                currentDialogue = slot.currentDialogueId?.let { id -> DialogueRepository.getDialogue(id) }
            )
        }
        audioEngine.setMood(scene.musicMood)
    }

    fun continueLatestGame() {
        viewModelScope.launch {
            val latest = repository.getLatestSaveSlot()
            if (latest != null) {
                loadGameFromSlot(latest)
            } else {
                startNewGame()
            }
        }
    }

    fun deleteSaveSlot(slotId: Int) {
        viewModelScope.launch {
            repository.deleteSaveSlot(slotId)
            showToast("Slot deleted.")
        }
    }

    fun triggerAutoSave(reason: String) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isAutoSaving = true, autoSaveMessage = reason) }
            repository.saveGame(
                slotId = 0,
                slotName = "Auto Save",
                locationId = state.currentLocationScene.locationId,
                locationName = state.currentLocationScene.locationId.displayName,
                storyProgressTitle = "Memories: ${state.discoveredMemoryIds.size}/7",
                playTimeSeconds = state.playTimeSeconds,
                empathyScore = state.empathyScore,
                courageScore = state.courageScore,
                playerX = state.playerX,
                playerY = state.playerY,
                currentDialogueId = state.currentDialogue?.id,
                discoveredMemoryIds = state.discoveredMemoryIds,
                inventoryIds = state.inventory.map { it.id }.toSet(),
                completedObjectiveIds = state.completedObjectiveIds,
                choicesMade = state.choicesMade
            )
            delay(1200L)
            _uiState.update { it.copy(isAutoSaving = false, autoSaveMessage = null) }
        }
    }

    fun restartCheckpoint() {
        viewModelScope.launch {
            val autoSave = repository.getSaveSlotById(0) ?: repository.getLatestSaveSlot()
            if (autoSave != null) {
                loadGameFromSlot(autoSave)
                showToast("Checkpoint reloaded.")
            } else {
                val startScene = _uiState.value.currentLocationScene
                _uiState.update {
                    it.copy(
                        currentScreen = AppScreen.PLAYING,
                        playerX = startScene.playerStartX,
                        playerY = startScene.playerStartY,
                        currentDialogue = null
                    )
                }
                showToast("Restarted scene.")
            }
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            for (i in 0..5) {
                repository.deleteSaveSlot(i)
            }
            startNewGame()
            showToast("All progress reset.")
        }
    }

    fun updateSettings(newSettings: GameSettings) {
        viewModelScope.launch {
            repository.saveSettings(newSettings)
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(toastNotification = message) }
            delay(2500L)
            _uiState.update { it.copy(toastNotification = null) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.stop()
        playTimerJob?.cancel()
        dialogueTypewriterJob?.cancel()
    }
}
