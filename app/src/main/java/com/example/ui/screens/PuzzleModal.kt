package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlin.math.abs

@Composable
fun PuzzleModal(
    puzzleId: String,
    onSolved: () -> Unit,
    onClose: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xEE080A0F))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.5.dp, CharcoalBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (puzzleId) {
                                "PUZZLE_FUSE_1" -> "⚡ STATION CIRCUIT BREAKER"
                                "PUZZLE_COFFEE_2" -> "☕ CAFE NOCTURNE SAFE DIAL"
                                "PUZZLE_RADIO_3" -> "📻 EMERGENCY RADIO RECEIVER"
                                "PUZZLE_STEAM_4" -> "💨 CATACOMB STEAM EQUALIZER"
                                "PUZZLE_CLOCK_3" -> "🕰️ GRAND CLOCK MECHANISM (07:18)"
                                else -> "ENVIRONMENTAL PUZZLE"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.testTag("close_puzzle_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (puzzleId) {
                        "PUZZLE_FUSE_1" -> FuseSequencePuzzle(onSolved)
                        "PUZZLE_COFFEE_2" -> LockboxCodePuzzle(onSolved)
                        "PUZZLE_RADIO_3" -> RadioFrequencyPuzzle(onSolved)
                        "PUZZLE_STEAM_4" -> SteamValvePuzzle(onSolved)
                        "PUZZLE_CLOCK_3" -> ClockGearPuzzle(onSolved)
                        else -> {
                            Text("Puzzle unknown.", color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FuseSequencePuzzle(onSolved: () -> Unit) {
    // Solution: Red: ON, Amber: ON, Azure: OFF, Jade: ON
    var switchRed by remember { mutableStateOf(false) }
    var switchAmber by remember { mutableStateOf(false) }
    var switchAzure by remember { mutableStateOf(true) }
    var switchJade by remember { mutableStateOf(false) }

    val isCorrect = switchRed && switchAmber && !switchAzure && switchJade

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Restore the auxiliary lighting to power the turnstile gate. Journal hint: 'RED and AMBER live, AZURE grounded, JADE active.'",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, textAlign = TextAlign.Center),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FuseSwitch("RED", CrimsonPrimary, switchRed) { switchRed = !switchRed }
            FuseSwitch("AMBER", MemoryAmber, switchAmber) { switchAmber = !switchAmber }
            FuseSwitch("AZURE", CyanAccent, switchAzure) { switchAzure = !switchAzure }
            FuseSwitch("JADE", SuccessGreen, switchJade) { switchJade = !switchJade }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                if (isCorrect) onSolved()
            },
            enabled = isCorrect,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCorrect) SuccessGreen else CharcoalSurfaceVariant,
                disabledContainerColor = CharcoalSurfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(48.dp)
                .testTag("submit_fuse_button")
        ) {
            Text(
                if (isCorrect) "⚡ RESTORE PLATFORM POWER" else "CIRCUIT INCOMPLETE",
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) CharcoalDark else TextMuted
            )
        }
    }
}

@Composable
fun FuseSwitch(
    label: String,
    color: Color,
    state: Boolean,
    onToggle: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onToggle() }
            .padding(8.dp)
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CharcoalDark)
                .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(4.dp),
            contentAlignment = if (state) Alignment.TopCenter else Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (state) color else CharcoalBorder)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(if (state) "ON" else "OFF", fontSize = 10.sp, color = TextMuted)
    }
}

@Composable
fun LockboxCodePuzzle(onSolved: () -> Unit) {
    // Solution: 2 - 4 - 8 - 1 (From Meera's phone draft)
    var digit1 by remember { mutableStateOf(0) }
    var digit2 by remember { mutableStateOf(0) }
    var digit3 by remember { mutableStateOf(0) }
    var digit4 by remember { mutableStateOf(0) }

    val isCorrect = digit1 == 2 && digit2 == 4 && digit3 == 8 && digit4 == 1

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Vintage 4-digit safe dial on the cafe counter. Meera's draft note: 'The combination is 2-4-8-1.'",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CodeDigit(digit1, { digit1 = (digit1 + 1) % 10 }, { digit1 = if (digit1 > 0) digit1 - 1 else 9 })
            Spacer(modifier = Modifier.width(10.dp))
            CodeDigit(digit2, { digit2 = (digit2 + 1) % 10 }, { digit2 = if (digit2 > 0) digit2 - 1 else 9 })
            Spacer(modifier = Modifier.width(10.dp))
            CodeDigit(digit3, { digit3 = (digit3 + 1) % 10 }, { digit3 = if (digit3 > 0) digit3 - 1 else 9 })
            Spacer(modifier = Modifier.width(10.dp))
            CodeDigit(digit4, { digit4 = (digit4 + 1) % 10 }, { digit4 = if (digit4 > 0) digit4 - 1 else 9 })
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = { if (isCorrect) onSolved() },
            enabled = isCorrect,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCorrect) CyanAccent else CharcoalSurfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(48.dp)
                .testTag("submit_lockbox_button")
        ) {
            Text(
                if (isCorrect) "🔓 UNLOCK CATACOMBS KEYCARD" else "ENTER 4-DIGIT CODE",
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) CharcoalDark else TextMuted
            )
        }
    }
}

