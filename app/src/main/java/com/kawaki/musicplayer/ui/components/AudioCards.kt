package com.kawaki.musicplayer.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.mutableIntStateOf
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
import coil.compose.AsyncImagePainter
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
                    if (painterState.value == PainterState.ERROR) {
                        Icon(
                            modifier = Modifier
                                .scale(0.60f)
                                .height(400.dp)
                                .padding(top = 80.dp),
                            painter = painterResource(id = R.drawable.music),
                            contentDescription = "Album Art"
                        )
                    } else {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            model = mAudio.value.albumArt,
                            contentDescription = "Album Art",
                            contentScale = ContentScale.FillBounds,
                            onState = { mPainterState ->
                                when (mPainterState) {
                                    is AsyncImagePainter.State.Loading -> painterState.value =
                                        PainterState.LOADING

                                    is AsyncImagePainter.State.Error -> painterState.value =
                                        PainterState.ERROR

                                    is AsyncImagePainter.State.Success -> painterState.value =
                                        PainterState.SUCCESS

                                    else -> painterState.value = PainterState.ERROR
                                }
                            }
                        )
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
    selectedTrack: (Audio) -> Unit
) {
    var isAlbumArtExist by remember { mutableStateOf(PainterState.LOADING) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clickable {
                viewModel.setMediaItem(MediaItem.fromUri(audio.uri))
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
            if (isAlbumArtExist == PainterState.ERROR) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxHeight()
                        .fillMaxWidth(0.16f),
                    color = MaterialTheme.colorScheme.onSurface
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.music),
                        contentDescription = "Music",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else {
                AsyncImage(
                    model = audio.albumArt,
                    contentDescription = audio.displayName,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxWidth(0.16f)
                        .fillMaxHeight(),
                    contentScale = ContentScale.FillBounds,
                    onState = { painterState ->
                        isAlbumArtExist = when (painterState) {
                            is AsyncImagePainter.State.Loading -> PainterState.LOADING
                            is AsyncImagePainter.State.Error -> PainterState.ERROR
                            is AsyncImagePainter.State.Success -> PainterState.SUCCESS
                            else -> PainterState.ERROR
                        }
                    }
                )
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