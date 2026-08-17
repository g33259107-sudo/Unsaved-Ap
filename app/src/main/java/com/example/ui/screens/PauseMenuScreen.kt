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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LocationId
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen

@Composable
fun PauseMenuScreen(
    currentLocation: LocationId,
    discoveredMemoriesCount: Int,
    playTimeSeconds: Long,
    onNavigate: (AppScreen) -> Unit,
    onResume: () -> Unit,
    onRestartCheckpoint: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showControlsDialog by remember { mutableStateOf(false) }
    var showRestartConfirmDialog by remember { mutableStateOf(false) }

    val minutes = playTimeSeconds / 60
    val seconds = playTimeSeconds % 60

    if (showControlsDialog) {
        AlertDialog(
            onDismissRequest = { showControlsDialog = false },
            title = {
                Text("GAMEPLAY CONTROLS", color = CyanAccent, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ControlGuideItem(icon = Icons.Default.Navigation, label = "Movement", desc = "Drag the thumbstick on the bottom-left")
                    ControlGuideItem(icon = Icons.Default.Visibility, label = "Look & Aim", desc = "Drag finger anywhere on the right screen")
                    ControlGuideItem(icon = Icons.Default.TouchApp, label = "Interact", desc = "Tap the circular button when near objects/doors")
                    ControlGuideItem(icon = Icons.Default.FlashlightOn, label = "Flashlight", desc = "Toggle beam on/off to illuminate dark corners")
                    ControlGuideItem(icon = Icons.Default.DirectionsRun, label = "Sprint & Crouch", desc = "Use the action buttons on the bottom-right")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showControlsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CharcoalSurfaceVariant)
                ) {
                    Text("GOT IT", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CharcoalSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }

    if (showRestartConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRestartConfirmDialog = false },
            title = {
                Text("RESTART CHECKPOINT?", color = CrimsonGlow, fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Reload from the latest checkpoint? Any unsaved current steps in this room will be reset.", color = TextPrimary, fontSize = 13.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRestartConfirmDialog = false
                        onRestartCheckpoint()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                ) {
                    Text("RESTART", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRestartConfirmDialog = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            containerColor = CharcoalSurface,
            shape = RoundedCornerShape(12.dp)
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xEE0E1420),
                        Color(0xFA070A0F)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        val isLandscape = maxWidth > maxHeight

        if (isLandscape) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column: Game State Info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "PAUSED",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = CrimsonGlow,
                            letterSpacing = 4.sp,
                            fontWeight = FontWeight.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentLocation.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .border(1.dp, CharcoalBorder, RoundedCornerShape(8.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Playtime", fontSize = 11.sp, color = TextSecondary)
                                Text("${minutes}m ${seconds}s", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Memories Restored", fontSize = 11.sp, color = TextSecondary)
                                Text("$discoveredMemoriesCount / 7", fontSize = 11.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Right Column: 5 Main Pause Menu Actions
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .widthIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Continue
                    MenuButton(
                        title = "CONTINUE",
                        subtitle = "Return to 3D World",
                        icon = Icons.Default.PlayArrow,
                        isPrimary = true,
                        testTag = "btn_pause_resume",
                        onClick = onResume
                    )

                    // 2. Restart Checkpoint
                    MenuButton(
                        title = "RESTART CHECKPOINT",
                        subtitle = "Reload Last Saved Point",
                        icon = Icons.Default.Refresh,
                        isPrimary = false,
                        testTag = "btn_pause_restart",
                        onClick = { showRestartConfirmDialog = true }
                    )

                    // 3. Settings
                    MenuButton(
                        title = "SETTINGS",
                        subtitle = "Graphics, Audio & Sensitivity",
                        icon = Icons.Default.Tune,
                        isPrimary = false,
                        testTag = "btn_pause_settings",
                        onClick = { onNavigate(AppScreen.SETTINGS) }
                    )

                    // 4. Controls
                    MenuButton(
                        title = "CONTROLS",
                        subtitle = "Touch Input Diagram & Guide",
                        icon = Icons.Default.Gamepad,
                        isPrimary = false,
                        testTag = "btn_pause_controls",
                        onClick = { showControlsDialog = true }
                    )

                    // 5. Exit to Main Menu
                    MenuButton(
                        title = "EXIT TO MAIN MENU",
                        subtitle = "Return to Title Screen",
                        icon = Icons.Default.ExitToApp,
                        isPrimary = false,
                        testTag = "btn_pause_mainmenu",
                        onClick = { onNavigate(AppScreen.MAIN_MENU) }
                    )
                }
            }
        } else {
            // Portrait Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "PAUSED",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = CrimsonGlow,
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.Black
                    )
                )

                Text(
                    text = currentLocation.displayName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 380.dp)
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Playtime", fontSize = 10.sp, color = TextSecondary)
                            Text("${minutes}m ${seconds}s", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Memories", fontSize = 10.sp, color = TextSecondary)
                            Text("$discoveredMemoriesCount / 7", fontSize = 12.sp, color = CyanAccent, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    MenuButton(
                        title = "CONTINUE",
                        subtitle = "Return to 3D World",
                        icon = Icons.Default.PlayArrow,
                        isPrimary = true,
                        testTag = "btn_pause_resume",
                        onClick = onResume
                    )

                    MenuButton(
                        title = "RESTART CHECKPOINT",
                        subtitle = "Reload Last Saved Point",
                        icon = Icons.Default.Refresh,
                        isPrimary = false,
                        testTag = "btn_pause_restart",
                        onClick = { showRestartConfirmDialog = true }
                    )

                    MenuButton(
                        title = "SETTINGS",
                        subtitle = "Graphics, Audio & Sensitivity",
                        icon = Icons.Default.Tune,
                        isPrimary = false,
                        testTag = "btn_pause_settings",
                        onClick = { onNavigate(AppScreen.SETTINGS) }
                    )

                    MenuButton(
                        title = "CONTROLS",
                        subtitle = "Touch Input Diagram & Guide",
                        icon = Icons.Default.Gamepad,
                        isPrimary = false,
                        testTag = "btn_pause_controls",
                        onClick = { showControlsDialog = true }
                    )

                    MenuButton(
                        title = "EXIT TO MAIN MENU",
                        subtitle = "Return to Title Screen",
                        icon = Icons.Default.ExitToApp,
                        isPrimary = false,
                        testTag = "btn_pause_mainmenu",
                        onClick = { onNavigate(AppScreen.MAIN_MENU) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlGuideItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(desc, color = TextSecondary, fontSize = 10.5.sp)
        }
    }
}
