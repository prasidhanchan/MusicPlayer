package com.kawaki.musicplayer.model

import android.net.Uri

data class Audio(
    val displayName: String,
    val uri: Uri,
    val id: Long,
    val title: String,
    val duration: Int,
    val artist: String,
    val albumArt: Uri,
)
