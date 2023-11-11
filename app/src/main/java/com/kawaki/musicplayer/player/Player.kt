package com.kawaki.musicplayer.player

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.random.Random

@UnstableApi
class Player @Inject constructor(
    private val exoPlayer: ExoPlayer
): Player.Listener {

    private val _playerState = MutableStateFlow(PlayerState.IDLE)
    val playerState = _playerState.asStateFlow()

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
    fun setMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    /**
     * Function to set list of MediaItems
     * @param mediaItemList Requires a list of [MediaItem]
     */
    fun setMediaItemList(mediaItemList: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaItemList)
        exoPlayer.prepare()
    }

    fun addMediaItem(uriList: List<Uri>) {
        val mediaItemsList = mutableStateListOf<MediaItem>()
        uriList.forEach { uri ->
            mediaItemsList.add(MediaItem.fromUri(uri))
        }
        exoPlayer.addMediaItems(mediaItemsList)

        if (mediaItemsList.isNotEmpty())  {
            exoPlayer.setMediaItem(mediaItemsList.first())
            mediaItemsList.forEach { mediaItem ->
                if (exoPlayer.duration == exoPlayer.currentPosition && exoPlayer.hasNextMediaItem()) {
                    exoPlayer.setMediaItem(mediaItem)
                }
            }
        }
    }

    /** Function to play/pause audio */
    fun playOrPause() = if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()

    /** Function to play next track */
    fun next() { if (exoPlayer.hasNextMediaItem()) exoPlayer.seekToNext() }

    /** Function to play previous track */
    fun previous() { if (exoPlayer.hasPreviousMediaItem()) exoPlayer.seekToPrevious() }

    /** Function to change audio position */
    fun seekTo(newPosition: Long) = exoPlayer.seekTo(exoPlayer.duration * newPosition)

    /**
     * Function to shuffle audio
     * @param isShuffleOn Shuffles the audio list if set to true
     */
    fun shuffle(isShuffleOn: Boolean) {
        if (isShuffleOn) exoPlayer.setShuffleOrder(ShuffleOrder.DefaultShuffleOrder(Random.nextInt()))
    }

    /** Function to get current live position
     * @return Returns the current position of the audio
     * */
    fun currentPosition(position: (Long) -> Unit) = position(exoPlayer.currentPosition)

}