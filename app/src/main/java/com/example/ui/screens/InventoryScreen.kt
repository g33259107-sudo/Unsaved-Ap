package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InventoryItem
import com.example.data.ItemCategory
import com.example.ui.theme.*

@Composable
fun InventoryScreen(
    items: List<InventoryItem>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("ALL", "KEY ITEMS", "MEMORY FRAGMENTS", "DOCUMENTS", "IMPORTANT OBJECTS")

    val filteredItems = remember(items, selectedCategoryIndex) {
        when (selectedCategoryIndex) {
            1 -> items.filter { it.category == ItemCategory.KEY_ITEM || it.category == ItemCategory.PUZZLE_TOOL }
            2 -> items.filter { it.category == ItemCategory.KEEPSAKE }
            3 -> items.filter { it.category == ItemCategory.DOCUMENT }
            4 -> items.filter { it.category == ItemCategory.AUDIO_TAPE || it.category == ItemCategory.KEY_ITEM }
            else -> items
        }
    }

    var selectedItem by remember(filteredItems) { mutableStateOf(filteredItems.firstOrNull() ?: items.firstOrNull()) }

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
                        .testTag("btn_inventory_back")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "INVENTORY",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = 2.sp
                    )
                )
            }

            // Category Filter Chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                categories.forEachIndexed { index, catName ->
                    val isSelected = selectedCategoryIndex == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) CyanAccent else CharcoalSurface)
                            .border(1.dp, if (isSelected) CyanGlow else CharcoalBorder, RoundedCornerShape(6.dp))
                            .clickable { selectedCategoryIndex = index }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = catName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) CharcoalDark else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Two-Column Landscape Grid (Left: Item Grid, Right: Compact Cinematic Inspection Card)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left: Item Grid
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight()
            ) {
                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No items found in this section.", color = TextMuted, fontSize = 12.sp)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredItems) { item ->
                            val isSelected = selectedItem?.id == item.id
                            InventoryGridCard(
                                item = item,
                                isSelected = isSelected,
                                onClick = { selectedItem = item }
                            )
                        }
                    }
                }
            }

            // Right: Selected Item Inspection View
            Box(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
            ) {
                if (selectedItem != null) {
                    val item = selectedItem!!
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Badge(containerColor = CharcoalSurfaceVariant) {
                                        Text(
                                            text = item.category.name.replace("_", " "),
                                            color = CyanAccent,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TextSecondary,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CharcoalDark)
                                        .border(1.dp, CharcoalBorder, RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                    ) {
                                    Text(
                                        text = "“${item.inspectionNotes}”",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MemoryGold,
                                            fontSize = 11.sp,
                                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                        )
                                    )
                                }
                            }

                            if (item.audioTranscript != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Transcript: ${item.audioTranscript}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Select an item to inspect details", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun InventoryGridCard(
    item: InventoryItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.5.dp,
                if (isSelected) CyanAccent else CharcoalBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .testTag("item_${item.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CharcoalSurfaceVariant else CharcoalCard
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = getItemIcon(item.category),
                contentDescription = item.name,
                tint = if (isSelected) CyanAccent else TextSecondary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) TextPrimary else TextSecondary,
                    fontSize = 10.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun getItemIcon(category: ItemCategory): ImageVector {
    return when (category) {
        ItemCategory.KEY_ITEM -> Icons.Default.VpnKey
        ItemCategory.KEEPSAKE -> Icons.Default.AutoAwesome
        ItemCategory.DOCUMENT -> Icons.Default.Description
        ItemCategory.PUZZLE_TOOL -> Icons.Default.Build
        ItemCategory.AUDIO_TAPE -> Icons.Default.Mic
    }
}
