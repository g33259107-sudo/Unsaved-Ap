package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import kotlin.random.Random

@Composable
fun MainMenuScreen(
    onNewGame: () -> Unit,
    onContinue: () -> Unit,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "ambientAnimation")

    // Ambient glow oscillation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Subtle mist drift
    val mistOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mist"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalDark)
    ) {
        val isLandscape = maxWidth > maxHeight
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        // 1. Cinematic Background Layer
        Image(
            painter = painterResource(id = R.drawable.img_menu_cover),
            contentDescription = "Cover Atmosphere",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Deep Blue-Black Gradient Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isLandscape) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFA070A0F),
                                Color(0xEE0A0F18),
                                Color(0xD90A0D14),
                                Color(0xF007090E)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xEE070A0F),
                                Color(0xFA0A0F18),
                                Color(0xFF07090E)
                            )
                        )
                    }
                )
        )

        // 3. Subtle Animated Rain & Fog Particle Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val random = Random(42)
            val dropCount = 40
            for (i in 0 until dropCount) {
                val startX = (random.nextFloat() * size.width + (mistOffset * 0.2f)) % size.width
                val startY = (random.nextFloat() * size.height + (mistOffset * 1.5f)) % size.height
                val dropLength = random.nextFloat() * 22f + 14f
                drawLine(
                    color = Color(0x3380D8FF),
                    start = Offset(startX, startY),
                    end = Offset(startX - 2f, startY + dropLength),
                    strokeWidth = 1.2f
                )
            }
        }

        // 4. Responsive Layout: Landscape 2-Column or Portrait Unified Column
        if (isLandscape) {
            val logoSize = (screenHeight * 0.42f).coerceIn(110.dp, 165.dp)

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Pane: Official UNSAVED Circular Logo & Story Tagline
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(logoSize)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(logoSize)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            CrimsonPrimary.copy(alpha = glowAlpha * 0.45f),
                                            CyanAccent.copy(alpha = glowAlpha * 0.25f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )

                        Image(
                            painter = painterResource(id = R.drawable.img_unsaved_logo),
                            contentDescription = "UNSAVED Official Game Logo",
                            modifier = Modifier
                                .size(logoSize * 0.90f)
                                .clip(CircleShape)
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.sweepGradient(
                                        listOf(
                                            CrimsonPrimary,
                                            CyanAccent,
                                            CrimsonPrimary
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .shadow(14.dp, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "A STORY • A MYSTERY • A SECOND CHANCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent.copy(alpha = glowAlpha),
                            letterSpacing = 2.0.sp,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "“Some memories were never meant to be deleted.”",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 10.5.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                // Right Pane: 8 Sleek Glass/Metal Buttons
                Column(
                    modifier = Modifier
                        .weight(1.15f)
                        .fillMaxHeight()
                        .widthIn(max = 420.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. NEW GAME
                    MenuButton(
                        title = "NEW GAME",
                        subtitle = "Awaken at Nocturne Station",
                        icon = Icons.Default.PlayArrow,
                        isPrimary = true,
                        testTag = "btn_new_game",
                        onClick = onNewGame
                    )

                    // 2. CONTINUE
                    MenuButton(
                        title = "CONTINUE",
                        subtitle = "Resume Latest Checkpoint",
                        icon = Icons.Default.FastForward,
                        isPrimary = false,
                        testTag = "btn_continue",
                        onClick = onContinue
                    )

                    // 3. LOAD GAME
                    MenuButton(
                        title = "LOAD GAME",
                        subtitle = "Timeline Archive & Save Slots",
                        icon = Icons.Default.FolderOpen,
                        isPrimary = false,
                        testTag = "btn_load_game",
                        onClick = { onNavigate(AppScreen.SAVE_LOAD) }
                    )

                    // 4. Row: SETTINGS & ACHIEVEMENTS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MenuSmallButton(
                            title = "SETTINGS",
                            icon = Icons.Default.Tune,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_settings",
                            onClick = { onNavigate(AppScreen.SETTINGS) }
                        )
                        MenuSmallButton(
                            title = "ACHIEVEMENTS",
                            icon = Icons.Default.EmojiEvents,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_achievements",
                            onClick = { onNavigate(AppScreen.ACHIEVEMENTS) }
                        )
                    }

                    // 5. Row: MEMORIES & CREDITS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MenuSmallButton(
                            title = "MEMORIES",
                            icon = Icons.Default.AutoAwesome,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_memories",
                            onClick = { onNavigate(AppScreen.MEMORY_ARCHIVE) }
                        )
                        MenuSmallButton(
                            title = "CREDITS",
                            icon = Icons.Default.Info,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_credits",
                            onClick = { onNavigate(AppScreen.CREDITS) }
                        )
                    }

                    // 6. EXIT
                    MenuButton(
                        title = "EXIT",
                        subtitle = "Quit to Android System",
                        icon = Icons.Default.PowerSettingsNew,
                        isPrimary = false,
                        testTag = "btn_exit",
                        onClick = { (context as? Activity)?.finish() }
                    )
                }
            }
        } else {
            // Portrait / Compact Adaptive Layout
            val logoSize = (screenWidth * 0.36f).coerceIn(100.dp, 140.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(logoSize)
                ) {
                    Box(
                        modifier = Modifier
                            .size(logoSize)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        CrimsonPrimary.copy(alpha = glowAlpha * 0.45f),
                                        CyanAccent.copy(alpha = glowAlpha * 0.25f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )

                    Image(
                        painter = painterResource(id = R.drawable.img_unsaved_logo),
                        contentDescription = "UNSAVED Official Game Logo",
                        modifier = Modifier
                            .size(logoSize * 0.90f)
                            .clip(CircleShape)
                            .border(
                                width = 1.5.dp,
                                brush = Brush.sweepGradient(
                                    listOf(CrimsonPrimary, CyanAccent, CrimsonPrimary)
                                ),
                                shape = CircleShape
                            )
                            .shadow(14.dp, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = "A STORY • A MYSTERY • A SECOND CHANCE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanAccent.copy(alpha = glowAlpha),
                        letterSpacing = 2.0.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "“Some memories were never meant to be deleted.”",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MenuButton(
                        title = "NEW GAME",
                        subtitle = "Awaken at Nocturne Station",
                        icon = Icons.Default.PlayArrow,
                        isPrimary = true,
                        testTag = "btn_new_game",
                        onClick = onNewGame
                    )

                    MenuButton(
                        title = "CONTINUE",
                        subtitle = "Resume Latest Checkpoint",
                        icon = Icons.Default.FastForward,
                        isPrimary = false,
                        testTag = "btn_continue",
                        onClick = onContinue
                    )

                    MenuButton(
                        title = "LOAD GAME",
                        subtitle = "Timeline Archive & Save Slots",
                        icon = Icons.Default.FolderOpen,
                        isPrimary = false,
                        testTag = "btn_load_game",
                        onClick = { onNavigate(AppScreen.SAVE_LOAD) }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        MenuSmallButton(
                            title = "SETTINGS",
                            icon = Icons.Default.Tune,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_settings",
                            onClick = { onNavigate(AppScreen.SETTINGS) }
                        )
                        MenuSmallButton(
                            title = "ACHIEVEMENTS",
                            icon = Icons.Default.EmojiEvents,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_achievements",
                            onClick = { onNavigate(AppScreen.ACHIEVEMENTS) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        MenuSmallButton(
                            title = "MEMORIES",
                            icon = Icons.Default.AutoAwesome,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_memories",
                            onClick = { onNavigate(AppScreen.MEMORY_ARCHIVE) }
                        )
                        MenuSmallButton(
                            title = "CREDITS",
                            icon = Icons.Default.Info,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_credits",
                            onClick = { onNavigate(AppScreen.CREDITS) }
                        )
                    }

                    MenuButton(
                        title = "EXIT",
                        subtitle = "Quit to Android System",
                        icon = Icons.Default.PowerSettingsNew,
                        isPrimary = false,
                        testTag = "btn_exit",
                        onClick = { (context as? Activity)?.finish() }
                    )
                }
            }
        }
    }
}

@Composable
fun MenuButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isPrimary: Boolean = false,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "btnScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .scale(scale)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isPrimary) 1.5.dp else 1.dp,
                color = if (isPrimary) CrimsonGlow.copy(alpha = 0.85f) else CharcoalBorder.copy(alpha = 0.7f),
                shape = RoundedCornerShape(10.dp)
            )
            .testTag(testTag),
        color = if (isPrimary) CharcoalSurfaceVariant else CharcoalCard,
        shadowElevation = if (isPrimary) 6.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isPrimary) CrimsonPrimary.copy(alpha = 0.25f) else CharcoalSurface)
                        .border(1.dp, if (isPrimary) CrimsonGlow else CharcoalBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isPrimary) CrimsonGlow else CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 1.2.sp,
                            fontSize = 13.sp
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondary,
                            fontSize = 10.sp
                        ),
                        maxLines = 1
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = if (isPrimary) CrimsonGlow else TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun MenuSmallButton(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "smallBtnScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .height(44.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, CharcoalBorder.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
            .testTag(testTag),
        color = CharcoalCard,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = CyanAccent,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 0.8.sp,
                    fontSize = 11.5.sp
                )
            )
        }
    }
}