@Composable
fun RadioFrequencyPuzzle(onSolved: () -> Unit) {
    var frequency by remember { mutableFloatStateOf(88.0f) }
    val targetFreq = 94.7f
    val isTuned = abs(frequency - targetFreq) < 0.25f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Tune the analog emergency frequency knob to 94.7 MHz to decrypt the broadcast.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Signal Quality Meter
        val signalPercent = (1.0f - (abs(frequency - targetFreq) / 10.0f)).coerceIn(0.05f, 1.0f)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = String.format("%.1f MHz", frequency),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isTuned) SuccessGreen else CyanAccent
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isTuned) "SIGNAL LOCKED • BROADCAST CLEAR" else "SEARCHING FOR BROADCAST...",
                fontSize = 11.sp,
                color = if (isTuned) SuccessGreen else TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { signalPercent },
                color = if (isTuned) SuccessGreen else CyanAccent,
                trackColor = CharcoalDark,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = frequency,
            onValueChange = { frequency = it },
            valueRange = 88.0f..108.0f,
            modifier = Modifier.fillMaxWidth(0.85f),
            colors = SliderDefaults.colors(
                thumbColor = CyanAccent,
                activeTrackColor = CyanAccent,
                inactiveTrackColor = CharcoalDark
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { if (isTuned) onSolved() },
            enabled = isTuned,
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(48.dp)
        ) {
            Text(
                if (isTuned) "📻 RECEIVE BROADCAST TAPE" else "ALIGN TO 94.7 MHz",
                fontWeight = FontWeight.Bold,
                color = CharcoalDark
            )
        }
    }
}

@Composable
fun SteamValvePuzzle(onSolved: () -> Unit) {
    // Solution: V1: ON, V2: OFF, V3: ON, V4: ON
    var v1 by remember { mutableStateOf(false) }
    var v2 by remember { mutableStateOf(true) }
    var v3 by remember { mutableStateOf(false) }
    var v4 by remember { mutableStateOf(false) }

    val isBalanced = v1 && !v2 && v3 && v4

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Equalize the catacomb steam bypass lines: Valves 1, 3, and 4 open, Valve 2 closed.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ValveSwitch("VALVE 1", v1) { v1 = !v1 }
            ValveSwitch("VALVE 2", v2) { v2 = !v2 }
            ValveSwitch("VALVE 3", v3) { v3 = !v3 }
            ValveSwitch("VALVE 4", v4) { v4 = !v4 }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { if (isBalanced) onSolved() },
            enabled = isBalanced,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isBalanced) SuccessGreen else CharcoalSurfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(48.dp)
        ) {
            Text(
                if (isBalanced) "💨 BYPASS STEAM BULKHEAD" else "PRESSURE UNBALANCED",
                fontWeight = FontWeight.Bold,
                color = if (isBalanced) CharcoalDark else TextMuted
            )
        }
    }
}

@Composable
fun ValveSwitch(name: String, state: Boolean, onToggle: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onToggle() }
            .padding(6.dp)
    ) {
        Text(name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (state) SuccessGreen.copy(alpha = 0.2f) else CharcoalDark)
                .border(2.dp, if (state) SuccessGreen else CharcoalBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.RotateRight,
                contentDescription = null,
                tint = if (state) SuccessGreen else TextMuted,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(if (state) "OPEN" else "CLOSED", fontSize = 10.sp, color = if (state) SuccessGreen else TextMuted)
    }
}

@Composable
fun CodeDigit(value: Int, onUp: () -> Unit, onDown: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onUp, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = CyanAccent)
        }
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(CharcoalDark)
                .border(1.5.dp, CharcoalBorder, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 24.sp
                )
            )
        }
        IconButton(onClick = onDown, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = CyanAccent)
        }
    }
}

@Composable
fun ClockGearPuzzle(onSolved: () -> Unit) {
    // Solution: Hour = 7, Minute = 18 (07:18 - train departure time)
    var hour by remember { mutableStateOf(12) }
    var minute by remember { mutableStateOf(0) }

    val isCorrect = hour == 7 && minute == 18

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Station schedule board: 'Train #404 to Nowhere departed at exactly 07:18 PM.' Set the frozen mechanism.",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, textAlign = TextAlign.Center)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("HOUR (1-12)", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { hour = if (hour > 1) hour - 1 else 12 }) {
                        Icon(Icons.Default.Remove, contentDescription = "-", tint = MemoryGold)
                    }
                    Text(
                        String.format("%02d", hour),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MemoryGold
                    )
                    IconButton(onClick = { hour = if (hour < 12) hour + 1 else 1 }) {
                        Icon(Icons.Default.Add, contentDescription = "+", tint = MemoryGold)
                    }
                }
            }

            Text(":", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.padding(horizontal = 8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("MINUTE (00-59)", fontSize = 11.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { minute = if (minute > 0) minute - 1 else 59 }) {
                        Icon(Icons.Default.Remove, contentDescription = "-", tint = MemoryGold)
                    }
                    Text(
                        String.format("%02d", minute),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MemoryGold
                    )
                    IconButton(onClick = { minute = (minute + 1) % 60 }) {
                        Icon(Icons.Default.Add, contentDescription = "+", tint = MemoryGold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { if (isCorrect) onSolved() },
            enabled = isCorrect,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCorrect) MemoryGold else CharcoalSurfaceVariant
            ),
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(48.dp)
                .testTag("submit_clock_button")
        ) {
            Text(
                if (isCorrect) "⚙️ ENGAGE GEARS (07:18)" else "SET TIME TO 07:18",
                fontWeight = FontWeight.Bold,
                color = if (isCorrect) CharcoalDark else TextMuted
            )
        }
    }
}
