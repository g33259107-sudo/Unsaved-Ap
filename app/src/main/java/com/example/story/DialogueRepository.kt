package com.example.story

import com.example.data.CharacterEmotion
import com.example.data.DialogueChoice
import com.example.data.DialogueLine

object DialogueRepository {

    private val dialogues = mapOf(
        // ACT 1 — ARRIVAL (Platform 4 & Awakening)
        "C1_INTRO_1" to DialogueLine(
            id = "C1_INTRO_1",
            speaker = "Aarav",
            text = "Cold rain... Heavy droplets drumming against the rusted corrugated steel roof of Platform 4. My head throbs with static.",
            emotion = CharacterEmotion.MELANCHOLY,
            soundMood = "mystery",
            nextLineId = "C1_INTRO_2"
        ),
        "C1_INTRO_2" to DialogueLine(
            id = "C1_INTRO_2",
            speaker = "Aarav",
            text = "A cracked quartz pendant hangs around my neck. Beside me on the bench lies a phone and a damp notebook. How did I get here?",
            emotion = CharacterEmotion.PENSIVE,
            soundMood = "mystery",
            choices = listOf(
                DialogueChoice("C1_CH1_A", "Examine Meera's phone for messages.", empathyDelta = 1, nextDialogueId = "C1_BENCH"),
                DialogueChoice("C1_CH1_B", "Look around the empty platform.", courageDelta = 1, nextDialogueId = "C1_INTRO_3")
            )
        ),
        "C1_INTRO_3" to DialogueLine(
            id = "C1_INTRO_3",
            speaker = "Aarav",
            text = "The station is completely silent except for the rain. A figure in a dark coat stands under the amber lantern ahead.",
            emotion = CharacterEmotion.DETERMINED,
            soundMood = "exploration"
        ),
        "C1_BENCH" to DialogueLine(
            id = "C1_BENCH",
            speaker = "Platform Bench",
            text = "An aged mahogany bench slick with rain. Resting on the wood is a cracked cell phone, a navy umbrella, and a worn leather journal.",
            emotion = CharacterEmotion.PENSIVE,
            soundMood = "mystery",
            choices = listOf(
                DialogueChoice(
                    choiceId = "C1_BENCH_PICKUP",
                    text = "Pick up Meera's phone and journal.",
                    grantItemId = "ITEM_PHONE",
                    empathyDelta = 2,
                    nextDialogueId = "C1_BENCH_2"
                )
            )
        ),
        "C1_BENCH_2" to DialogueLine(
            id = "C1_BENCH_2",
            speaker = "Aarav",
            text = "The screen flickers. There's an unsent draft addressed to me: 'I'm still waiting for you... The code to the cafe safe is 2-4-8-1.' Meera... what happened to you?",
            emotion = CharacterEmotion.TEARFUL,
            soundMood = "emotional",
            nextLineId = "C1_BENCH_3"
        ),
        "C1_BENCH_3" to DialogueLine(
            id = "C1_BENCH_3",
            speaker = "Aarav",
            text = "The umbrella has a silver engraving: 'Stay dry until we meet again.' I need to get through the security gate to the city.",
            emotion = CharacterEmotion.WARM,
            soundMood = "mystery"
        ),
        "C1_IRIS_1" to DialogueLine(
            id = "C1_IRIS_1",
            speaker = "Iris Chen",
            text = "You've returned, Aarav. I wondered if the rain would wash away what was left of your footsteps.",
            emotion = CharacterEmotion.SHADOWY,
            soundMood = "emotional",
            choices = listOf(
                DialogueChoice("C1_IRIS_CH1", "Iris... tell me why we are both trapped in this eternal rain.", empathyDelta = 2, nextDialogueId = "C1_IRIS_2"),
                DialogueChoice("C1_IRIS_CH2", "Where is Meera? Did she take the express train?", courageDelta = 2, nextDialogueId = "C1_IRIS_2")
            )
        ),
        "C1_IRIS_2" to DialogueLine(
            id = "C1_IRIS_2",
            speaker = "Iris Chen",
            text = "This was the last place you saw her. She boarded into the mist, but you held back. Ever since, The Keeper has been erasing every memory left in Nocturne.",
            emotion = CharacterEmotion.MELANCHOLY,
            soundMood = "emotional",
            choices = listOf(
                DialogueChoice("C1_IRIS_CH3", "I will find every shard of her memory, whatever the cost.", empathyDelta = 3, courageDelta = 1, nextDialogueId = "C1_IRIS_3"),
                DialogueChoice("C1_IRIS_CH4", "How do I unlock the gate to the city avenue?", courageDelta = 2, nextDialogueId = "C1_IRIS_3")
            )
        ),
        "C1_IRIS_3" to DialogueLine(
            id = "C1_IRIS_3",
            speaker = "Iris Chen",
            text = "Restore the power junction on the wall. Red and Amber live, Azure grounded, Jade engaged. Take the brass key when the circuits hum. And beware... he is listening.",
            emotion = CharacterEmotion.PENSIVE,
            soundMood = "mystery"
        ),
        "C1_GATE" to DialogueLine(
            id = "C1_GATE",
            speaker = "Platform Gate",
            text = "The heavy steel gate hums as the electromagnetic lock releases. The path to Rainy City Avenue is open.",
            emotion = CharacterEmotion.DETERMINED,
            soundMood = "reveal",
            choices = listOf(
                DialogueChoice("C1_GATE_OPEN", "Step through the gate onto Rainy City Avenue.", nextDialogueId = "ACT2_TRANSITION")
            )
        ),
        "ACT2_TRANSITION" to DialogueLine(
            id = "ACT2_TRANSITION",
            speaker = "Aarav",
            text = "The rain intensifies as I step into the neon-lit avenue. Cafe Nocturne and Apartment 404 loom through the fog.",
            emotion = CharacterEmotion.DETERMINED,
            soundMood = "exploration"
        ),

        // ACT 2 — INVESTIGATION (Avenue, Cafe Nocturne, Apartment 404)
        "AV_APT_ENTER" to DialogueLine(
            id = "AV_APT_ENTER",
            speaker = "Apartment 404 Door",
            text = "The apartment door is unlocked. Inside, the red light of the emergency radio transmitter pulses in the dark.",
            emotion = CharacterEmotion.PENSIVE,
            soundMood = "mystery"
        ),
        "APT_ROOFTOP" to DialogueLine(
            id = "APT_ROOFTOP",
            speaker = "Aarav",
            text = "Looking out from the balcony over the endless nocturnal skyline. Meera and I stood right here, watching the rain fill the street below.",
            emotion = CharacterEmotion.MELANCHOLY,
            soundMood = "emotional"
        ),
        "APT_LEAVE" to DialogueLine(
            id = "APT_LEAVE",
            speaker = "Aarav",
            text = "Returning to the rain-soaked avenue.",
            emotion = CharacterEmotion.NEUTRAL,
            soundMood = "exploration"
        ),
        "C2_DOOR" to DialogueLine(
            id = "C2_DOOR",
            speaker = "Cafe Nocturne Entrance",
            text = "The chime bells ring softly as you push open the cafe door. The scent of roasted beans and damp coats lingers in the air.",
            emotion = CharacterEmotion.WARM,
            soundMood = "romance"
        ),
        "C2_MEERA_1" to DialogueLine(
            id = "C2_MEERA_1",
            speaker = "Echo of Meera",
            text = "You're late, Aarav. Your coffee is getting cold.",
            emotion = CharacterEmotion.WARM,
            soundMood = "romance",
            choices = listOf(
                DialogueChoice("C2_M_CH1", "Meera... is this really you, or just another ghost of yesterday?", empathyDelta = 2, nextDialogueId = "C2_MEERA_2"),
                DialogueChoice("C2_M_CH2", "I found your phone. Why did you leave that draft unsent?", courageDelta = 2, nextDialogueId = "C2_MEERA_2")
            )
        ),
        "C2_MEERA_2" to DialogueLine(
            id = "C2_MEERA_2",
            speaker = "Echo of Meera",
            text = "Because I was terrified that when the Keeper took me, you would forget our promise. The magnetic keycard to the subway catacombs is inside the counter safe. The dial code is 2-4-8-1.",
            emotion = CharacterEmotion.TEARFUL,
            soundMood = "emotional",
            nextLineId = "C2_MEERA_3"
        ),
        "C2_MEERA_3" to DialogueLine(
            id = "C2_MEERA_3",
            speaker = "Echo of Meera",
            text = "Be careful beneath the streets, Aarav. In the catacombs, sound is your worst enemy. If you hear heavy dragging footsteps... crouch low or hide inside the lockers.",
            emotion = CharacterEmotion.TERRIFIED,
            soundMood = "suspense"
        ),
        "CAFE_LEAVE" to DialogueLine(
            id = "CAFE_LEAVE",
            speaker = "Aarav",
            text = "Heading back out into the rainy street.",
            emotion = CharacterEmotion.NEUTRAL,
            soundMood = "exploration"
        ),
        "AV_CATACOMB_ENTER" to DialogueLine(
            id = "AV_CATACOMB_ENTER",
            speaker = "Catacomb Entrance Gate",
            text = "A heavy iron subterranean storm door marked: 'TRANSIT MAINTENANCE DEPOT - AUTHORIZED PERSONNEL ONLY'.",
            emotion = CharacterEmotion.TERRIFIED,
            soundMood = "suspense",
            choices = listOf(
                DialogueChoice("AV_CATACOMB_SWIPE", "Swipe the Catacombs Access Card and descend into the dark.", nextDialogueId = "ACT3_TRANSITION")
            )
        ),

        // ACT 3 — DANGER (Subway Catacombs & The Keeper)
        "ACT3_TRANSITION" to DialogueLine(
            id = "ACT3_TRANSITION",
            speaker = "Aarav",
            text = "The smell of rust and stagnant water fills my lungs. A chilling shriek echoes from the end of the maintenance tunnel... The Keeper is near.",
            emotion = CharacterEmotion.TERRIFIED,
            soundMood = "danger"
        ),
        "C3_STATION_ENTER" to DialogueLine(
            id = "C3_STATION_ENTER",
            speaker = "Maintenance Elevator",
            text = "The steam valves have been bypassed! The maintenance freight elevator opens, leading up to the Clocktower at Track 1.",
            emotion = CharacterEmotion.DETERMINED,
            soundMood = "reveal"
        ),

        // ACT 4 — TRUTH (Clocktower & Memory Archive)
        "C4_ARCHIVE_ENTER" to DialogueLine(
            id = "C4_ARCHIVE_ENTER",
            speaker = "The Archivist",
            text = "You have walked through the deepest labyrinth of your own grief, Aarav. Do you finally comprehend what this place is?",
            emotion = CharacterEmotion.SHADOWY,
            soundMood = "reveal",
            choices = listOf(
                DialogueChoice("C4_ARCH_CH1", "This entire city... it's the boundary where our memories refuse to die.", empathyDelta = 3, nextDialogueId = "C4_ARCH_2"),
                DialogueChoice("C4_ARCH_CH2", "Why did The Keeper try to destroy everything we built?", courageDelta = 3, nextDialogueId = "C4_ARCH_2")
            )
        ),
        "C4_ARCH_2" to DialogueLine(
            id = "C4_ARCH_2",
            speaker = "The Archivist",
            text = "Because The Keeper is the phantom of your own denial. When Meera was taken by the accident, you locked your soul away to avoid the pain of living without her. By collecting these 7 shards, you have rebuilt the bridge between realms.",
            emotion = CharacterEmotion.MELANCHOLY,
            soundMood = "emotional",
            nextLineId = "C4_ARCH_3"
        ),
        "C4_ARCH_3" to DialogueLine(
            id = "C4_ARCH_3",
            speaker = "The Archivist",
            text = "Meera waits at the Hilltop Overlook. Go to her at the edge of dawn. The final choice rests in your hands.",
            emotion = CharacterEmotion.WARM,
            soundMood = "mystery"
        ),
        "C5_OVERLOOK_1" to DialogueLine(
            id = "C5_OVERLOOK_1",
            speaker = "Aarav",
            text = "The rain has finally stopped. The cold night wind carries the fragrance of blooming jasmines as the first rays of dawn break over the horizon.",
            emotion = CharacterEmotion.DETERMINED,
            soundMood = "ending"
        ),

        // ACT 5 — FINAL DECISION (Hilltop Overlook & Branching Endings)
        "C5_MEERA_FINAL" to DialogueLine(
            id = "C5_MEERA_FINAL",
            speaker = "Meera",
            text = "Aarav... you found every piece of our story. Look at the horizon—the sun is rising over Nocturne for the first time in years.",
            emotion = CharacterEmotion.WARM,
            soundMood = "ending",
            choices = listOf(
                DialogueChoice(
                    choiceId = "CH_ENDING_A",
                    text = "Choice A: Accept the sorrow and let go (Step into the Waking Dawn).",
                    empathyDelta = 2,
                    nextDialogueId = "END_A_RESOLVE"
                ),
                DialogueChoice(
                    choiceId = "CH_ENDING_B",
                    text = "Choice B: Refuse to part (Remain in Nocturne with Meera forever).",
                    courageDelta = 2,
                    nextDialogueId = "END_B_RESOLVE"
                ),
                DialogueChoice(
                    choiceId = "CH_ENDING_C",
                    text = "Choice C: Send the unsent message and reclaim the true bond across eternity.",
                    empathyDelta = 5,
                    courageDelta = 5,
                    nextDialogueId = "END_C_RESOLVE"
                )
            )
        ),
        "END_A_RESOLVE" to DialogueLine(
            id = "END_A_RESOLVE",
            speaker = "Meera",
            text = "Thank you for loving me, Aarav. Live a long, beautiful life in the sunlight. I will always be with you.",
            emotion = CharacterEmotion.TEARFUL,
            soundMood = "ending",
            triggersCutsceneId = "ENDING_A"
        ),
        "END_B_RESOLVE" to DialogueLine(
            id = "END_B_RESOLVE",
            speaker = "Meera",
            text = "Then take my hand, Aarav. The rain will fall gently over us, and we will never be separated again.",
            emotion = CharacterEmotion.WARM,
            soundMood = "romance",
            triggersCutsceneId = "ENDING_B"
        ),
        "END_C_RESOLVE" to DialogueLine(
            id = "END_C_RESOLVE",
            speaker = "Meera",
            text = "The unsent message has been delivered! The Keeper dissolves into starlight... Our souls are free, Aarav. This is not the end—it is the beginning of UNSAVED 2.",
            emotion = CharacterEmotion.DETERMINED,
            soundMood = "reveal",
            triggersCutsceneId = "ENDING_C"
        )
    )

    fun getDialogue(id: String): DialogueLine? {
        return dialogues[id]
    }
}
