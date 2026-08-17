package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.EntityType
import com.example.data.InteractiveEntity
import com.example.data.LocationId
import com.example.story.LocationScene
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class WeatherParticle(
    var x: Float,
    var y: Float,
    var speed: Float,
    var length: Float,
    var alpha: Float
)

@Composable
fun CinematicCanvasView(
    scene: LocationScene,
    playerX: Float,
    playerY: Float,
    isRunning: Boolean,
    activeNearbyEntity: InteractiveEntity?,
    modifier: Modifier = Modifier
) {
    // Pulse animation for memory crystals and beacon points
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beaconPulse"
    )

    val rainOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainFall"
    )

    // Generate weather particles
    val particles = remember {
        List(70) {
            WeatherParticle(
                x = Random.nextFloat() * 1600f,
                y = Random.nextFloat() * 1200f,
                speed = 12f + Random.nextFloat() * 10f,
                length = 14f + Random.nextFloat() * 16f,
                alpha = 0.15f + Random.nextFloat() * 0.35f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize().testTag("exploration_canvas")) {
        val viewWidth = size.width
        val viewHeight = size.height

        // Camera tracking lerp to keep player centered in viewport safely across all screen ratios
        val cameraOffsetX = if (scene.worldWidth > viewWidth) {
            (viewWidth / 2 - playerX).coerceIn(viewWidth - scene.worldWidth, 0f)
        } else {
            (viewWidth - scene.worldWidth) / 2f
        }

        val cameraOffsetY = if (scene.worldHeight > viewHeight) {
            (viewHeight / 2 - playerY).coerceIn(viewHeight - scene.worldHeight, 0f)
        } else {
            (viewHeight - scene.worldHeight) / 2f
        }

        // 1. Draw World Background Canvas Grid / Atmosphere
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF080A0F), Color(0xFF0F141E), Color(0xFF0A0C10)),
                startY = 0f,
                endY = scene.worldHeight
            ),
            topLeft = Offset(cameraOffsetX, cameraOffsetY),
            size = Size(scene.worldWidth, scene.worldHeight)
        )

        // Draw architectural floor tiles & gridlines
        val tileSize = 120f
        var gridX = 0f
        while (gridX <= scene.worldWidth) {
            drawLine(
                color = Color(0x143B82F6),
                start = Offset(gridX + cameraOffsetX, cameraOffsetY),
                end = Offset(gridX + cameraOffsetX, scene.worldHeight + cameraOffsetY),
                strokeWidth = 1.dp.toPx()
            )
            gridX += tileSize
        }

        var gridY = 0f
        while (gridY <= scene.worldHeight) {
            drawLine(
                color = Color(0x143B82F6),
                start = Offset(cameraOffsetX, gridY + cameraOffsetY),
                end = Offset(scene.worldWidth + cameraOffsetX, gridY + cameraOffsetY),
                strokeWidth = 1.dp.toPx()
            )
            gridY += tileSize
        }

        // Draw Location-specific landmark structures
        drawSceneLandmarks(scene, cameraOffsetX, cameraOffsetY)

        // 2. Draw Interactive Entities & Memory Beacons
        for (entity in scene.entities) {
            val entityScreenX = entity.x + cameraOffsetX
            val entityScreenY = entity.y + cameraOffsetY
            val isTargeted = activeNearbyEntity?.id == entity.id

            when (entity.type) {
                EntityType.MEMORY_SHARD -> {
                    // Pulsing Cyan / Amber Memory Crystal
                    val baseColor = if (entity.id == "MEM_07") MemoryGold else CyanAccent
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(baseColor.copy(alpha = 0.5f * pulseScale), Color.Transparent),
                            center = Offset(entityScreenX, entityScreenY),
                            radius = entity.radius * pulseScale
                        ),
                        radius = entity.radius * pulseScale,
                        center = Offset(entityScreenX, entityScreenY)
                    )

                    // Diamond Shard Shape
                    val path = Path().apply {
                        moveTo(entityScreenX, entityScreenY - 20f * pulseScale)
                        lineTo(entityScreenX + 14f * pulseScale, entityScreenY)
                        lineTo(entityScreenX, entityScreenY + 20f * pulseScale)
                        lineTo(entityScreenX - 14f * pulseScale, entityScreenY)
                        close()
                    }
                    drawPath(path, color = baseColor)
                    drawPath(path, color = Color.White, style = Stroke(width = 2.dp.toPx()))
                }

                EntityType.NPC -> {
                    // Ethereal Apparition Figure (Iris Chen or Elena Vance)
                    val npcColor = if (entity.id.contains("IRIS")) CrimsonGlow else if (entity.id.contains("ELENA")) CyanGlow else MemoryAmber
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(npcColor.copy(alpha = 0.45f), Color.Transparent),
                            center = Offset(entityScreenX, entityScreenY),
                            radius = 38f
                        ),
                        radius = 38f,
                        center = Offset(entityScreenX, entityScreenY)
                    )
                    // Head & Torso
                    drawCircle(
                        color = npcColor.copy(alpha = 0.9f),
                        radius = 12f,
                        center = Offset(entityScreenX, entityScreenY - 14f)
                    )
                    drawRoundRect(
                        color = npcColor.copy(alpha = 0.75f),
                        topLeft = Offset(entityScreenX - 10f, entityScreenY - 2f),
                        size = Size(20f, 24f),
                        cornerRadius = CornerRadius(6f, 6f)
                    )
                }

                EntityType.PUZZLE_STATION -> {
                    // Amber Gear / Terminal Station
                    drawCircle(
                        color = MemoryAmber.copy(alpha = 0.25f),
                        radius = 28f,
                        center = Offset(entityScreenX, entityScreenY)
                    )
                    drawCircle(
                        color = MemoryAmber,
                        radius = 24f,
                        center = Offset(entityScreenX, entityScreenY),
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 6f,
                        center = Offset(entityScreenX, entityScreenY)
                    )
                }

                EntityType.DOOR_PORTAL -> {
                    // Neon Gateway Doorway
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(CyanAccent.copy(alpha = 0.6f), BlueDeep.copy(alpha = 0.4f)),
                            startY = entityScreenY - 35f,
                            endY = entityScreenY + 35f
                        ),
                        topLeft = Offset(entityScreenX - 25f, entityScreenY - 35f),
                        size = Size(50f, 70f),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                    drawRoundRect(
                        color = CyanAccent,
                        topLeft = Offset(entityScreenX - 25f, entityScreenY - 35f),
                        size = Size(50f, 70f),
                        cornerRadius = CornerRadius(10f, 10f),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                else -> {
                    drawCircle(
                        color = TextMuted.copy(alpha = 0.5f),
                        radius = 18f,
                        center = Offset(entityScreenX, entityScreenY)
                    )
                }
            }

            // Proximity indicator ring if near player
            if (isTargeted) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.8f),
                    radius = entity.radius + 6f,
                    center = Offset(entityScreenX, entityScreenY),
                    style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f)))
                )
            }
        }

        // 3. Draw Player Character (Noah)
        val playerScreenX = playerX + cameraOffsetX
        val playerScreenY = playerY + cameraOffsetY

        // Flashlight / Aura Light Cone
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x3300E5FF), Color(0x1100E5FF), Color.Transparent),
                center = Offset(playerScreenX, playerScreenY),
                radius = if (isRunning) 140f else 110f
            ),
            radius = if (isRunning) 140f else 110f,
            center = Offset(playerScreenX, playerScreenY)
        )

        // Shadow under player
        drawOval(
            color = Color(0x88000000),
            topLeft = Offset(playerScreenX - 18f, playerScreenY + 10f),
            size = Size(36f, 16f)
        )

        // Coat / Torso
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(playerScreenX - 12f, playerScreenY - 6f),
            size = Size(24f, 26f),
            cornerRadius = CornerRadius(6f, 6f)
        )

        // Crimson Scarf accent
        drawRoundRect(
            color = CrimsonPrimary,
            topLeft = Offset(playerScreenX - 9f, playerScreenY - 8f),
            size = Size(18f, 7f),
            cornerRadius = CornerRadius(3f, 3f)
        )

        // Head
        drawCircle(
            color = Color(0xFFE2E8F0),
            radius = 10f,
            center = Offset(playerScreenX, playerScreenY - 16f)
        )

        // 4. Draw Weather Rain Particle Overlay
        for (p in particles) {
            val px = (p.x + cameraOffsetX * 0.3f) % viewWidth
            val py = (p.y + rainOffset * (p.speed / 10f)) % viewHeight

            drawLine(
                color = Color(0x6693C5FD).copy(alpha = p.alpha),
                start = Offset(px, py),
                end = Offset(px - 4f, py + p.length),
                strokeWidth = 1.5.dp.toPx()
            )
        }

        // 5. Cinematic Vignette on Edges
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color.Transparent, Color(0x44000000), Color(0xCC05070A)),
                center = Offset(viewWidth / 2, viewHeight / 2),
                radius = viewWidth * 0.75f
            ),
            size = size
        )
    }
}

