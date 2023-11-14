package com.kawaki.musicplayer.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.kawaki.musicplayer.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@UnstableApi
class NotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exoPlayer: ExoPlayer
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotification()
    }

    fun startNotification(
        mediaSession: MediaSession,
        mediaSessionService: MediaSessionService
    ) {
        buildNotification(mediaSession)
        startForegroundNotificationService(mediaSessionService)
    }

    private fun startForegroundNotificationService(mediaSessionService: MediaSessionService) {
        val notification = Notification.Builder(
            context,
            context.getString(R.string.music)
        )
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        mediaSessionService.startForeground(
            100,
            notification
        )
    }

    private fun createNotification() {
        val notificationChannel = NotificationChannel(
            context.getString(R.string.music),
            context.getString(R.string.music),
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun buildNotification(mediaSession: MediaSession) {
        PlayerNotificationManager.Builder(
            context,
            100,
            context.getString(R.string.music)
        )
            .setMediaDescriptionAdapter(
                NotificationAdapter(
                    context = context,
                    pendingIntent = mediaSession.sessionActivity
                )
            )
            .setSmallIconResourceId(R.drawable.music_notification)
            .build()
            .apply {
                setMediaSessionToken(mediaSession.sessionCompatToken)
                setUseNextActionInCompactView(true)
                setUsePreviousActionInCompactView(true)
                setPriority(NotificationCompat.PRIORITY_LOW)
                setPlayer(exoPlayer)
            }
    }
}