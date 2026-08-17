package com.example.story

import com.example.data.*

object StoryData {

    val ALL_MEMORIES = listOf(
        MemoryFragment(
            id = "MEM_01",
            orderIndex = 1,
            title = "The Rooftop Vow",
            location = "Nocturne Station (Platform 4)",
            dateStamp = "August 14, 2021",
            previewText = "Two cups of warm tea on a damp concrete ledge as rain drummed against the roof.",
            fullMemoryStory = "Meera leaned against the railing, clutching her camera. 'If we ever get separated in this maze of a city, Aarav,' she whispered with a bittersweet smile, 'promise you won't let our memories fade into the rain. Some things are too precious to leave unsaved.' We shared an umbrella and made a vow that night.",
            isCrucial = true,
            audioMemoText = "'Aarav... if you're hearing this, don't let the shadows convince you I'm gone. Follow the railway lights.'"
        ),
        MemoryFragment(
            id = "MEM_02",
            orderIndex = 2,
            title = "The Busker's Melody",
            location = "Rainy City Avenue",
            dateStamp = "September 2, 2021",
            previewText = "A street musician playing a melancholic acoustic arpeggio beneath a flickering lamppost.",
            fullMemoryStory = "Meera had stopped in her tracks, pulling my jacket sleeve. She closed her eyes to listen to the busker's melody. 'Some tunes feel like memories you haven't lived yet,' she said softly. I bought the cassette tape from the busker and kept it in my pocket ever since.",
            isCrucial = false,
            audioMemoText = "'Track 2... the melody we listened to when it poured. It still plays in the back of my mind.'"
        ),
        MemoryFragment(
            id = "MEM_03",
            orderIndex = 3,
            title = "The Unsent Letter",
            location = "Cafe Nocturne (Booth #4)",
            dateStamp = "October 3, 2022",
            previewText = "Ink-stained parchment folded into thirds, tucked beneath a vintage ceramic saucer.",
            fullMemoryStory = "'Dear Aarav, I don't know how to explain what is happening to me. The darkness has been whispering from the shadows of the subway. If one day you look at me and the light in my eyes is gone, please know that loving you was never a mistake.' The letter was never mailed.",
            isCrucial = true,
            audioMemoText = "'I wanted to tell you that evening at the cafe, but fear held my tongue. Please forgive me, Aarav.'"
        ),
        MemoryFragment(
            id = "MEM_04",
            orderIndex = 4,
            title = "Promise Under the Neon",
            location = "Cafe Nocturne (Counter)",
            dateStamp = "November 11, 2022",
            previewText = "The buzzing amber neon sign reflecting in two untouched cappuccino cups.",
            fullMemoryStory = "Meera laughed as steam rose between us. 'Let's promise that whatever tomorrow brings, we won't let our story be deleted. Even if the entire world forgets, we keep each other alive.' We clinked our cups. I thought we had eternity.",
            isCrucial = false,
            audioMemoText = "'The cafe was always our safe haven from the storm. But even safe havens crumble when the Keeper awakens.'"
        ),
        MemoryFragment(
            id = "MEM_05",
            orderIndex = 5,
            title = "The Catacomb Passage",
            location = "Subway Catacombs & Depot",
            dateStamp = "December 24, 2022",
            previewText = "A dropped sketchbook covered in charcoal drawings of a towering horned shadow.",
            fullMemoryStory = "Meera was running through these flooded maintenance tunnels. The creature—The Keeper—stalked her footsteps through the steam pipes. In her final moments before the rift swallowed her, she hid her pendant inside the electrical switchboard for Aarav to find.",
            isCrucial = true,
            audioMemoText = "'It's hunting me through the tunnels, Aarav! Don't look into its red eyes! Stay low and keep quiet!'"
        ),
        MemoryFragment(
            id = "MEM_06",
            orderIndex = 6,
            title = "The Frozen Locket",
            location = "Station Clocktower (Track 1)",
            dateStamp = "January 19, 2023",
            previewText = "A cracked brass locket discovered inside the gears of the frozen clock at 07:18.",
            fullMemoryStory = "Inside the locket was a photograph of Meera smiling at the summer festival. On the back, her handwriting in faded ink: 'Save me from the silence.' The station clock stopped the exact minute the express departed into the fog.",
            isCrucial = false,
            audioMemoText = "'07:18... that was the hour the world stopped moving forward. Can you restart the gears for us, Aarav?'"
        ),
        MemoryFragment(
            id = "MEM_07",
            orderIndex = 7,
            title = "The Unsaved Truth",
            location = "The Memory Archive Core",
            dateStamp = "March 30, 2023",
            previewText = "The central obsidian crystal revealing the profound reality of Nocturne.",
            fullMemoryStory = "In the archive's core, the truth crystal reveals everything: Nocturne is not a physical city, but the metaphysical boundary of trapped grief. Meera didn't leave Aarav—she was caught in a coma following an accident, and Aarav descended into this realm of memories to anchor her back to life. The Keeper is the personification of Aarav's own despair attempting to erase the pain by destroying their memories.",
            isCrucial = true,
            audioMemoText = "'Aarav... you came for me. You walked through the dark to find me. Now, make your choice.'"
        )
    )

