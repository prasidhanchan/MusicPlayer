package com.kawaki.musicplayer

import android.app.Application
import android.content.Intent
import androidx.media3.common.util.UnstableApi
import com.kawaki.musicplayer.notification.ACTIONS
import com.kawaki.musicplayer.notification.AudioService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
@UnstableApi
class MusicPlayerApplication : Application()