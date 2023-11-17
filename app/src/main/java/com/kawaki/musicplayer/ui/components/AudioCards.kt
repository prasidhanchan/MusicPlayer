package com.kawaki.musicplayer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.kawaki.musicplayer.R
import com.kawaki.musicplayer.Utils
import com.kawaki.musicplayer.model.Audio
import com.kawaki.musicplayer.ui.screens.home.HomeScreenViewModel

enum class PainterState {
    LOADING,
    ERROR,
    SUCCESS
}

@UnstableApi
@Composable
fun AudioCards(
    audioList: List<Audio>,
    audio: Audio,
    viewModel: HomeScreenViewModel,
    mediaItemList: List<MediaItem>,
    selectedIndex: (Int) -> Unit
) {
    val mAudio = remember(audio) { mutableStateOf(audio) }
    val painterState = remember(audio) { mutableStateOf(PainterState.LOADING) }

    val dynamicColor = if (isSystemInDarkTheme()) Utils.offBlack else Utils.offWhite

    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = StaggeredGridCells.Fixed(1)
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            if (audioList.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    when (painterState.value) {
                        PainterState.LOADING, PainterState.SUCCESS -> {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp),
                                model = mAudio.value.albumArt,
                                contentDescription = "Album Art",
                                contentScale = ContentScale.FillBounds,
                                onError = { painterState.value = PainterState.ERROR },
                                onLoading = { painterState.value = PainterState.LOADING }
                            )
                        }

                        PainterState.ERROR -> {
                            Icon(
                                modifier = Modifier
                                    .scale(0.60f)
                                    .height(400.dp)
                                    .padding(top = 80.dp),
                                painter = painterResource(id = R.drawable.music),
                                contentDescription = "Album Art"
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        dynamicColor
                                    ),
                                    startY = 0f
                                )
                            )
                    )
                }
            }
        }
        item(span = StaggeredGridItemSpan.FullLine) {
            Spacer(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                dynamicColor,
                                Color.Transparent
                            ),
                            startY = 0f
                        )
                    )
            )
        }
        itemsIndexed(audioList) { index, audio ->
            AudioCardItem(
                audio = audio,
                viewModel = viewModel,
                mediaItemList = mediaItemList,
                selectedIndex = index,
                selectedTrack = {
                    selectedIndex(index)
                }
            )
        }
    }
}

@UnstableApi
@Composable
fun AudioCardItem(
    audio: Audio,
    viewModel: HomeScreenViewModel,
    mediaItemList: List<MediaItem>,
    selectedIndex: Int,
    selectedTrack: (Audio) -> Unit
) {
    var painterState by remember { mutableStateOf(PainterState.LOADING) }
    val dynamicTint = if (isSystemInDarkTheme()) Color.Black else Color.White

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clickable {
                viewModel.setMediaItem(
                    mediaItem = MediaItem.fromUri(audio.uri),
                    mediaItemList = mediaItemList,
                    selectedIndex = selectedIndex
                )
                selectedTrack(audio)
            },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                when(painterState) {
                    PainterState.LOADING, PainterState.SUCCESS -> {
                        AsyncImage(
                            model = audio.albumArt,
                            contentDescription = audio.displayName,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .fillMaxWidth(0.16f)
                                .fillMaxHeight(),
                            contentScale = ContentScale.FillBounds,
                            onError = { painterState = PainterState.ERROR },
                            onLoading = { painterState = PainterState.LOADING}
                        )
                    }
                    PainterState.ERROR -> {
                        Surface(
                            modifier = Modifier
                                .size(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.music),
                                contentDescription = "Music",
                                modifier = Modifier.padding(8.dp),
                                tint = dynamicTint
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.80f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = audio.displayName,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = audio.artist,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            Text(
                text = audio.duration.toLong().formatMinSec(),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}