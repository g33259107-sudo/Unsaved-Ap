package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CharacterEmotion
import com.example.data.DialogueChoice
import com.example.data.DialogueLine
import com.example.ui.theme.*

@Composable
fun DialogueOverlay(
    dialogue: DialogueLine,
    progress: Int,
    isComplete: Boolean,
    onTapDialogue: () -> Unit,
    onSelectChoice: (DialogueChoice) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayedText = dialogue.text.take(progress)

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable { onTapDialogue() }
            .padding(bottom = 16.dp, start = 12.dp, end = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 700.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dialogue Choices (if any, displayed above the text box)
            if (isComplete && dialogue.choices.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    dialogue.choices.forEachIndexed { index, choice ->
                        Button(
                            onClick = { onSelectChoice(choice) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, CyanAccent.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .testTag("dialogue_choice_$index"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CharcoalSurfaceVariant.copy(alpha = 0.95f)
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Choice",
                                    tint = if (choice.choiceId.contains("ENDING")) CrimsonGlow else CyanAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = choice.text,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Main Letterbox Dialogue Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.2.dp, CharcoalBorder, RoundedCornerShape(16.dp))
                    .testTag("dialogue_box"),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface.copy(alpha = 0.96f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Speaker Nameplate & Emotion Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (dialogue.speaker) {
                                            "Noah" -> BlueAtmosphere
                                            "Elena", "Echo of Elena" -> CrimsonGlow
                                            "Ren", "Echo of Ren" -> MemoryGold
                                            else -> CyanAccent
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = dialogue.speaker.uppercase(),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    letterSpacing = 1.5.sp
                                )
                            )
                        }

                        if (dialogue.emotion != CharacterEmotion.NEUTRAL) {
                            Text(
                                text = "[ ${dialogue.emotion.name.lowercase()} ]",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextMuted,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Typewriter Story Text
                    Text(
                        text = displayedText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextPrimary,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        ),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tap to advance hint
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isComplete && dialogue.choices.isEmpty()) {
                            Text(
                                text = "TAP TO ADVANCE",
                                fontSize = 10.sp,
                                color = TextMuted,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Next",
                                tint = CyanAccent,
                                modifier = Modifier.size(12.dp)
                            )
                        } else if (!isComplete) {
                            Text(
                                text = "TAP TO SKIP",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
