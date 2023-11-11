package com.kawaki.musicplayer.ui.theme.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.content.ContextCompat

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