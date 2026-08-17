package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.EndingType
import com.example.ui.theme.*

@Composable
fun EndingScreen(
    ending: EndingType,
    memoriesDiscoveredCount: Int,
    playTimeSeconds: Long,
    empathyScore: Int,
    courageScore: Int,
    onReturnToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val minutes = playTimeSeconds / 60
    val seconds = playTimeSeconds % 60

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalDark)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_ending_stars),
            contentDescription = "Ending Art",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark Gradient Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x8805070A),
                            Color(0xDD090C12),
                            Color(0xF50A0C10)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when (ending) {
                        EndingType.ENDING_A -> "EPILOGUE • ENDING 1 OF 3"
                        EndingType.ENDING_B -> "EPILOGUE • ENDING 2 OF 3"
                        EndingType.ENDING_C -> "★ TRUE RESOLUTION • HIDDEN ENDING"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (ending == EndingType.ENDING_C) MemoryGold else CyanAccent,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = ending.title.uppercase(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 2.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Narrative Resolution Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface.copy(alpha = 0.9f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = when (ending) {
                                EndingType.ENDING_A ->
                                    "The morning sun breaks over the horizon, painting the rainy streets in shades of amber and rose.\n\nNoah gently closes the vintage notebook. The ache in his chest hasn't disappeared, but it is no longer a wound—it is a sanctuary. Elena's voice echoes in the breeze: 'Live well, Noah. I'm proud of you.'\n\nHe walks down the hill into the waking world, carrying her memory not as a burden, but as a guiding light."

                                EndingType.ENDING_B ->
                                    "Noah sits on the rainy curb under the warm streetlamp, clutching the glowing quartz pendant.\n\nThe city beyond fades into mist. In this pocket of time, Elena sits beside him with two hot coffees, smiling softly. Here, nothing hurts, nothing is forgotten, and nothing is lost.\n\nHe closes his eyes, deciding that some dreams are worth remaining asleep for forever."

                                EndingType.ENDING_C ->
                                    "Every memory fragment aligns in radiant symmetry across the midnight sky.\n\nElena's silhouette steps forward and takes Noah's hand. 'You didn't run away from the pain, and you didn't trap yourself in the dark,' she whispers, tears of joy sparkling like stardust. 'You saved every memory, and in doing so, you saved both of us.'\n\nAs the dawn arrives, Elena dissolves into a gentle warmth that settles permanently into Noah's heart. He is whole. He is peaceful. Nothing was left unsaved."
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary,
                                fontSize = 14.sp,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Journey Statistics
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard.copy(alpha = 0.85f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "JOURNEY SUMMARY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Memories Saved:", fontSize = 13.sp, color = TextSecondary)
                            Text("$memoriesDiscoveredCount / 7", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MemoryGold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Exploration Time:", fontSize = 13.sp, color = TextSecondary)
                            Text("${minutes}m ${seconds}s", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Empathy Resonance:", fontSize = 13.sp, color = TextSecondary)
                            Text("$empathyScore pts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyanAccent)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Courage Rating:", fontSize = 13.sp, color = TextSecondary)
                            Text("$courageScore pts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CrimsonGlow)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Developer Credits Section
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "A GAME BY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            letterSpacing = 3.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Satyam Gupta",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onReturnToMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("btn_ending_return_menu"),
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Home, contentDescription = "Menu", tint = CharcoalDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RETURN TO TITLE SCREEN",
                        fontWeight = FontWeight.Bold,
                        color = CharcoalDark
                    )
                }
            }
        }
    }
}
