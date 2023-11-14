package com.kawaki.musicplayer.player

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@UnstableApi
class Player @Inject constructor(
    private val exoPlayer: ExoPlayer
): Player.Listener {

    private val _playerState = MutableStateFlow(PlayerState.IDLE)
    val playerState = _playerState.asStateFlow()

    private val job: Job? = null

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when(playbackState) {
            ExoPlayer.STATE_BUFFERING -> _playerState.value = PlayerState.BUFFERING
            ExoPlayer.STATE_IDLE -> _playerState.value = PlayerState.IDLE
            ExoPlayer.STATE_READY -> _playerState.value = PlayerState.READY
            ExoPlayer.STATE_ENDED -> _playerState.value = PlayerState.ENDED
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        when(isPlaying) {
            true -> _playerState.value = PlayerState.IsPLAYING
            else -> _playerState.value = PlayerState.PAUSED
        }
    }

    /**
     * Function to set MediaItem
     * @param mediaItem Requires [MediaItem]
     */
    fun setMediaItem(
        mediaItem: MediaItem,
        playWhenReady: Boolean
    ) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        if (playWhenReady) exoPlayer.playWhenReady = true
    }

    /**
     * Function to set list of MediaItems
     * @param uriList Requires a list of [Uri]
     */
    fun setMediaItemList(mediaItemList: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaItemList)
        exoPlayer.prepare()
    }

    /** Function to play/pause audio */
    fun playOrPause() = if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()

    /** Function to play next track */
    fun next() = exoPlayer.seekToNextMediaItem()

    /** Function to play previous track */
    fun previous() = exoPlayer.seekToPreviousMediaItem()

    /** Function to change audio position */
    fun seekTo(newPosition: Long) = exoPlayer.seekTo(newPosition)

    /**
     * Function to shuffle audio
     * @param isShuffleOn Shuffles the audio list if set to true
     */
    fun shuffle(isShuffleOn: Boolean) {
        if (isShuffleOn) exoPlayer.setShuffleOrder(ShuffleOrder.DefaultShuffleOrder(exoPlayer.mediaItemCount, 0L))
    }

    /** Function to get current live position
     * @return Returns the current position of the audio
     * */
    suspend fun currentPosition(position: (Long) -> Unit)  {
        job.run {
            while (true) {
                position(exoPlayer.currentPosition)
                delay(1000)
            }
        }
    }
}