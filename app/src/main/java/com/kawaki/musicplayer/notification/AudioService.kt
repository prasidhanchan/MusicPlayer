package com.kawaki.musicplayer.notification

import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

enum class ACTIONS {
    START,
    STOP
}

@AndroidEntryPoint
@UnstableApi
class AudioService : MediaSessionService() {

    @Inject
    lateinit var  mediaSession: MediaSession

    @Inject
    lateinit var notificationManager: NotificationManager

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTIONS.START.toString() -> {
                notificationManager.startNotification(
                    mediaSession = mediaSession,
                    mediaSessionService = this
                )
            }
            ACTIONS.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    fun stopService() {
        Intent(applicationContext, AudioService::class.java).also {
            it.action = ACTIONS.STOP.toString()
            stopService(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.apply {
            release()
            player.stop()
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}