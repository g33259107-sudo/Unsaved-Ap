package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun CreditsModal(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "creditsGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Pane: Official UNSAVED Logo Badge & Core Developed Tag
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(160.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
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
                        contentDescription = "UNSAVED Official Logo",
                        modifier = Modifier
                            .size(145.dp)
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

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "UNSAVED",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = CrimsonGlow,
                        letterSpacing = 4.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Developed by",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSecondary,
                        letterSpacing = 1.5.sp
                    )
                )

                Text(
                    text = "Satyam Gupta",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                )
            }

            // Right Pane: Credits Card & Back Button
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CharcoalCard)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CreditEntry(role = "GAME CREATOR & LEAD DESIGNER", name = "Satyam Gupta")
                        Spacer(modifier = Modifier.height(12.dp))
                        CreditEntry(role = "GENRE & DIRECTION", name = "3D Story Horror & Mystery Adventure")
                        Spacer(modifier = Modifier.height(12.dp))
                        CreditEntry(role = "SOUNDTRACK & PROCEDURAL AUDIO", name = "Atmospheric Noir Sound Engine")
                        Spacer(modifier = Modifier.height(12.dp))
                        CreditEntry(role = "ENGINE & 3D RENDERING", name = "Custom OpenGL ES 2.0 Pipeline")
                        Spacer(modifier = Modifier.height(12.dp))
                        CreditEntry(role = "PLATFORM & SAVE PERSISTENCE", name = "Room Database & Jetpack Compose")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_close_credits"),
                    colors = ButtonDefaults.buttonColors(containerColor = CharcoalSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("BACK TO MAIN MENU", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun CreditEntry(role: String, name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = role,
            style = MaterialTheme.typography.labelSmall.copy(
                color = CyanAccent,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp
            )
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        )
    }
}
