package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LocationId
import com.example.ui.theme.*

@Composable
fun MapScreen(
    currentLocation: LocationId,
    discoveredMemoriesCount: Int,
    onTravelToLocation: (LocationId) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val locations = listOf(
        Pair(LocationId.NOCTURNE_STATION, "Abandoned underground terminal where Ren awakened"),
        Pair(LocationId.RAINY_AVENUE, "Neon-lit misty street with glowing payphones & posters"),
        Pair(LocationId.APARTMENT_404, "Ren & Elena's forgotten residence filled with sketches"),
        Pair(LocationId.CAFE_NOCTURNE, "Intimate corner cafe with amber lights & Elena's notes"),
        Pair(LocationId.STATION_TRACKS, "Industrial railway line leading into the deep mist"),
        Pair(LocationId.MEMORY_ARCHIVE, "Crystalline sanctuary storing Elena's recordings"),
        Pair(LocationId.HILLTOP_OVERLOOK, "Summit overlooking the quiet city below dawn")
    )

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
                    .testTag("btn_map_back")
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }

            Text(
                text = "WORLD TOPOGRAPHY",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 1.5.sp
                )
            )

            Spacer(modifier = Modifier.width(42.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Connected zones in Ren's emotional journey. Discovered memories: $discoveredMemoriesCount/7",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(locations) { (locId, details) ->
                val isHere = currentLocation == locId

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.2.dp,
                            if (isHere) CyanAccent else CharcoalBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onTravelToLocation(locId) }
                        .testTag("map_loc_${locId.name}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isHere) CharcoalSurfaceVariant else CharcoalCard
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isHere) CyanAccent else CharcoalSurfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isHere) Icons.Default.MyLocation else Icons.Default.Place,
                                contentDescription = null,
                                tint = if (isHere) CharcoalDark else TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = locId.displayName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isHere) CyanAccent else TextPrimary
                                    )
                                )
                                if (isHere) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "CURRENT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CrimsonGlow,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            Text(
                                text = details,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = if (isHere) CyanAccent else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
