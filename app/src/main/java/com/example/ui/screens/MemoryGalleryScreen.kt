package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemoryFragment
import com.example.story.StoryData
import com.example.ui.theme.*

@Composable
fun MemoryGalleryScreen(
    discoveredIds: Set<String>,
    onSelectMemory: (MemoryFragment) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCount = StoryData.ALL_MEMORIES.size
    val unlockedCount = discoveredIds.size

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CharcoalSurface)
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(8.dp))
                        .testTag("btn_memory_gallery_back")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "MEMORIES",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = 2.sp
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { (unlockedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .width(120.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (unlockedCount == totalCount) MemoryGold else CyanAccent,
                    trackColor = CharcoalSurfaceVariant
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "$unlockedCount / $totalCount RESTORED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (unlockedCount == totalCount) MemoryGold else CyanAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Landscape Memory Cards Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(StoryData.ALL_MEMORIES) { memory ->
                val isUnlocked = discoveredIds.contains(memory.id)
                MemoryCard(
                    memory = memory,
                    isUnlocked = isUnlocked,
                    onClick = { if (isUnlocked) onSelectMemory(memory) }
                )
            }
        }
    }
}

@Composable
private fun MemoryCard(
    memory: MemoryFragment,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isUnlocked) 1.2.dp else 1.dp,
                color = if (isUnlocked) (if (memory.isCrucial) MemoryGold.copy(alpha = 0.8f) else CyanAccent.copy(alpha = 0.6f)) else CharcoalBorder.copy(alpha = 0.4f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(enabled = isUnlocked) { onClick() }
            .testTag("memory_shard_${memory.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) CharcoalCard else CharcoalDark.copy(alpha = 0.7f)
        )
    ) {
        if (isUnlocked) {
            // Unlocked Crystal Memory Card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (memory.isCrucial) MemoryGold else CyanAccent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Shard #${memory.orderIndex}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (memory.isCrucial) MemoryGold else CyanAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Text(
                        text = memory.dateStamp,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    )
                }

                Text(
                    text = memory.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 12.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = memory.previewText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 10.5.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        } else {
            // Locked Silhouetted Memory Card
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6f)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0D111A), Color(0xFF07090E))
                        )
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked Memory",
                        tint = TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "LOCKED MEMORY #${memory.orderIndex}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = memory.location,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CharcoalBorder,
                            fontSize = 8.5.sp
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
