package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanGlow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun VirtualJoystick(
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    knobRadius: Dp = 28.dp,
    onMove: (dx: Float, dy: Float) -> Unit
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(size)
            .testTag("virtual_joystick")
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.toPx() / 2, size.toPx() / 2)
                        val dragVector = offset - center
                        val maxRadius = (size.toPx() - knobRadius.toPx() * 2) / 2
                        val dist = sqrt(dragVector.x * dragVector.x + dragVector.y * dragVector.y)
                        val clamped = if (dist > maxRadius) {
                            dragVector * (maxRadius / dist)
                        } else {
                            dragVector
                        }
                        knobOffset = clamped
                        onMove(clamped.x / maxRadius, clamped.y / maxRadius)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val maxRadius = (size.toPx() - knobRadius.toPx() * 2) / 2
                        val newOffset = knobOffset + dragAmount
                        val dist = sqrt(newOffset.x * newOffset.x + newOffset.y * newOffset.y)
                        val clamped = if (dist > maxRadius) {
                            newOffset * (maxRadius / dist)
                        } else {
                            newOffset
                        }
                        knobOffset = clamped
                        onMove(clamped.x / maxRadius, clamped.y / maxRadius)
                    },
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2, this.size.height / 2)
            val outerRadius = this.size.width / 2 - 4.dp.toPx()

            // Outer Base Ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x33101827), Color(0x660A0C10), Color(0x99000000)),
                    center = center,
                    radius = outerRadius
                ),
                radius = outerRadius,
                center = center
            )

            // Neon Cyan Ring Accent
            drawCircle(
                color = CyanAccent.copy(alpha = 0.4f),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Center Target Crosshair Dots
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = 4.dp.toPx(),
                center = center
            )

            // Inner Knob
            val currentKnobCenter = center + knobOffset
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CyanGlow.copy(alpha = 0.9f), CyanAccent.copy(alpha = 0.7f), Color(0xFF00384D)),
                    center = currentKnobCenter,
                    radius = knobRadius.toPx()
                ),
                radius = knobRadius.toPx(),
                center = currentKnobCenter
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = knobRadius.toPx(),
                center = currentKnobCenter,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}
