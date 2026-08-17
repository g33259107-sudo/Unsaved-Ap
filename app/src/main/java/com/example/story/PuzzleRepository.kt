package com.example.story

import com.example.data.*

object PuzzleRepository {

    private val puzzles = mapOf(
        "PUZZLE_FUSE_1" to PuzzleData(
            id = "PUZZLE_FUSE_1",
            title = "Station Circuit Breaker Junction",
            locationName = "Nocturne Station (Platform 4)",
            promptInstructions = "Restore the auxiliary lighting to power the station turnstile gate. Meera's note hints: 'RED and AMBER live, AZURE grounded, JADE active.'",
            type = PuzzleType.FUSE_CIRCUIT,
            switches = listOf(
                PuzzleSwitch(0, "Red Circuit (Primary)", isOn = false),
                PuzzleSwitch(1, "Amber Circuit (Lighting)", isOn = false),
                PuzzleSwitch(2, "Azure Circuit (Grounding)", isOn = true),
                PuzzleSwitch(3, "Jade Circuit (Gate Solenoid)", isOn = false)
            ),
            targetSwitchStates = listOf(true, true, false, true),
            solutionDescription = "Engage Red, Amber, and Jade circuits while leaving Azure off.",
            rewardItemId = "ITEM_CAFE_KEY",
            unlocksDoorToLocation = LocationId.RAINY_AVENUE,
            rewardMemoryId = "MEM_01"
        ),

        "PUZZLE_COFFEE_2" to PuzzleData(
            id = "PUZZLE_COFFEE_2",
            title = "Cafe Nocturne Vintage Safe Dial",
            locationName = "Cafe Nocturne (Counter)",
            promptInstructions = "Turn the 4 rotary tumbler dials to match the passcode left in Meera's phone draft (2-4-8-1).",
            type = PuzzleType.SAFE_COMBINATION,
            dialCurrentDigits = listOf(0, 0, 0, 0),
            dialTargetDigits = listOf(2, 4, 8, 1),
            solutionDescription = "Dial combination 2-4-8-1 unlocks the drawer containing the Catacombs Access Card.",
            rewardItemId = "ITEM_CATACOMB_CARD",
            unlocksDoorToLocation = LocationId.CATACOMBS_DEPOT,
            rewardMemoryId = "MEM_03"
        ),

        "PUZZLE_RADIO_3" to PuzzleData(
            id = "PUZZLE_RADIO_3",
            title = "Emergency Radio Frequency Broadcast",
            locationName = "Apartment 404 (Radio Desk)",
            promptInstructions = "Adjust the analog frequency knob to tune into the broadcast from the Unknown Voice at 94.7 MHz.",
            type = PuzzleType.AUDIO_FREQUENCY,
            currentFrequency = 88.0f,
            targetFrequency = 94.7f,
            solutionDescription = "Frequency tuned to 94.7 MHz: 'Aarav... the Keeper is waiting in the maintenance depot. Take the cassette tape and stay in the shadows.'",
            rewardItemId = "ITEM_TAPE_1",
            rewardMemoryId = "MEM_04"
        ),

        "PUZZLE_STEAM_4" to PuzzleData(
            id = "PUZZLE_STEAM_4",
            title = "Catacomb Steam Pressure Equalizer",
            locationName = "Subway Catacombs & Depot",
            promptInstructions = "Align the 4 industrial steam valves to release boiler pressure from the sealed bulkhead door before The Keeper corners you.",
            type = PuzzleType.PRESSURE_VALVES,
            switches = listOf(
                PuzzleSwitch(0, "Valve 1 (Main Header)", isOn = false),
                PuzzleSwitch(1, "Valve 2 (Auxiliary Exhaust)", isOn = true),
                PuzzleSwitch(2, "Valve 3 (Bypass Vent)", isOn = false),
                PuzzleSwitch(3, "Valve 4 (Turbine Return)", isOn = false)
            ),
            targetSwitchStates = listOf(true, false, true, true),
            solutionDescription = "Engage Valves 1, 3, and 4 while closing Valve 2 to bypass steam pipes.",
            rewardItemId = "ITEM_CLOCK_GEAR",
            unlocksDoorToLocation = LocationId.STATION_TRACKS,
            rewardMemoryId = "MEM_05"
        ),

        "PUZZLE_CLOCK_3" to PuzzleData(
            id = "PUZZLE_CLOCK_3",
            title = "The Grand Clock Mechanism (07:18)",
            locationName = "Station Clocktower (Track 1)",
            promptInstructions = "Insert the brass gear and align the hour and minute hands to the fateful departure moment: 07:18.",
            type = PuzzleType.CLOCK_GEARS,
            dialCurrentDigits = listOf(1, 2, 0, 0),
            dialTargetDigits = listOf(0, 7, 1, 8),
            solutionDescription = "Setting the clock hands to 07:18 activates the temporal archway leading to the Core Memory Archive.",
            unlocksDoorToLocation = LocationId.MEMORY_ARCHIVE,
            rewardMemoryId = "MEM_06"
        )
    )

    fun getPuzzle(id: String): PuzzleData? {
        return puzzles[id]
    }
}
