package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.DialogueChoice
import com.example.data.EntityType
import com.example.data.MemoryFragment
import com.example.data.Objective
import com.example.engine3d.Entity3D
import com.example.engine3d.GLWorldView
import com.example.story.StoryData
import com.example.ui.components.VirtualJoystick
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GameUiState

@Composable
fun GamePlayScreen(
    state: GameUiState,
    onMovePlayer: (dx: Float, dy: Float, running: Boolean) -> Unit,
    onStealthStateChanged: (tension: Float, noise: Float, isCrouched: Boolean, isHiding: Boolean) -> Unit = { _, _, _, _ -> },
    onInteract: () -> Unit,
    onTapDialogue: () -> Unit,
    onSelectChoice: (DialogueChoice) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onInspectMemoryDismiss: () -> Unit,
    onDismissFlashback: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isRunMode by remember { mutableStateOf(false) }
    var isCrouchMode by remember { mutableStateOf(false) }
    var isHidingMode by remember { mutableStateOf(false) }
    var glViewRef by remember { mutableStateOf<GLWorldView?>(null) }
    var focused3DEntity by remember { mutableStateOf<Entity3D?>(null) }
    var playerYaw by remember { mutableStateOf(0f) }
    var isFlashlightOn by remember { mutableStateOf(true) }
    var tensionLevel by remember { mutableFloatStateOf(0f) }

    val objectives = remember(state.completedObjectiveIds) {
        StoryData.ALL_STORY_OBJECTIVES.map { obj: Objective ->
            obj.copy(isCompleted = state.completedObjectiveIds.contains(obj.id))
        }
    }
    val currentMainObjective = objectives.firstOrNull { !it.isCompleted }

    // Synchronize 3D location changes
    LaunchedEffect(state.currentLocationScene.locationId) {
        glViewRef?.loadLocation(state.currentLocationScene.locationId)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. True 3D First-Person OpenGL ES World Viewport
        AndroidView(
            factory = { context ->
                GLWorldView(
                    context = context,
                    onEntityFocus = { entity ->
                        focused3DEntity = entity
                    },
                    onPlayerState = { x, y, z, yaw, isFlash ->
                        playerYaw = yaw
                        isFlashlightOn = isFlash
                    },
                    onStealthState = { tension, noise, isCrouch, isHide ->
                        tensionLevel = tension
                        isCrouchMode = isCrouch
                        isHidingMode = isHide
                        onStealthStateChanged(tension, noise, isCrouch, isHide)
                    }
                ).also { view ->
                    view.loadLocation(state.currentLocationScene.locationId)
                    glViewRef = view
                }
            },
            update = { view ->
                view.lookSensitivity = 0.20f * state.settings.joystickSensitivity
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Keeper Danger Vignette Pulse Overlay
        if (tensionLevel > 0.1f) {
            val dangerAlpha = (tensionLevel * 0.45f).coerceIn(0.05f, 0.45f)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CrimsonPrimary.copy(alpha = dangerAlpha))
            )
        }

        // 3. 3D First-Person Dynamic Crosshair & Reticle
        if (state.currentDialogue == null) {
            val infiniteTransition = rememberInfiniteTransition(label = "reticle_pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1.0f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (focused3DEntity != null) {
                    // Interactive targeted reticle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 60.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .scale(pulseScale)
                                .border(2.dp, CyanAccent, CircleShape)
                                .background(CyanAccent.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(MemoryGold, CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CharcoalSurface.copy(alpha = 0.9f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.7f)),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = when (focused3DEntity?.type) {
                                        EntityType.MEMORY_SHARD -> Icons.Default.AutoAwesome
                                        EntityType.NPC -> Icons.Default.ChatBubble
                                        EntityType.PUZZLE_STATION -> Icons.Default.Build
                                        EntityType.DOOR_PORTAL -> Icons.Default.MeetingRoom
                                        EntityType.HIDING_SPOT -> Icons.Default.SensorDoor
                                        else -> Icons.Default.TouchApp
                                    },
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = focused3DEntity?.name ?: "",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                } else {
                    // Subtle neutral crosshair dot
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.White.copy(alpha = 0.6f), CircleShape)
                    )
                }
            }
        }

        // 4. Top HUD Bar (Location, Compass, Stealth Heartbeat, Menus)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location badge & 3D Compass
                Column {
                    Text(
                        text = state.currentLocationScene.locationId.displayName.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    )

                    // Compass Heading
                    val heading = when (((playerYaw % 360f + 360f) % 360f).toInt()) {
                        in 338..360, in 0..22 -> "N • FIRST PERSON"
                        in 23..67 -> "NE • FIRST PERSON"
                        in 68..112 -> "E • FIRST PERSON"
                        in 113..157 -> "SE • FIRST PERSON"
                        in 158..202 -> "S • FIRST PERSON"
                        in 203..247 -> "SW • FIRST PERSON"
                        in 248..292 -> "W • FIRST PERSON"
                        else -> "NW • FIRST PERSON"
                    }

                    Text(
                        text = if (isHidingMode) "HIDING IN SHADOWS" else (if (isCrouchMode) "$heading (CROUCH)" else heading),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (isHidingMode) SuccessGreen else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                // Stealth Tension & HUD Actions
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (tensionLevel > 0.05f) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CrimsonPrimary.copy(alpha = 0.3f))
                                .border(1.dp, CrimsonPrimary, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Tension",
                                tint = CrimsonPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${(70 + tensionLevel * 90).toInt()} BPM",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = { onNavigate(AppScreen.MAP) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CharcoalSurface.copy(alpha = 0.85f))
                            .border(1.dp, CharcoalBorder, CircleShape)
                            .testTag("btn_hud_map")
                    ) {
                        Icon(Icons.Default.Map, contentDescription = "Map", tint = CyanAccent, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = { onNavigate(AppScreen.INVENTORY) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CharcoalSurface.copy(alpha = 0.85f))
                            .border(1.dp, CharcoalBorder, CircleShape)
                            .testTag("btn_hud_inventory")
                    ) {
                        Icon(Icons.Default.Backpack, contentDescription = "Inventory", tint = MemoryGold, modifier = Modifier.size(18.dp))
                    }

                    IconButton(
                        onClick = { onNavigate(AppScreen.PAUSE_MENU) },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CharcoalSurface.copy(alpha = 0.85f))
                            .border(1.dp, CharcoalBorder, CircleShape)
                            .testTag("btn_hud_pause")
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = "Pause", tint = TextPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Objective Tracker Pill
            if (currentMainObjective != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, CharcoalBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = CharcoalSurface.copy(alpha = 0.85f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Objective",
                            tint = CyanAccent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ACT ${currentMainObjective.actIndex}: ${currentMainObjective.title}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = currentMainObjective.targetHint,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 10.5.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 5. AutoSave Toast Notification Pill
        AnimatedVisibility(
            visible = state.isAutoSaving || state.toastNotification != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 80.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CharcoalSurface.copy(alpha = 0.92f))
                    .border(1.dp, CyanAccent.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = state.autoSaveMessage ?: state.toastNotification ?: "Saving...",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        // 6. Mobile First-Person Touch Controls
        if (state.currentDialogue == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Left: Virtual Movement Thumbstick
                    VirtualJoystick(
                        size = 135.dp,
                        onMove = { dx, dy ->
                            glViewRef?.setMovementInput(dx, -dy, isRunMode)
                            onMovePlayer(dx, dy, isRunMode)
                        }
                    )

                    // Right: Action Control Cluster
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Quick Toggles Row (Flashlight, Crouch, Sprint, Jump)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Flashlight Button
                            IconButton(
                                onClick = { glViewRef?.toggleFlashlight() },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isFlashlightOn) CyanAccent.copy(alpha = 0.85f) else CharcoalSurface.copy(alpha = 0.85f))
                                    .border(1.2.dp, if (isFlashlightOn) CyanAccent else CharcoalBorder, CircleShape)
                                    .testTag("btn_flashlight_toggle")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashlightOn,
                                    contentDescription = "Flashlight",
                                    tint = if (isFlashlightOn) CharcoalDark else TextSecondary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }

                            // Crouch Toggle Button
                            IconButton(
                                onClick = {
                                    isCrouchMode = glViewRef?.toggleCrouch() ?: false
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isCrouchMode) MemoryAmber.copy(alpha = 0.85f) else CharcoalSurface.copy(alpha = 0.85f))
                                    .border(1.2.dp, if (isCrouchMode) MemoryAmber else CharcoalBorder, CircleShape)
                                    .testTag("btn_crouch_toggle")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AirlineSeatReclineExtra,
                                    contentDescription = "Crouch",
                                    tint = if (isCrouchMode) CharcoalDark else TextSecondary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }

                            // Sprint Toggle Button
                            IconButton(
                                onClick = {
                                    isRunMode = !isRunMode
                                    glViewRef?.setMovementInput(0f, 0f, isRunMode)
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isRunMode) CrimsonPrimary.copy(alpha = 0.85f) else CharcoalSurface.copy(alpha = 0.85f))
                                    .border(1.2.dp, if (isRunMode) CrimsonGlow else CharcoalBorder, CircleShape)
                                    .testTag("btn_run_toggle")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsRun,
                                    contentDescription = "Run",
                                    tint = if (isRunMode) Color.White else TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Jump Button
                            IconButton(
                                onClick = { glViewRef?.jump() },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(CharcoalSurface.copy(alpha = 0.85f))
                                    .border(1.2.dp, CharcoalBorder, CircleShape)
                                    .testTag("btn_jump")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = "Jump",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Contextual 3D Interact / Hide Action Button
                        val target = focused3DEntity
                        val isHidingSpot = target?.type == EntityType.HIDING_SPOT
                        val hasTarget = target != null || isHidingMode
                        val promptText = when {
                            isHidingMode -> "LEAVE HIDING SPOT"
                            isHidingSpot -> "ENTER HIDING SPOT"
                            target != null -> target.promptText
                            else -> "LOOK AROUND"
                        }

                        Button(
                            onClick = {
                                if (isHidingMode) {
                                    glViewRef?.toggleHiding(null)
                                } else if (isHidingSpot) {
                                    glViewRef?.toggleHiding(target)
                                } else if (target != null) {
                                    when (target.type) {
                                        EntityType.NPC, EntityType.INTERACTABLE -> target.targetDialogueId?.let { onInteract() }
                                        EntityType.MEMORY_SHARD -> target.targetMemoryId?.let { onInteract() }
                                        EntityType.PUZZLE_STATION -> target.targetPuzzleId?.let { onInteract() }
                                        EntityType.DOOR_PORTAL -> onInteract()
                                        else -> onInteract()
                                    }
                                }
                            },
                            enabled = hasTarget,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .widthIn(min = 140.dp)
                                .border(
                                    1.5.dp,
                                    if (hasTarget) CyanAccent else CharcoalBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .testTag("btn_interact_action"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasTarget) CyanAccent else CharcoalSurface.copy(alpha = 0.6f),
                                disabledContainerColor = CharcoalSurface.copy(alpha = 0.6f)
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when {
                                        isHidingMode || isHidingSpot -> Icons.Default.SensorDoor
                                        target?.type == EntityType.MEMORY_SHARD -> Icons.Default.AutoAwesome
                                        target?.type == EntityType.NPC -> Icons.Default.ChatBubble
                                        target?.type == EntityType.PUZZLE_STATION -> Icons.Default.Build
                                        target?.type == EntityType.DOOR_PORTAL -> Icons.Default.MeetingRoom
                                        else -> Icons.Default.TouchApp
                                    },
                                    contentDescription = null,
                                    tint = if (hasTarget) CharcoalDark else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = promptText.uppercase(),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (hasTarget) CharcoalDark else TextMuted,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Active Dialogue System Overlay
        if (state.currentDialogue != null) {
            DialogueOverlay(
                dialogue = state.currentDialogue,
                progress = state.dialogueTextProgress,
                isComplete = state.isDialogueComplete,
                onTapDialogue = onTapDialogue,
                onSelectChoice = onSelectChoice
            )
        }

        // 8. Memory Shard Discovery Inspection Modal
        if (state.memoryInspectModal != null) {
            MemoryInspectionModal(
                memory = state.memoryInspectModal,
                onDismiss = onInspectMemoryDismiss
            )
        }

        // 9. Cinematic Memory Flashback Modal
        if (state.activeFlashbackMemory != null) {
            FlashbackModal(
                memory = state.activeFlashbackMemory,
                onDismiss = onDismissFlashback
            )
        }
    }
}

@Composable
fun MemoryInspectionModal(
    memory: MemoryFragment,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE605070A))
            .clickable { onDismiss() }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(18.dp))
                .border(1.5.dp, CyanAccent.copy(alpha = 0.8f), RoundedCornerShape(18.dp))
                .testTag("memory_inspect_card"),
            colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Memory",
                    tint = if (memory.isCrucial) MemoryGold else CyanAccent,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "MEMORY SHARD SAVED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanAccent,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = memory.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${memory.dateStamp} • ${memory.location}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = memory.fullMemoryStory,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextPrimary,
                        lineHeight = 22.sp,
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_close_memory_inspect"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                ) {
                    Text(
                        text = "RESUME EXPLORATION",
                        fontWeight = FontWeight.Bold,
                        color = CharcoalDark
                    )
                }
            }
        }
    }
}