    val ALL_ACHIEVEMENTS = listOf(
        Achievement("AWAKENED", "Awakened in the Rain", "Begin Aarav's journey at Nocturne Platform 4.", "Story"),
        Achievement("UNSENT_MESSAGE", "The Unsent Message", "Discover Meera's draft: 'I'm still waiting for you.'", "Story"),
        Achievement("FIRST_SHARD", "Fragments of the Past", "Discover your first Memory Fragment.", "Exploration"),
        Achievement("STEALTH_SURVIVOR", "Shadow Walker", "Successfully evade The Keeper by crouching or hiding in a locker.", "Stealth"),
        Achievement("CIRCUIT_BREAKER", "Illuminating Shadows", "Restore power to the station breaker junction.", "Puzzle"),
        Achievement("CAFE_NOCTURNE", "Echoes of Coffee & Rain", "Unlock the safe dial inside Cafe Nocturne.", "Story"),
        Achievement("RADIO_FREQUENCY", "Decrypted Signals", "Tune the emergency frequency to 94.7 MHz.", "Puzzle"),
        Achievement("STEAM_EQUALIZER", "Pressure Under Threat", "Equalize the catacomb steam valves while The Keeper stalks.", "Puzzle"),
        Achievement("TICKING_HOURS", "Frozen at 07:18", "Align the Grand Clock gears to unlock Track 1.", "Puzzle"),
        Achievement("ARCHIVE_SEEKER", "Sanctuary of Lost Souls", "Enter the Core of the Memory Archive.", "Story"),
        Achievement("COLLECTOR_OF_TEARS", "Every Moment Saved", "Find all 7 Memory Fragments in a single journey.", "Mastery"),
        Achievement("ENDING_A", "The Waking Dawn", "Accept the past and step into the light of a new day.", "Ending"),
        Achievement("ENDING_B", "The Eternal Echo", "Choose to remain in the sanctuary of memories forever.", "Ending"),
        Achievement("ENDING_C", "Unbroken Horizon", "The True Ending: Save Meera's soul and unlock the path to UNSAVED 2.", "Ending")
    )

    val ALL_ITEMS = listOf(
        InventoryItem(
            id = "ITEM_PHONE",
            name = "Meera's Cracked Smartphone",
            category = ItemCategory.DOCUMENT,
            description = "A vintage smartphone with a spiderweb crack across the screen. Still receives emergency radio pings.",
            inspectionNotes = "The unsent outbox has a draft to Aarav: 'I'm still waiting for you at the station. Don't let them erase what we had.'",
            iconName = "ic_phone",
            audioTranscript = "Draft voice memo: 'Aarav... if you find this phone, remember the cafe dial combination is 2-4-8-1.'"
        ),
        InventoryItem(
            id = "ITEM_PENDANT",
            name = "Broken Quartz Pendant",
            category = ItemCategory.KEEPSAKE,
            description = "A luminescent crystal pendant that pulses with warm cyan light near memory shards.",
            inspectionNotes = "Meera gave this to Aarav on the night of the rainstorm. It vibrates when Truth is near.",
            iconName = "ic_pendant"
        ),
        InventoryItem(
            id = "ITEM_NOTEBOOK",
            name = "Meera's Sketchbook & Notes",
            category = ItemCategory.DOCUMENT,
            description = "A worn leather journal filled with charcoal sketches of Nocturne and electrical schematics.",
            inspectionNotes = "Page 12: 'Station breaker fuse sequence: RED and AMBER engaged, AZURE grounded, JADE active.'",
            iconName = "ic_notebook"
        ),
        InventoryItem(
            id = "ITEM_TAPE_1",
            name = "Cassette Tape: 'The Busker's Melody'",
            category = ItemCategory.AUDIO_TAPE,
            description = "A vintage magnetic tape labeled in blue ink: 'Aarav & Meera - Autumn 2021'.",
            inspectionNotes = "Can be played in the portable tape recorder to soothe restless spirits.",
            iconName = "ic_tape",
            audioTranscript = "A gentle, nostalgic acoustic guitar melody that pierces through the sound of falling rain."
        ),
        InventoryItem(
            id = "ITEM_CAFE_KEY",
            name = "Brass Turnstile Key",
            category = ItemCategory.KEY_ITEM,
            description = "An ornate brass key retrieved after restoring platform power.",
            inspectionNotes = "Unlocks the security gate leading from Platform 4 to Rainy City Avenue.",
            iconName = "ic_key"
        ),
        InventoryItem(
            id = "ITEM_CATACOMB_CARD",
            name = "Catacombs Access Card",
            category = ItemCategory.KEY_ITEM,
            description = "A magnetic keycard found inside the Cafe Nocturne dial safe.",
            inspectionNotes = "Grants entry to the subterranean maintenance depot and catacombs.",
            iconName = "ic_card"
        ),
        InventoryItem(
            id = "ITEM_CLOCK_GEAR",
            name = "Chrono Brass Gear",
            category = ItemCategory.PUZZLE_TOOL,
            description = "A heavy brass gear recovered from the steam equalizer console.",
            inspectionNotes = "Used to align the Clocktower mechanism at Track 1 to 07:18.",
            iconName = "ic_gear"
        )
    )

