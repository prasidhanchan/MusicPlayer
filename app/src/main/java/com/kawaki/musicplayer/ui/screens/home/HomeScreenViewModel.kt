package com.kawaki.musicplayer.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.kawaki.musicplayer.model.Audio
import com.kawaki.musicplayer.notification.AudioService
import com.kawaki.musicplayer.player.Player
import com.kawaki.musicplayer.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@UnstableApi
class HomeScreenViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val player: Player,
    private val exoPlayer: ExoPlayer
) : ViewModel() {

    private val _audioList = MutableStateFlow<List<Audio>>(listOf())
    val audioList = _audioList.asStateFlow()

    private val _playerState = player.playerState
    val playerState = _playerState

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _exoPlayer = exoPlayer
    val mExoPlayer = _exoPlayer

    private val audioService by lazy { AudioService() }

    init {
        getAudioList()
        currentTime()
        exoPlayer.addListener(player)
    }

    private fun getAudioList() {
        viewModelScope.launch(Dispatchers.IO) {
            audioRepository.getAudioList()
                .distinctUntilChanged()
                .collectLatest { mAudioList ->
                    if (mAudioList.isNotEmpty()) {
                        _audioList.update { mAudioList }
                        /** Artificial Delay to show loading screen for 1 sec */
                        delay(1000)
                        _isLoading.value = false
                    }
                }
        }
    }

    fun setMediaItem(
        mediaItem: MediaItem,
        mediaItemList: List<MediaItem>,
        selectedIndex: Int = 0,
        playWhenReady: Boolean = true
    ) {
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
    private fun currentTime() {
        viewModelScope.launch {
            player.currentPosition { position ->
                _currentPosition.update { position }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
        exoPlayer.removeListener(player)
        //TODO: Find an alternative for stopService()
        audioService.stopService()
    }
}