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

/** Function to format Time for AudioPlayer ex: to 00:00 format
 * @return Returns a string with the formatted timeStamp
 * */
fun Long.formatMinSec(): String {
    return String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(this),
        TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
    )
}