package com.kawaki.musicplayer.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.kawaki.musicplayer.local.ContentResolver
import com.kawaki.musicplayer.player.Player
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideContentResolver(@ApplicationContext context: Context) = ContentResolver(context)

    @Singleton
    @Provides
    fun provideAudioAttributes(): AudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @Singleton
    @Provides
    @UnstableApi
    fun provideExoPlayer(@ApplicationContext context: Context, audioAttributes: AudioAttributes) = ExoPlayer.Builder(context)
        .setAudioAttributes(audioAttributes, true)
        .setTrackSelector(DefaultTrackSelector(context))
        .setHandleAudioBecomingNoisy(true)
        .build()

    @Singleton
    @Provides
    @UnstableApi
    fun providePlayerListener(exoPlayer: ExoPlayer) = Player(exoPlayer)
}