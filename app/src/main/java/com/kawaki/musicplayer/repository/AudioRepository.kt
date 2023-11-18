package com.kawaki.musicplayer.repository

import com.kawaki.musicplayer.local.ContentResolver
import com.kawaki.musicplayer.model.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(
    private val contentResolver: ContentResolver
) {
    suspend fun getAudioList(): Flow<List<Audio>> = withContext(Dispatchers.IO) { contentResolver.getAudioList().flowOn(Dispatchers.IO) }
}