package com.kawaki.musicplayer.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.kawaki.musicplayer.model.Audio
import com.kawaki.musicplayer.player.Player
import com.kawaki.musicplayer.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@UnstableApi
class HomeScreenViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val player: Player,
    private val exoPlayer: ExoPlayer
): ViewModel() {

    private val _audioList = MutableStateFlow<List<Audio>>(listOf())
    val audioList = _audioList.asSharedFlow()

    private val _playerState = player.playerState
    val playerState = _playerState

    val mExoPlayer = exoPlayer

    init {
        getAudioList()
        exoPlayer.addListener(player)
    }

    private fun getAudioList() {
        viewModelScope.launch(Dispatchers.IO) {
            _audioList.value = audioRepository.getAudioList()
        }
    }
    fun setMediaItem(
        mediaItem: MediaItem,
        mediaItemList: List<MediaItem>,
        selectedIndex: Int = 0,
        playWhenReady: Boolean = true
    )  {
        viewModelScope.launch {
            player.setMediaItem(
                mediaItem,
                mediaItemList,
                selectedIndex,
                playWhenReady
            )
        }
    }
    fun setMediaItemList(mediaItemList: List<MediaItem>) = player.setMediaItemList(mediaItemList)

    fun playOrPause() = player.playOrPause()
    fun seekToNext() = player.next()
    fun seekToPrevious() = player.previous()
    fun seekTo(newPosition: Long) = player.seekTo(newPosition)
    fun currentTime(currentPosition: (Long) -> Unit) {
        viewModelScope.launch {
            player.currentPosition { position -> currentPosition(position) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
        exoPlayer.removeListener(player)
    }
}