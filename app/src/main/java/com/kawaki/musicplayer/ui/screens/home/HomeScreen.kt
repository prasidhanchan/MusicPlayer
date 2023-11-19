package com.kawaki.musicplayer.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.kawaki.musicplayer.player.PlayerState
import com.kawaki.musicplayer.ui.components.AudioCards
import com.kawaki.musicplayer.ui.components.LoadingComp
import com.kawaki.musicplayer.ui.components.PlayerContent

@ExperimentalMaterial3Api
@UnstableApi
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = hiltViewModel()) {

    val audioListState = viewModel.audioList.collectAsState()

    val playerState = viewModel.playerState.collectAsState()

    val isLoading = viewModel.isLoading.collectAsState()

    val sheetState = rememberBottomSheetScaffoldState()
    val currentPosition = viewModel.currentPosition.collectAsState()

    /** Current MediaItem Index from ExoPlayer */
    val currentMediaItemIndex = viewModel.mExoPlayer.currentMediaItemIndex

    /** ExoPlayer */
    val exoPlayer = viewModel.mExoPlayer

    /** On tap index */
    val selectedIndex = remember(viewModel.mExoPlayer) { mutableIntStateOf(0) }
    val mediaItemList = remember(true) { mutableListOf<MediaItem>() }
    LaunchedEffect(key1 = audioListState.value) {
        audioListState.value.forEach { audio ->
            mediaItemList.add(MediaItem.fromUri(audio.uri))
        }
    }

    if (!isLoading.value) {

        val selectedTrack = remember(exoPlayer) { mutableStateOf(audioListState.value.first()) }

        /** On every first Launch
         * To add music to the playlist at first launch
         */
        LaunchedEffect(key1 = true) {
            if (audioListState.value.isNotEmpty()) {
                viewModel.setMediaItemList(mediaItemList)
            }
        }
        LaunchedEffect(key1 = currentPosition.value) {
            val dynamicIndex =
                if (currentMediaItemIndex != 0) currentMediaItemIndex else selectedIndex.intValue
            selectedTrack.value = audioListState.value[dynamicIndex]
        }

        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            sheetContent = {
                PlayerContent(
                    audio = selectedTrack.value,
                    previous = { viewModel.seekToPrevious() },
                    next = { viewModel.seekToNext() },
                    playPause = { viewModel.playOrPause() },
                    duration = currentPosition.value,
                    onSeekChange = { viewModel.seekTo(it.toLong()) },
                    onIndexChange = { selectedIndex.intValue = it },
                    totalDuration = selectedTrack.value.duration.toLong(),
                    isPlaying = playerState.value == PlayerState.IsPLAYING,
                    sheetScaffoldState = sheetState,
                    viewModel = viewModel
                )
            },
            scaffoldState = sheetState,
            sheetPeekHeight = 100.dp,
            sheetDragHandle = { }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AudioCards(
                    audioList = audioListState.value,
                    audio = selectedTrack.value,
                    viewModel = viewModel,
                    mediaItemList = mediaItemList
                ) {
                    selectedIndex.intValue = it
                }
            }
        }
    } else {
        LoadingComp()
    }
}