package com.kawaki.musicplayer.ui.screens.home

import android.Manifest
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.kawaki.musicplayer.player.PlayerState
import com.kawaki.musicplayer.ui.components.AudioCards
import com.kawaki.musicplayer.ui.components.LoadingComp
import com.kawaki.musicplayer.ui.components.PlayerContent
import com.kawaki.musicplayer.ui.components.checkStoragePermission

@ExperimentalMaterial3Api
@UnstableApi
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, "Enable storage permission in settings", Toast.LENGTH_LONG)
                    .show()
            }
        }
    )

    val audioListState = viewModel.audioList.collectAsState(initial = listOf())

    val playerState = viewModel.playerState.collectAsState()

    LaunchedEffect(key1 = true) {
        if (!checkStoragePermission(context)) {
            if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    val sheetState = rememberBottomSheetScaffoldState()
    val currentPosition = remember { mutableLongStateOf(0L) }
    LaunchedEffect(key1 = viewModel.mExoPlayer) {
        viewModel.currentTime { currentPosition.longValue = it }
    }

    val selectedIndex = remember(viewModel.mExoPlayer) { mutableIntStateOf(0) }
    val mediaItemList = remember(viewModel.mExoPlayer) { mutableListOf<MediaItem>() }
    LaunchedEffect(key1 = viewModel.mExoPlayer) {
        audioListState.value.forEach { audio ->
            mediaItemList.add(MediaItem.fromUri(audio.uri))
        }
    }

    if (audioListState.value.isNotEmpty() && mediaItemList.isNotEmpty()) {
        /** On tap index */
        val selectedTrack = remember(viewModel.mExoPlayer) { mutableStateOf(audioListState.value.first()) }

        /** On every first Launch
         * To add music to the playlist at first launch
         */
        LaunchedEffect(key1 = true) {
            if (audioListState.value.isNotEmpty()) {
                viewModel.setMediaItemList(mediaItemList)
            }
        }
        LaunchedEffect(key1 = currentPosition.longValue) {
            val dynamicIndex = if (viewModel.mExoPlayer.currentMediaItemIndex != 0) viewModel.mExoPlayer.currentMediaItemIndex else selectedIndex.intValue
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
                    duration = currentPosition.longValue,
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