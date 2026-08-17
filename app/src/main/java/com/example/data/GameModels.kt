package com.example.data

enum class LocationId(val displayName: String, val ambientMood: String) {
    NOCTURNE_STATION("Nocturne Station (Platform 4)", "Rainy, desolate train platform frozen in time"),
    RAINY_AVENUE("Rainy City Avenue", "Neon-lit alleyways and damp cobblestone streets"),
    CAFE_NOCTURNE("Cafe Nocturne", "Warm, nostalgic cafe holding echoes of the past"),
    APARTMENT_404("Apartment 404 & Rooftop", "The empty studio apartment overlooking the rainy skyline"),
    CATACOMBS_DEPOT("Subway Catacombs & Depot", "Claustrophobic rusted tunnels where The Keeper lurks"),
    STATION_TRACKS("Clocktower & Track 1", "Misty railway terminus stuck at 07:18"),
    MEMORY_ARCHIVE("The Core Memory Archive", "Ethereal archive of floating luminous fragments"),
    HILLTOP_OVERLOOK("Hilltop Overlook", "Cosmic starry cliffside at the edge of dawn")
}

data class MemoryFragment(
    val id: String,
    val orderIndex: Int,
    val title: String,
    val location: String,
    val dateStamp: String,
    val previewText: String,
    val fullMemoryStory: String,
    val isCrucial: Boolean = false,
    val isUnlocked: Boolean = false,
    val audioMemoText: String = ""
)

enum class ItemCategory {
    KEY_ITEM,
    KEEPSAKE,
    DOCUMENT,
    PUZZLE_TOOL,
    AUDIO_TAPE
}

data class InventoryItem(
    val id: String,
    val name: String,
    val category: ItemCategory,
    val description: String,
    val inspectionNotes: String,
    val iconName: String,
    val isUsed: Boolean = false,
    val audioTranscript: String? = null
)

data class Objective(
    val id: String,
    val actIndex: Int, // 1 to 5
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val isOptional: Boolean = false,
    val targetHint: String = ""
)

enum class CharacterEmotion {
    NEUTRAL,
    MELANCHOLY,
    PENSIVE,
    WARM,
    SURPRISED,
    TEARFUL,
    DETERMINED,
    SHADOWY,
    TERRIFIED
}

data class DialogueChoice(
    val choiceId: String,
    val text: String,
    val empathyDelta: Int = 0,
    val courageDelta: Int = 0,
    val nextDialogueId: String? = null,
    val unlockedMemoryId: String? = null,
    val grantItemId: String? = null
)

data class DialogueLine(
    val id: String,
    val speaker: String,
    val text: String,
    val emotion: CharacterEmotion = CharacterEmotion.NEUTRAL,
    val soundMood: String = "mystery",
    val choices: List<DialogueChoice> = emptyList(),
    val nextLineId: String? = null,
    val triggersCutsceneId: String? = null,
    val triggersPuzzleId: String? = null
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val isUnlocked: Boolean = false,
    val unlockedDate: String? = null
)

data class GameSettings(
    val masterVolume: Float = 1.0f,
    val musicVolume: Float = 0.8f,
    val sfxVolume: Float = 0.9f,
    val ambienceVolume: Float = 0.7f,
    val voiceVolume: Float = 0.85f,
    val graphicsQuality: String = "High",
    val textSpeed: Int = 2, // 1: Slow, 2: Normal, 3: Fast, 4: Instant
    val joystickSensitivity: Float = 1.0f,
    val hapticFeedback: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val puzzleHints: Boolean = true,
    val cinematicSubtitles: Boolean = true,
    val subtitlesEnabled: Boolean = true,
    val language: String = "English"
)

enum class EndingType(val title: String, val subtitle: String, val description: String) {
    ENDING_A(
        title = "Ending 1: The Waking Dawn",
        subtitle = "Normal Ending",
        description = "Aarav accepts the sorrow and steps into the dawn alone. The rain ceases over the city as he returns to reality, carrying Meera's memory peacefully in his heart."
    ),
    ENDING_B(
        title = "Ending 2: The Eternal Echo",
        subtitle = "Bittersweet Ending",
        description = "Aarav refuses to let go of the past, choosing to remain in the timeless realm of Nocturne beside Meera's echo, caught in an eternal, gentle night."
    ),
    ENDING_C(
        title = "Ending 3: Unbroken Horizon",
        subtitle = "True Ending (Requires all 7 Memories)",
        description = "By recovering all 7 memory shards and restoring the lost message, Aarav frees Meera's soul and banishes The Keeper forever. Their bond transcends time, setting up the journey for UNSAVED 2."
    )
}

data class InteractiveEntity(
    val id: String,
    val name: String,
    val x: Float,
    val y: Float,
    val radius: Float = 45f,
    val promptText: String = "Inspect",
    val type: EntityType = EntityType.INTERACTABLE,
    val targetDialogueId: String? = null,
    val targetPuzzleId: String? = null,
    val targetMemoryId: String? = null,
    val requiredItemId: String? = null,
    val isAlreadyUsed: Boolean = false
)

enum class EntityType {
    INTERACTABLE,
    NPC,
    MEMORY_SHARD,
    PUZZLE_STATION,
    DOOR_PORTAL,
    ITEM_PICKUP,
    HIDING_SPOT,
    AUDIO_RECORDER
}

data class PhoneMessage(
    val id: String,
    val sender: String,
    val timeStamp: String,
    val messageText: String,
    val isVoicemail: Boolean = false,
    val isRead: Boolean = false
)

enum class KeeperState {
    IDLE_PATROL,
    INVESTIGATING_NOISE,
    ALERT_SPOTTED,
    PURSUING_CHASE,
    LOST_TARGET
}

enum class PuzzleType {
    FUSE_CIRCUIT,
    SAFE_COMBINATION,
    AUDIO_FREQUENCY,
    PRESSURE_VALVES,
    CLOCK_GEARS,
    MEMORY_MATRIX
}

data class PuzzleSwitch(
    val id: Int,
    val label: String,
    val isOn: Boolean
)

data class PuzzleData(
    val id: String,
    val title: String,
    val locationName: String,
    val promptInstructions: String,
    val type: PuzzleType,
    val switches: List<PuzzleSwitch> = emptyList(),
    val targetSwitchStates: List<Boolean> = emptyList(),
    val dialCurrentDigits: List<Int> = emptyList(),
    val dialTargetDigits: List<Int> = emptyList(),
    val currentFrequency: Float = 0f,
    val targetFrequency: Float = 0f,
    val solutionDescription: String = "",
    val rewardItemId: String? = null,
    val unlocksDoorToLocation: LocationId? = null,
    val rewardMemoryId: String? = null
)
