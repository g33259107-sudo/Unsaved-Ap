package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameSettings
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    currentSettings: GameSettings,
    onSaveSettings: (GameSettings) -> Unit,
    onResetProgress: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var masterVol by remember(currentSettings) { mutableFloatStateOf(currentSettings.masterVolume) }
    var musicVol by remember(currentSettings) { mutableFloatStateOf(currentSettings.musicVolume) }
    var sfxVol by remember(currentSettings) { mutableFloatStateOf(currentSettings.sfxVolume) }
    var voiceVol by remember(currentSettings) { mutableFloatStateOf(currentSettings.voiceVolume) }
    var sensitivity by remember(currentSettings) { mutableFloatStateOf(currentSettings.joystickSensitivity) }
    var graphicsQuality by remember(currentSettings) { mutableStateOf(currentSettings.graphicsQuality) }
    var vibrationEnabled by remember(currentSettings) { mutableStateOf(currentSettings.vibrationEnabled) }
    var subtitlesEnabled by remember(currentSettings) { mutableStateOf(currentSettings.subtitlesEnabled) }
    var language by remember(currentSettings) { mutableStateOf(currentSettings.language) }

    var showResetConfirmDialog by remember { mutableStateOf(false) }

    fun commitSettings() {
        onSaveSettings(
            GameSettings(
                masterVolume = masterVol,
                musicVolume = musicVol,
                sfxVolume = sfxVol,
                ambienceVolume = currentSettings.ambienceVolume,
                voiceVolume = voiceVol,
                graphicsQuality = graphicsQuality,
                textSpeed = currentSettings.textSpeed,
                joystickSensitivity = sensitivity,
                hapticFeedback = vibrationEnabled,
                vibrationEnabled = vibrationEnabled,
                puzzleHints = currentSettings.puzzleHints,
                cinematicSubtitles = subtitlesEnabled,
                subtitlesEnabled = subtitlesEnabled,
                language = language
            )
        )
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text(
                    "RESET ALL PROGRESS?",
                    color = CrimsonGlow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "This will permanently delete all save slots, checkpoints, and unlocked story progress. This action cannot be undone.",
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirmDialog = false
                        onResetProgress()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                ) {
                    Text("YES, RESET EVERYTHING", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showResetConfirmDialog = false }
                ) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            containerColor = CharcoalSurface,
            shape = RoundedCornerShape(14.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CharcoalSurface)
                    .border(1.dp, CharcoalBorder, RoundedCornerShape(8.dp))
                    .testTag("btn_settings_back")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary, modifier = Modifier.size(18.dp))
            }

            Text(
                text = "SYSTEM SETTINGS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 2.sp
                )
            )

            Spacer(modifier = Modifier.width(38.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Two-Column Landscape Grid
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Column: Audio & Sound Levels
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("AUDIO & VOLUME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent, letterSpacing = 1.2.sp)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        VolumeSliderRow(
                            title = "Master Volume",
                            value = masterVol,
                            color = CyanAccent,
                            onValueChange = { masterVol = it; commitSettings() },
                            testTag = "slider_master_vol"
                        )
                        VolumeSliderRow(
                            title = "Music Volume",
                            value = musicVol,
                            color = CyanAccent,
                            onValueChange = { musicVol = it; commitSettings() },
                            testTag = "slider_music_vol"
                        )
                        VolumeSliderRow(
                            title = "SFX Volume",
                            value = sfxVol,
                            color = MemoryGold,
                            onValueChange = { sfxVol = it; commitSettings() },
                            testTag = "slider_sfx_vol"
                        )
                        VolumeSliderRow(
                            title = "Voice & Memos",
                            value = voiceVol,
                            color = CrimsonGlow,
                            onValueChange = { voiceVol = it; commitSettings() },
                            testTag = "slider_voice_vol"
                        )
                    }
                }
            }

            // Right Column: Graphics, Controls, Language & Reset
            Column(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("GAMEPLAY & DISPLAY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyanAccent, letterSpacing = 1.2.sp)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Graphics Quality
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Graphics Quality", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("Low", "Med", "High", "Ultra").forEach { level ->
                                    val isSelected = (level == "Med" && graphicsQuality == "Medium") || graphicsQuality == level
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) CyanAccent else CharcoalSurface)
                                            .border(1.dp, if (isSelected) CyanGlow else CharcoalBorder, RoundedCornerShape(6.dp))
                                            .clickable {
                                                graphicsQuality = if (level == "Med") "Medium" else level
                                                commitSettings()
                                            }
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = level,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) CharcoalDark else TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // Sensitivity
                        VolumeSliderRow(
                            title = "Camera Sensitivity",
                            value = (sensitivity / 2.0f).coerceIn(0.1f, 1.0f),
                            color = CyanAccent,
                            onValueChange = {
                                sensitivity = (it * 2.0f).coerceIn(0.2f, 2.0f)
                                commitSettings()
                            },
                            testTag = "slider_sensitivity"
                        )

                        // Toggles Row: Vibration & Subtitles
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = vibrationEnabled,
                                    onCheckedChange = { vibrationEnabled = it; commitSettings() },
                                    colors = SwitchDefaults.colors(checkedThumbColor = CyanAccent, checkedTrackColor = CharcoalSurfaceVariant),
                                    modifier = Modifier.scale(0.8f).testTag("switch_vibration")
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Vibration", fontSize = 11.5.sp, color = TextPrimary)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = subtitlesEnabled,
                                    onCheckedChange = { subtitlesEnabled = it; commitSettings() },
                                    colors = SwitchDefaults.colors(checkedThumbColor = CyanAccent, checkedTrackColor = CharcoalSurfaceVariant),
                                    modifier = Modifier.scale(0.8f).testTag("switch_subtitles")
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Subtitles", fontSize = 11.5.sp, color = TextPrimary)
                            }
                        }

                        // Language Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Language", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("English", "Español", "日本語").forEach { lang ->
                                    val isSelected = language == lang
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) CyanAccent else CharcoalSurface)
                                            .border(1.dp, if (isSelected) CyanGlow else CharcoalBorder, RoundedCornerShape(6.dp))
                                            .clickable {
                                                language = lang
                                                commitSettings()
                                            }
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = lang,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) CharcoalDark else TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Reset Progress Action
                OutlinedButton(
                    onClick = { showResetConfirmDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("btn_reset_progress"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonGlow),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonDark),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = CrimsonGlow, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("RESET PROGRESS", color = CrimsonGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun VolumeSliderRow(
    title: String,
    value: Float,
    color: Color,
    onValueChange: (Float) -> Unit,
    testTag: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 11.5.sp, color = TextPrimary)
            Text("${(value * 100).toInt()}%", fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(thumbColor = color, activeTrackColor = color),
            modifier = Modifier
                .height(28.dp)
                .testTag(testTag)
        )
    }
}
