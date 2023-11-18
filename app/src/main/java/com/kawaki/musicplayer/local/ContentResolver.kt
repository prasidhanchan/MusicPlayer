package com.kawaki.musicplayer.local

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.kawaki.musicplayer.model.Audio
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

/**
 * This class retrieves local songs on the device if the storage permission is granted
 * @param context Requires current [Context]
 */
class ContentResolver @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioList = MutableStateFlow(mutableListOf<Audio>())
    private var cursor: Cursor? = null

    private val projection = arrayOf(
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.ALBUM_ID,
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
    )

    private val selectionClaus = "${MediaStore.Audio.Media.IS_MUSIC} = ?"
    private val selectionArg = arrayOf("1")
    private val sortOrder = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"

    /**
     * This function is used to get list of Audios present on the device locally
     * @return Returns  a mutable list of [Audio]
     */
    fun getAudioList(): Flow<List<Audio>> = getCursorData()

    /**
     * This function gets the artist name, title, uri, displayName, etc from local storage and adds the [Audio] data to the audioList
     * @return Returns  a mutable list of [Audio]
     */
    private fun getCursorData(): Flow<List<Audio>> {
        cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selectionClaus,
            selectionArg,
            sortOrder
        )

        cursor?.use { mCursor ->
            val displayNameColumn = mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val idColumn = mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val idAlbumColumn = mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
            val titleColumn = mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
            val durationColumn = mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            val artistColumn = mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)

            mCursor.apply {

                while (mCursor.moveToNext()) {
                    val displayName = getString(displayNameColumn)
                    val id = getLong(idColumn)
                    val idAlbum = getLong(idAlbumColumn)
                    val title = getString(titleColumn)
                    val duration = getInt(durationColumn)
                    val artistName = getString(artistColumn)
                    val uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val albumArt = ContentUris.withAppendedId(
                        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        idAlbum
                    )

                    audioList.value.add(
                        Audio(
                            displayName = displayName,
                            id = id,
                            uri = uri,
                            title = title,
                            artist = artistName,
                            duration = duration,
                            albumArt = albumArt
                        )
                    )
                }
            }
        }
        return audioList
    }
}