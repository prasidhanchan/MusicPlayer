package com.kawaki.musicplayer.notification

import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@UnstableApi
class AudioService @Inject constructor(
    private  val mediaSession: MediaSession,
    private val notificationManager: NotificationManager
): MediaSessionService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager.startNotification(
            mediaSession = mediaSession,
            mediaSessionService = this
        )
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession
    override fun onDestroy() {
        super.onDestroy()
        mediaSession.apply {
            release()
            player.stop()
        }
    }
}