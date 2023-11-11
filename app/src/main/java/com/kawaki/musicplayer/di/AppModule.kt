package com.kawaki.musicplayer.di

import android.content.Context
import com.kawaki.musicplayer.local.ContentResolver
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
}