package com.example.story

import com.example.R
import com.example.data.EntityType
import com.example.data.InteractiveEntity
import com.example.data.LocationId

data class LocationScene(
    val locationId: LocationId,
    val backgroundRes: Int,
    val worldWidth: Float = 1400f,
    val worldHeight: Float = 1200f,
    val playerStartX: Float = 250f,
    val playerStartY: Float = 600f,
    val entities: List<InteractiveEntity> = emptyList(),
    val ambientMood: String = "rain",
    val musicMood: String = "mystery"
)

object LocationSceneRepository {

    fun getScene(locationId: LocationId): LocationScene {
        return when (locationId) {
            LocationId.NOCTURNE_STATION -> LocationScene(
                locationId = LocationId.NOCTURNE_STATION,
                backgroundRes = R.drawable.img_menu_cover,
                worldWidth = 1500f,
                worldHeight = 1200f,
                playerStartX = 200f,
                playerStartY = 650f,
                entities = listOf(
                    InteractiveEntity(
                        id = "ENT_1_BENCH",
                        name = "Platform Bench (Phone & Umbrella)",
                        x = 260f,
                        y = 480f,
                        radius = 65f,
                        promptText = "Examine Bench",
                        type = EntityType.INTERACTABLE,
                        targetDialogueId = "C1_BENCH"
                    ),
                    InteractiveEntity(
                        id = "ENT_1_IRIS",
                        name = "Iris Chen",
                        x = 640f,
                        y = 560f,
                        radius = 70f,
                        promptText = "Speak with Iris",
                        type = EntityType.NPC,
                        targetDialogueId = "C1_IRIS_1"
                    ),
                    InteractiveEntity(
                        id = "ENT_1_FUSE",
                        name = "Station Breaker Junction",
                        x = 440f,
                        y = 350f,
                        radius = 65f,
                        promptText = "Restore Power",
                        type = EntityType.PUZZLE_STATION,
                        targetPuzzleId = "PUZZLE_FUSE_1"
                    ),
                    InteractiveEntity(
                        id = "ENT_1_MEM1",
                        name = "Resonant Shard (The Rooftop Vow)",
                        x = 1020f,
                        y = 380f,
                        radius = 60f,
                        promptText = "Commune with Shard",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_01"
                    ),
                    InteractiveEntity(
                        id = "ENT_1_GATE",
                        name = "Gate to City Avenue",
                        x = 1360f,
                        y = 650f,
                        radius = 75f,
                        promptText = "Unlock Gate",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "C1_GATE",
                        requiredItemId = "ITEM_CAFE_KEY"
                    )
                ),
                ambientMood = "rain",
                musicMood = "mystery"
            )

            LocationId.RAINY_AVENUE -> LocationScene(
                locationId = LocationId.RAINY_AVENUE,
                backgroundRes = R.drawable.img_menu_cover,
                worldWidth = 1500f,
                worldHeight = 1200f,
                playerStartX = 180f,
                playerStartY = 600f,
                entities = listOf(
                    InteractiveEntity(
                        id = "ENT_AV_BUSKER",
                        name = "Echo of the Busker",
                        x = 480f,
                        y = 480f,
                        radius = 65f,
                        promptText = "Listen to Melody",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_02"
                    ),
                    InteractiveEntity(
                        id = "ENT_AV_NEWSPAPER",
                        name = "Rain-Slicked Newspaper Stand",
                        x = 750f,
                        y = 700f,
                        radius = 60f,
                        promptText = "Read Headlines",
                        type = EntityType.INTERACTABLE,
                        targetDialogueId = "AV_NEWSPAPER"
                    ),
                    InteractiveEntity(
                        id = "ENT_AV_APT",
                        name = "Apartment 404 Entrance",
                        x = 980f,
                        y = 360f,
                        radius = 65f,
                        promptText = "Enter Apartment",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "AV_APT_ENTER"
                    ),
                    InteractiveEntity(
                        id = "ENT_AV_CAFE",
                        name = "Cafe Nocturne Entrance",
                        x = 1320f,
                        y = 600f,
                        radius = 75f,
                        promptText = "Enter Cafe Nocturne",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "C2_DOOR"
                    )
                ),
                ambientMood = "rain",
                musicMood = "exploration"
            )

            LocationId.CAFE_NOCTURNE -> LocationScene(
                locationId = LocationId.CAFE_NOCTURNE,
                backgroundRes = R.drawable.img_chapter_memory,
                worldWidth = 1400f,
                worldHeight = 1100f,
                playerStartX = 200f,
                playerStartY = 550f,
                entities = listOf(
                    InteractiveEntity(
                        id = "ENT_2_ELENA",
                        name = "Echo of Elena",
                        x = 700f,
                        y = 450f,
                        radius = 70f,
                        promptText = "Talk to Elena",
                        type = EntityType.NPC,
                        targetDialogueId = "C2_ELENA_1"
                    ),
                    InteractiveEntity(
                        id = "ENT_2_GRINDER",
                        name = "Vintage Coffee Lockbox",
                        x = 450f,
                        y = 750f,
                        radius = 60f,
                        promptText = "Solve Lockbox",
                        type = EntityType.PUZZLE_STATION,
                        targetPuzzleId = "PUZZLE_COFFEE_2"
                    ),
                    InteractiveEntity(
                        id = "ENT_2_MEM3",
                        name = "Unsent Shard (Memory #3)",
                        x = 950f,
                        y = 350f,
                        radius = 55f,
                        promptText = "Read Memory Shard",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_03"
                    ),
                    InteractiveEntity(
                        id = "ENT_2_MEM4",
                        name = "Neon Shard (Memory #4)",
                        x = 1100f,
                        y = 700f,
                        radius = 55f,
                        promptText = "Inspect Memory",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_04"
                    ),
                    InteractiveEntity(
                        id = "ENT_2_EXIT",
                        name = "Exit to Train Tracks",
                        x = 1260f,
                        y = 550f,
                        radius = 70f,
                        promptText = "Head to Tracks",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "C3_STATION_ENTER"
                    )
                ),
                ambientMood = "cozy_rain",
                musicMood = "romance"
            )

            LocationId.APARTMENT_404 -> LocationScene(
                locationId = LocationId.APARTMENT_404,
                backgroundRes = R.drawable.img_chapter_memory,
                worldWidth = 1300f,
                worldHeight = 1100f,
                playerStartX = 220f,
                playerStartY = 580f,
                entities = listOf(
                    InteractiveEntity(
                        id = "ENT_APT_GUITAR",
                        name = "Meera's Acoustic Guitar",
                        x = 520f,
                        y = 420f,
                        radius = 65f,
                        promptText = "Strum Strings",
                        type = EntityType.INTERACTABLE,
                        targetDialogueId = "APT_GUITAR"
                    ),
                    InteractiveEntity(
                        id = "ENT_APT_ROOFTOP",
                        name = "Rooftop Rain Overlook",
                        x = 880f,
                        y = 650f,
                        radius = 65f,
                        promptText = "Look Out at Rain",
                        type = EntityType.INTERACTABLE,
                        targetDialogueId = "APT_ROOFTOP"
                    ),
                    InteractiveEntity(
                        id = "ENT_APT_EXIT",
                        name = "Return to City Avenue",
                        x = 1180f,
                        y = 580f,
                        radius = 70f,
                        promptText = "Leave Apartment",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "APT_LEAVE"
                    )
                ),
                ambientMood = "cozy_rain",
                musicMood = "emotional"
            )

            LocationId.CATACOMBS_DEPOT -> LocationScene(
                locationId = LocationId.CATACOMBS_DEPOT,
                backgroundRes = R.drawable.img_memory_subway,
                worldWidth = 1600f,
                worldHeight = 1300f,
                playerStartX = 200f,
                playerStartY = 650f,
                entities = listOf(
                    InteractiveEntity(
                        id = "ENT_CATA_STEAM",
                        name = "Steam Pressure Equalizer",
                        x = 700f,
                        y = 500f,
                        radius = 65f,
                        promptText = "Equalize Steam",
                        type = EntityType.PUZZLE_STATION,
                        targetPuzzleId = "PUZZLE_STEAM_4"
                    ),
                    InteractiveEntity(
                        id = "ENT_CATA_MEM5",
                        name = "Memory Shard #5 (The Final Call)",
                        x = 950f,
                        y = 600f,
                        radius = 60f,
                        promptText = "Recover Memory",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_05"
                    ),
                    InteractiveEntity(
                        id = "ENT_CATA_EXIT",
                        name = "Clocktower Maintenance Gate",
                        x = 1400f,
                        y = 650f,
                        radius = 70f,
                        promptText = "Enter Track 1",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "C3_TRACKS_1"
                    )
                ),
                ambientMood = "dripping_echo",
                musicMood = "suspense"
            )

            LocationId.STATION_TRACKS -> LocationScene(
                locationId = LocationId.STATION_TRACKS,
                backgroundRes = R.drawable.img_memory_subway,
                worldWidth = 1500f,
                worldHeight = 1200f,
                playerStartX = 200f,
                playerStartY = 600f,
                entities = listOf(
                    InteractiveEntity(
                        id = "ENT_3_REN",
                        name = "Echo of Iris",
                        x = 650f,
                        y = 600f,
                        radius = 65f,
                        promptText = "Speak with Iris",
                        type = EntityType.NPC,
                        targetDialogueId = "C3_REN_1"
                    ),
                    InteractiveEntity(
                        id = "ENT_3_CLOCK",
                        name = "Grand Station Clock",
                        x = 1050f,
                        y = 400f,
                        radius = 65f,
                        promptText = "Align Clock Gears",
                        type = EntityType.PUZZLE_STATION,
                        targetPuzzleId = "PUZZLE_CLOCK_3"
                    ),
                    InteractiveEntity(
                        id = "ENT_3_MEM5",
                        name = "Crossing Shard (Memory #5)",
                        x = 450f,
                        y = 850f,
                        radius = 55f,
                        promptText = "Inspect Shard",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_05"
                    ),
                    InteractiveEntity(
                        id = "ENT_3_MEM6",
                        name = "Locket Shard (Memory #6)",
                        x = 1350f,
                        y = 400f,
                        radius = 55f,
                        promptText = "Recover Memory",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_06"
                    ),
                    InteractiveEntity(
                        id = "ENT_3_PORTAL",
                        name = "Gateway to Archive",
                        x = 1400f,
                        y = 750f,
                        radius = 70f,
                        promptText = "Enter Archive",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "C4_ARCHIVE_ENTER"
                    )
                ),
                ambientMood = "mist",
                musicMood = "mystery"
            )

            LocationId.MEMORY_ARCHIVE -> LocationScene(
                locationId = LocationId.MEMORY_ARCHIVE,
                backgroundRes = R.drawable.img_chapter_memory,
                worldWidth = 1400f,
                worldHeight = 1200f,
                playerStartX = 250f,
                playerStartY = 600f,
                entities = listOf(
                    InteractiveEntity(
                        id = "ENT_4_ARCHIVIST",
                        name = "The Archivist",
                        x = 650f,
                        y = 500f,
                        radius = 70f,
                        promptText = "Speak with Archivist",
                        type = EntityType.NPC,
                        targetDialogueId = "C4_ARCHIVE_ENTER"
                    ),
                    InteractiveEntity(
                        id = "ENT_4_MATRIX",
                        name = "Memory Altar Matrix",
                        x = 1000f,
                        y = 500f,
                        radius = 65f,
                        promptText = "Reconstruct Memory",
                        type = EntityType.PUZZLE_STATION,
                        targetPuzzleId = "PUZZLE_MATRIX_4"
                    ),
                    InteractiveEntity(
                        id = "ENT_4_MEM7",
                        name = "Crucial Shard (Memory #7)",
                        x = 1000f,
                        y = 800f,
                        radius = 60f,
                        promptText = "Embrace Truth",
                        type = EntityType.MEMORY_SHARD,
                        targetMemoryId = "MEM_07"
                    ),
                    InteractiveEntity(
                        id = "ENT_4_SUMMIT_DOOR",
                        name = "Path to Hilltop Overlook",
                        x = 1300f,
                        y = 600f,
                        radius = 70f,
                        promptText = "Ascend to Summit",
                        type = EntityType.DOOR_PORTAL,
                        targetDialogueId = "C5_OVERLOOK_1"
                    )
                ),
                ambientMood = "ethereal",
                musicMood = "suspense"
            )

            LocationId.HILLTOP_OVERLOOK -> LocationScene(
                locationId = LocationId.HILLTOP_OVERLOOK,
                backgroundRes = R.drawable.img_ending_stars,
                worldWidth = 1300f,
                worldHeight = 1100f,
                playerStartX = 250f,
                playerStartY = 550f,
                entities = listOf(
                    InteractiveEntity(
                        id = "ENT_5_ELENA_FINAL",
                        name = "Elena at the Edge of Dawn",
                        x = 850f,
                        y = 550f,
                        radius = 80f,
                        promptText = "Confront Your Heart",
                        type = EntityType.NPC,
                        targetDialogueId = "C5_ELENA_FINAL"
                    )
                ),
                ambientMood = "wind_stars",
                musicMood = "ending"
            )
        }
    }
}