private fun DrawScope.drawSceneLandmarks(scene: LocationScene, offsetX: Float, offsetY: Float) {
    when (scene.locationId) {
        LocationId.NOCTURNE_STATION, LocationId.STATION_TRACKS -> {
            // Station Railway tracks
            drawLine(
                color = Color(0xFF334155),
                start = Offset(100f + offsetX, 700f + offsetY),
                end = Offset(1400f + offsetX, 700f + offsetY),
                strokeWidth = 6.dp.toPx()
            )
            drawLine(
                color = Color(0xFF334155),
                start = Offset(100f + offsetX, 750f + offsetY),
                end = Offset(1400f + offsetX, 750f + offsetY),
                strokeWidth = 6.dp.toPx()
            )
        }
        LocationId.RAINY_AVENUE, LocationId.APARTMENT_404 -> {
            // Rainy Alley Streetlamp post & brick walls
            drawRoundRect(
                color = Color(0xFF1E2430),
                topLeft = Offset(520f + offsetX, 150f + offsetY),
                size = Size(200f, 30f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Streetlamp glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x55F59E0B), Color(0x15F59E0B), Color.Transparent),
                    center = Offset(550f + offsetX, 420f + offsetY),
                    radius = 160f
                ),
                radius = 160f,
                center = Offset(550f + offsetX, 420f + offsetY)
            )
        }
        LocationId.CAFE_NOCTURNE -> {
            // Cafe Nocturne Bar Counter & Tables
            drawRoundRect(
                color = Color(0xFF2C221E),
                topLeft = Offset(350f + offsetX, 400f + offsetY),
                size = Size(240f, 60f),
                cornerRadius = CornerRadius(8f, 8f)
            )
            drawRoundRect(
                color = Color(0xFF3E2F28),
                topLeft = Offset(880f + offsetX, 320f + offsetY),
                size = Size(140f, 100f),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }
        else -> {}
    }
}
