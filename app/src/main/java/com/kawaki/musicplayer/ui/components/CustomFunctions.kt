package com.kawaki.musicplayer.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit

/**
 * Function to check if storage permission is granted
 * @param context Requires [Context]
 * @return Returns true if granted else false
 */
fun checkStoragePermission(context: Context): Boolean {
    return if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context,Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
    } else {
        ContextCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}

/**
 * Function to convert ExoPlayer duration to 00:00 format
 * @param duration Actual duration from the player
 * @return Returns the formatted duration as a string
 */
fun formatDuration(duration: Long): String {
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (minutes) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
    return String.format("%02d:%02d", minutes, seconds)
}