package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun OpeningCinematicScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var loadProgress by remember { mutableFloatStateOf(0.15f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulseLogo")
    val logoGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (loadProgress < 1.0f) {
            delay(40)
            val elapsed = System.currentTimeMillis() - startTime
            loadProgress = (elapsed / 1800f).coerceIn(0.15f, 1.0f)
        }
        delay(300)
        onFinish()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0F1522),
                        Color(0xFF090D14),
                        Color(0xFF05070A)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        val isLandscape = maxWidth > maxHeight
        val screenHeight = maxHeight
        val screenWidth = maxWidth

        if (isLandscape) {
            val logoSize = (screenHeight * 0.45f).coerceIn(110.dp, 160.dp)

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Official UNSAVED Logo Badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(logoSize)
                        .padding(end = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(logoSize)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        CrimsonPrimary.copy(alpha = logoGlow * 0.45f),
                                        CyanAccent.copy(alpha = logoGlow * 0.25f),
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
                                1.5.dp,
                                Brush.sweepGradient(listOf(CrimsonPrimary, CyanAccent, CrimsonPrimary)),
                                CircleShape
                            )
                            .shadow(12.dp, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                // Right: Narrative & Loading Info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    Column {
                        Text(
                            text = "CHAPTER 1 • NOCTURNE STATION",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CrimsonGlow,
                                letterSpacing = 3.sp,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "The First Memory",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "“Some memories were never meant to be deleted.”",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }

                    // Loading Bar & Enter Button
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INITIALIZING 3D WORLD & LIGHTS...",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.2.sp
                                )
                            )
                            Text(
                                text = "${(loadProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        LinearProgressIndicator(
                            progress = { loadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = CyanAccent,
                            trackColor = CharcoalSurface
                        )

                        Button(
                            onClick = onFinish,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CharcoalSurfaceVariant,
                                contentColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .border(1.dp, CyanAccent.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .testTag("btn_enter_chapter1")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ENTER 3D WORLD",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = TextPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Portrait layout
            val logoSize = (screenWidth * 0.38f).coerceIn(100.dp, 140.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                        CrimsonPrimary.copy(alpha = logoGlow * 0.45f),
                                        CyanAccent.copy(alpha = logoGlow * 0.25f),
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
                                1.5.dp,
                                Brush.sweepGradient(listOf(CrimsonPrimary, CyanAccent, CrimsonPrimary)),
                                CircleShape
                            )
                            .shadow(12.dp, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = "CHAPTER 1 • NOCTURNE STATION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CrimsonGlow,
                        letterSpacing = 2.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "The First Memory",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "“Some memories were never meant to be deleted.”",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 11.5.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INITIALIZING 3D WORLD...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "${(loadProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    LinearProgressIndicator(
                        progress = { loadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CyanAccent,
                        trackColor = CharcoalSurface
                    )

                    Button(
                        onClick = onFinish,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CharcoalSurfaceVariant,
                            contentColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .border(1.dp, CyanAccent.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                            .testTag("btn_enter_chapter1")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ENTER 3D WORLD",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
