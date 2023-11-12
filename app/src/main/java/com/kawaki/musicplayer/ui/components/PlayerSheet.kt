package com.kawaki.musicplayer.ui.components

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.kawaki.musicplayer.R
import com.kawaki.musicplayer.model.Audio
import com.kawaki.musicplayer.ui.screens.home.HomeScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun PlayerSheet(
    sheetState: SheetState,
    audio: Audio,
    shuffle: () -> Unit,
    previous: () -> Unit,
    next: () -> Unit,
    playPause: () -> Unit,
    favourite: () -> Unit,
    duration: Long,
    totalDuration: Long,
    isPlaying: Boolean,
    isFavourite: Boolean,
    isSheetOpen: MutableState<Boolean>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = MaterialTheme.shapes.small,
        dragHandle = { }
    ) {
        PlayerContent(
            audio = audio,
            shuffle = shuffle,
            previous = previous,
            next = next,
            playPause = playPause,
            favourite = favourite,
            duration = duration,
            totalDuration = totalDuration,
            isPlaying = isPlaying,
            isFavourite = isFavourite,
            isSheetOpen = isSheetOpen
        )
    }
}

@Composable
fun PlayerBottomBar(
    audio: Audio,
    playPause: () -> Unit,
    next: () -> Unit,
    isSheetOpen: MutableState<Boolean>,
    isPlaying: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(bottom = 10.dp)
                .clickable { isSheetOpen.value = true },
            color = MaterialTheme.colorScheme.background
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    model = audio.albumArt,
                    contentDescription = audio.title,
                    contentScale = ContentScale.FillBounds
                )
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 30.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.50f),
                        text = audio.title,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(0.50f),
                        text = audio.artist,
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable { playPause() },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(id = if (isPlaying) R.drawable.pause else R.drawable.play),
                            contentDescription = "Play/Pause"
                        )
                    }
                }

                IconButton(onClick = next) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.play_next),
                        contentDescription = "Next"
                    )
                }
            }
        }
    }
}

@UnstableApi
@Composable
fun PlayerContent(
    audio: Audio,
    shuffle: () -> Unit,
    previous: () -> Unit,
    next: () -> Unit,
    playPause: () -> Unit,
    favourite: () -> Unit,
    duration: Long,
    totalDuration: Long,
    isPlaying: Boolean,
    isFavourite: Boolean,
    isSheetOpen: MutableState<Boolean>
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        PlayerTopContent(
            audio = audio,
            isSheetOpen = isSheetOpen
        )

        PlayerCenterControls(
            duration = duration,
            totalDuration = totalDuration,
            onChange = { }
        )
        PlayerBottomControls(
            shuffle = shuffle,
            previous = previous,
            next = next,
            playPause = playPause,
            favourite = favourite,
            isPlaying = isPlaying,
            isFavourite = isFavourite
        )
    }
}

@Composable
fun PlayerTopContent(
    audio: Audio,
    isSheetOpen: MutableState<Boolean>
) {
    val isLoaded = remember { mutableStateOf(PainterState.LOADING) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (isLoaded.value == PainterState.ERROR) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                color = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.music),
                    contentDescription = "",
                    modifier = Modifier.scale(0.50f)
                )
            }
        } else {
            AsyncImage(
                model = audio.albumArt,
                contentDescription = "Album Art",
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                contentScale = ContentScale.FillBounds,
                onState = { painterState ->
                    when (painterState) {
                        is AsyncImagePainter.State.Loading -> isLoaded.value = PainterState.LOADING
                        is AsyncImagePainter.State.Error -> isLoaded.value = PainterState.ERROR
                        is AsyncImagePainter.State.Success -> isLoaded.value = PainterState.SUCCESS
                        else -> isLoaded.value = PainterState.ERROR
                    }
                }
            )
        }
        AppBar(isSheetOpen = isSheetOpen)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 25.dp, bottom = 45.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = audio.title,
                style = TextStyle(
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = audio.artist,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@UnstableApi
@Composable
fun PlayerCenterControls(
    duration: Long,
    totalDuration: Long,
    onChange: (Float) -> Unit
) {
    val currentPosition = remember { mutableFloatStateOf(duration.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(15.dp),
            contentAlignment = Alignment.Center
        ) {
            Slider(
                value = 100f,
                onValueChange = { },
                valueRange = 0f..100f,
                enabled = false,
                colors = SliderDefaults.colors(
                    disabledActiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledThumbColor = Color.Transparent
                )
            )
            Slider(
                value = currentPosition.floatValue,
                onValueChange = {
                    currentPosition.value = it
                    onChange(it)
                },
                valueRange = 0f..totalDuration.toFloat(),
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.onBackground,
                    inactiveTrackColor = Color.Transparent,
                    thumbColor = Color.Transparent
                ),
                thumb = { }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(duration),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = formatDuration(totalDuration),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PlayerBottomControls(
    shuffle: () -> Unit,
    previous: () -> Unit,
    next: () -> Unit,
    playPause: () -> Unit,
    favourite: () -> Unit,
    isPlaying: Boolean,
    isFavourite: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = shuffle) {
            Icon(
                painter = painterResource(id = R.drawable.shuffle),
                contentDescription = "Shuffle"
            )
        }
        IconButton(onClick = previous) {
            Icon(
                painter = painterResource(id = R.drawable.play_previous),
                contentDescription = "Previous"
            )
        }
        Surface(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .clickable { playPause() },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = if (isPlaying) R.drawable.pause else R.drawable.play),
                    contentDescription = "Play/Pause"
                )
            }
        }
        IconButton(onClick = next) {
            Icon(
                painter = painterResource(id = R.drawable.play_next),
                contentDescription = "Next"
            )
        }
        IconButton(onClick = favourite) {
            Icon(
                painter = painterResource(id = if (isFavourite) R.drawable.heart_filled else R.drawable.heart_outlined),
                contentDescription = "Favourite"
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@UnstableApi
@Preview(showBackground = true)
@Composable
fun PlayerPreview() {
    val audio = Audio(
        "Chaleya",
        Uri.parse(""),
        0L,
        "Chaleya",
        30000,
        "Arijit singh",
        Uri.parse("")
    )

    PlayerContent(
        audio = audio,
        shuffle = { /*TODO*/ },
        previous = { /*TODO*/ },
        next = { /*TODO*/ },
        playPause = { /*TODO*/ },
        favourite = { /*TODO*/ },
        duration = 90L,
        totalDuration = 100L,
        isPlaying = false,
        isFavourite = false,
        isSheetOpen = mutableStateOf(true)
    )
}