    val ALL_STORY_OBJECTIVES = listOf(
        Objective(
            id = "OBJ_1",
            actIndex = 1,
            title = "Awaken & Find Meera's Trail",
            description = "Search the bench on Platform 4 for Meera's phone and umbrella.",
            targetHint = "Inspect the wooden bench near your spawn point."
        ),
        Objective(
            id = "OBJ_2",
            actIndex = 1,
            title = "Restore Platform Power",
            description = "Align the circuit breakers on the wall junction to power the exit gate.",
            targetHint = "Follow Meera's journal notes: Red & Amber live, Azure grounded, Jade engaged."
        ),
        Objective(
            id = "OBJ_3",
            actIndex = 1,
            title = "Relive the First Memory",
            description = "Touch the resonant memory shard near the station gate.",
            targetHint = "Commune with the cyan floating crystal on the platform."
        ),
        Objective(
            id = "OBJ_4",
            actIndex = 2,
            title = "Investigate the City Avenue & Cafe",
            description = "Explore Rainy City Avenue, unlock Cafe Nocturne, and search for Meera's clues.",
            targetHint = "Enter Cafe Nocturne and solve the counter dial safe (Code: 2-4-8-1)."
        ),
        Objective(
            id = "OBJ_5",
            actIndex = 2,
            title = "Tune the Emergency Radio",
            description = "Enter Apartment 404 and tune the radio frequency to 94.7 MHz.",
            targetHint = "Decode the broadcast from the Unknown Voice on the apartment radio desk."
        ),
        Objective(
            id = "OBJ_6",
            actIndex = 3,
            title = "Survive the Catacombs & Evade The Keeper",
            description = "Descend into the maintenance depot. Use crouch and lockers to avoid The Keeper.",
            targetHint = "Crouch under low pipes and hide inside lockers when heartbeat audio accelerates."
        ),
        Objective(
            id = "OBJ_7",
            actIndex = 3,
            title = "Equalize Steam Valves",
            description = "Align the 4 pressure valves to bypass the sealed subway bulkhead.",
            targetHint = "Balance steam needles to the green safety zone."
        ),
        Objective(
            id = "OBJ_8",
            actIndex = 4,
            title = "Unlock the Grand Clocktower",
            description = "Insert the brass gear and align the hands to 07:18.",
            targetHint = "Solve the clock mechanism on Track 1."
        ),
        Objective(
            id = "OBJ_9",
            actIndex = 4,
            title = "Discover the True Purpose of Nocturne",
            description = "Enter the Core Memory Archive and commune with the truth shard.",
            targetHint = "Listen to The Archivist and assemble the 7 memory shards."
        ),
        Objective(
            id = "OBJ_10",
            actIndex = 5,
            title = "The Final Decision at Dawn",
            description = "Ascend to Hilltop Overlook and decide the fate of Aarav and Meera.",
            targetHint = "Confront The Keeper and choose: Release, Preserve, or True Restoration."
        )
    )

    val INITIAL_PHONE_MESSAGES = listOf(
        PhoneMessage(
            id = "MSG_01",
            sender = "Meera",
            timeStamp = "07:14 PM",
            messageText = "Aarav, they are closing the gates. Meet me at Platform 4 before the express departs. Please hurry."
        ),
        PhoneMessage(
            id = "MSG_02",
            sender = "Unknown Voice",
            timeStamp = "07:17 PM",
            messageText = "Do not board that train, Aarav. What waits on the other side of the fog is not your reality."
        ),
        PhoneMessage(
            id = "MSG_03",
            sender = "Meera (Draft - Unsent)",
            timeStamp = "07:18 PM",
            messageText = "I'm still waiting for you. If the Keeper comes for me, look for my sketchpad in Cafe Nocturne. The code is 2-4-8-1."
        )
    )
}
