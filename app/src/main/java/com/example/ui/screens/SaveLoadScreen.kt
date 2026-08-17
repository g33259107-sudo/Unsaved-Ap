package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.SaveSlotEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SaveLoadScreen(
    saveSlots: List<SaveSlotEntity>,
    onSaveToSlot: (slotId: Int, slotName: String) -> Unit,
    onLoadSlot: (SaveSlotEntity) -> Unit,
    onDeleteSlot: (slotId: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Load Game, 1 = Save Game
    val slotsMap = remember(saveSlots) { saveSlots.associateBy { it.slotId } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CharcoalSurface)
                    .border(1.dp, CharcoalBorder, RoundedCornerShape(8.dp))
                    .testTag("btn_saveload_back")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }

            Text(
                text = "TIMELINE ARCHIVE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 1.5.sp
                )
            )

            Spacer(modifier = Modifier.width(42.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Mode Switcher (Load Game / Save Game)
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = CharcoalSurface,
            contentColor = CyanAccent,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, CharcoalBorder, RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("LOAD TIMELINE", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_load_game")
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("SAVE TIMELINE", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("tab_save_game")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Auto Save Slot
            val autoSave = slotsMap[0]
            item {
                SaveSlotCard(
                    slotId = 0,
                    slotDefaultName = "Auto Save Checkpoint",
                    slotData = autoSave,
                    isSaveMode = activeTab == 1,
                    canSaveToSlot = false, // Auto-save is system managed
                    onSave = { onSaveToSlot(0, "Auto Save") },
                    onLoad = { autoSave?.let { onLoadSlot(it) } },
                    onDelete = { onDeleteSlot(0) }
                )
            }

            // Manual Slots 1 to 5
            items(5) { index ->
                val slotId = index + 1
                val slot = slotsMap[slotId]
                SaveSlotCard(
                    slotId = slotId,
                    slotDefaultName = "Slot $slotId",
                    slotData = slot,
                    isSaveMode = activeTab == 1,
                    canSaveToSlot = true,
                    onSave = { onSaveToSlot(slotId, "Manual Save Slot $slotId") },
                    onLoad = { slot?.let { onLoadSlot(it) } },
                    onDelete = { onDeleteSlot(slotId) }
                )
            }
        }
    }
}

@Composable
fun SaveSlotCard(
    slotId: Int,
    slotDefaultName: String,
    slotData: SaveSlotEntity?,
    isSaveMode: Boolean,
    canSaveToSlot: Boolean,
    onSave: () -> Unit,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    val isPopulated = slotData != null && slotData.isPopulated

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (slotId == 0) CyanAccent.copy(alpha = 0.6f) else CharcoalBorder,
                RoundedCornerShape(14.dp)
            )
            .testTag("save_slot_$slotId"),
        colors = CardDefaults.cardColors(containerColor = CharcoalCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (slotId == 0) Icons.Default.CloudSync else Icons.Default.Save,
                        contentDescription = null,
                        tint = if (slotId == 0) CyanAccent else MemoryGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPopulated) slotData!!.slotName else slotDefaultName,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                if (isPopulated) {
                    val dateFormatted = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                        .format(Date(slotData!!.updatedTimestamp))
                    Text(
                        text = dateFormatted,
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                } else {
                    Text(
                        text = "EMPTY SLOT",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }
            }

            if (isPopulated) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = slotData!!.locationName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = slotData.storyProgressTitle,
                            style = MaterialTheme.typography.labelSmall.copy(color = CyanAccent)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        val minutes = slotData.playTimeSeconds / 60
                        val seconds = slotData.playTimeSeconds % 60
                        Text(
                            text = "Playtime: ${minutes}m ${seconds}s",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                        val memCount = slotData.discoveredMemoryIds.split(",").filter { it.isNotBlank() }.size
                        Text(
                            text = "Memories: $memCount / 7",
                            style = MaterialTheme.typography.labelSmall.copy(color = MemoryGold)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPopulated && !isSaveMode) {
                    Button(
                        onClick = onLoad,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                    ) {
                        Text("LOAD", color = CharcoalDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                } else if (isSaveMode && canSaveToSlot) {
                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPopulated) CrimsonPrimary else CyanAccent
                        ),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                    ) {
                        Text(
                            if (isPopulated) "OVERWRITE" else "SAVE HERE",
                            color = if (isPopulated) TextPrimary else CharcoalDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
