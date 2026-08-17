package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.UnsavedTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private var gameViewModel: GameViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnsavedTheme {
                val vm: GameViewModel = viewModel()
                gameViewModel = vm
                UnsavedApp(viewModel = vm)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        gameViewModel?.audioEngine?.start()
    }

    override fun onPause() {
        super.onPause()
        gameViewModel?.audioEngine?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameViewModel?.audioEngine?.stop()
    }
}

@Composable
fun UnsavedApp(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CharcoalDark
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(
                targetState = uiState.currentScreen,
                animationSpec = tween(400),
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    AppScreen.MAIN_MENU -> {
                        MainMenuScreen(
                            onNewGame = { viewModel.startNewGame() },
                            onContinue = { viewModel.continueGame() },
                            onNavigate = { dest -> viewModel.navigateTo(dest) }
                        )
                    }

                    AppScreen.OPENING_CINEMATIC -> {
                        OpeningCinematicScreen(
                            onFinish = { viewModel.finishOpeningCinematic() }
                        )
                    }

                    AppScreen.PLAYING -> {
                        GamePlayScreen(
                            state = uiState,
                            onMovePlayer = { dx, dy, running -> viewModel.movePlayer(dx, dy, running) },
                            onStealthStateChanged = { tension, noise, isCrouch, isHide ->
                                viewModel.updateStealthState(tension, noise, isCrouch, isHide)
                            },
                            onInteract = { viewModel.interactWithNearby() },
                            onTapDialogue = { viewModel.advanceDialogue() },
                            onSelectChoice = { choice -> viewModel.selectDialogueChoice(choice) },
                            onNavigate = { dest -> viewModel.navigateTo(dest) },
                            onInspectMemoryDismiss = { viewModel.dismissMemoryInspectModal() },
                            onDismissFlashback = { viewModel.dismissFlashback() }
                        )
                    }

                    AppScreen.INVENTORY -> {
                        InventoryScreen(
                            items = uiState.inventoryItems,
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.MEMORY_ARCHIVE -> {
                        MemoryGalleryScreen(
                            discoveredIds = uiState.discoveredMemoryIds,
                            onSelectMemory = { memory -> viewModel.inspectMemory(memory) },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.SAVE_LOAD -> {
                        SaveLoadScreen(
                            saveSlots = uiState.saveSlots,
                            onSaveToSlot = { slotId, name -> viewModel.saveGame(slotId, name) },
                            onLoadSlot = { slot -> viewModel.loadGame(slot) },
                            onDeleteSlot = { slotId -> viewModel.deleteSaveSlot(slotId) },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.MAP -> {
                        MapScreen(
                            currentLocation = uiState.currentLocationScene.locationId,
                            discoveredMemoriesCount = uiState.discoveredMemoryIds.size,
                            onTravelToLocation = { locId ->
                                viewModel.transitionToLocation(locId)
                                viewModel.navigateTo(AppScreen.PLAYING)
                            },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.PAUSE_MENU -> {
                        PauseMenuScreen(
                            currentLocation = uiState.currentLocationScene.locationId,
                            discoveredMemoriesCount = uiState.discoveredMemoryIds.size,
                            playTimeSeconds = uiState.playTimeSeconds,
                            onNavigate = { dest -> viewModel.navigateTo(dest) },
                            onResume = { viewModel.resumeGame() },
                            onRestartCheckpoint = { viewModel.restartCheckpoint() }
                        )
                    }

                    AppScreen.SETTINGS -> {
                        SettingsScreen(
                            currentSettings = uiState.settings,
                            onSaveSettings = { settings -> viewModel.updateSettings(settings) },
                            onResetProgress = { viewModel.resetProgress() },
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.ACHIEVEMENTS -> {
                        AchievementsScreen(
                            achievements = uiState.achievements,
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    AppScreen.PUZZLE -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            GamePlayScreen(
                                state = uiState,
                                onMovePlayer = { dx, dy, running -> viewModel.movePlayer(dx, dy, running) },
                                onStealthStateChanged = { tension, noise, isCrouch, isHide ->
                                    viewModel.updateStealthState(tension, noise, isCrouch, isHide)
                                },
                                onInteract = { viewModel.interactWithNearby() },
                                onTapDialogue = { viewModel.advanceDialogue() },
                                onSelectChoice = { choice -> viewModel.selectDialogueChoice(choice) },
                                onNavigate = { dest -> viewModel.navigateTo(dest) },
                                onInspectMemoryDismiss = { viewModel.dismissMemoryInspectModal() }
                            )
                            PuzzleModal(
                                puzzleId = uiState.activePuzzleId ?: "PUZZLE_FUSE_1",
                                onSolved = { viewModel.onPuzzleSolved() },
                                onClose = { viewModel.closePuzzle() }
                            )
                        }
                    }

                    AppScreen.ENDING_CINEMATIC -> {
                        EndingScreen(
                            ending = uiState.endingReached,
                            memoriesDiscoveredCount = uiState.discoveredMemoryIds.size,
                            playTimeSeconds = uiState.playTimeSeconds,
                            empathyScore = uiState.empathyScore,
                            courageScore = uiState.courageScore,
                            onReturnToMenu = { viewModel.returnToMainMenu() }
                        )
                    }

                    AppScreen.CREDITS -> {
                        CreditsModal(
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }
            }
        }
    }
}
