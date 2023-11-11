package com.kawaki.musicplayer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kawaki.musicplayer.model.Audio
import com.kawaki.musicplayer.ui.screens.home.HomeScreenViewModel

@UnstableApi
@Composable
fun AudioCards(
    audioList: List<Audio>,
    viewModel: HomeScreenViewModel
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(audioList) { audio ->
            AudioCardItem(
                audio = audio,
                viewModel = viewModel
            )
        }
    }
}

@UnstableApi
@Composable
fun AudioCardItem(
    audio: Audio,
    viewModel: HomeScreenViewModel
) {
    var isAlbumArtExist by remember { mutableStateOf(PainterState.LOADING) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .padding(vertical = 5.dp)
            .clickable { viewModel.setMediaItem(MediaItem.fromUri(audio.uri)) },
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
                text = formatDuration(audio.duration.toLong()),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

enum class PainterState {
    LOADING,
    ERROR,
    SUCCESS
}