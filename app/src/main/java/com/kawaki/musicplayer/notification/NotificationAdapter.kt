package com.kawaki.musicplayer.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import javax.inject.Inject

@UnstableApi
class NotificationAdapter @Inject constructor(
    private val context: Context,
    private val pendingIntent: PendingIntent?
): PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence = player.mediaMetadata.albumTitle ?: "Unknown"

    override fun createCurrentContentIntent(player: Player): PendingIntent? = pendingIntent

    override fun getCurrentContentText(player: Player): CharSequence = player.mediaMetadata.displayTitle ?: "Unknown"

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        TODO("Not yet implemented")
    }
}