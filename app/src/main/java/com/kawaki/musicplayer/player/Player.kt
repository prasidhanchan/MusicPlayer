package com.kawaki.musicplayer.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@UnstableApi
class Player @Inject constructor(
    private val exoPlayer: ExoPlayer
) : Player.Listener {

    private val _playerState = MutableStateFlow(PlayerState.IDLE)
    val playerState = _playerState.asStateFlow()

    private val job: Job? = null

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState) {
            ExoPlayer.STATE_BUFFERING -> _playerState.value = PlayerState.BUFFERING
            ExoPlayer.STATE_IDLE -> _playerState.value = PlayerState.IDLE
            ExoPlayer.STATE_READY -> _playerState.value = PlayerState.READY
            ExoPlayer.STATE_ENDED -> _playerState.value = PlayerState.ENDED
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        when (isPlaying) {
            true -> _playerState.value = PlayerState.IsPLAYING
            else -> _playerState.value = PlayerState.PAUSED
        }
    }

    /**
     * Function to set MediaItem
     * @param mediaItem Requires [MediaItem]
     */
    suspend fun setMediaItem(
        mediaItem: MediaItem,
        mediaItemList: List<MediaItem>,
        selectedIndex: Int,
        playWhenReady: Boolean
    ) {
        val mediaItemsFirst = mediaItemList.subList(0, selectedIndex)
        val mediaItemsLast = mediaItemList.subList(selectedIndex + 1, mediaItemList.size)
        exoPlayer.clearMediaItems()
        exoPlayer.setMediaItem(mediaItem)
        delay(800)
        exoPlayer.addMediaItems(0, mediaItemsFirst)
        exoPlayer.addMediaItems(mediaItemsLast)
        exoPlayer.prepare()
        if (playWhenReady) exoPlayer.play()
    }

    /**
     * Function to set list of MediaItems
     * @param mediaItemList Requires a list of [MediaItem]
     */
    fun setMediaItemList(mediaItemList: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaItemList)
        exoPlayer.prepare()
    }

    /** Function to play/pause audio */
    fun playOrPause() = if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()

    /** Function to play next track */
    fun next() {
        exoPlayer.seekToNextMediaItem()
        exoPlayer.play()
    }

    /** Function to play previous track */
    fun previous() {
        exoPlayer.seekToPreviousMediaItem()
        exoPlayer.play()
    }

    /** Function to change audio position */
    fun seekTo(newPosition: Long) = exoPlayer.seekTo(newPosition)

    /** Function to get current live position
     * @return Returns the current position of the audio
     * */
    suspend fun currentPosition(position: (Long) -> Unit) {
        job.run {
            while (true) {
                position(exoPlayer.currentPosition)
                delay(1000)
            }
        }
    }
}