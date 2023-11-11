package com.kawaki.musicplayer.ui.theme.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kawaki.musicplayer.model.Audio
import com.kawaki.musicplayer.repository.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val audioRepository: AudioRepository
): ViewModel() {

    private val _audioList = MutableStateFlow<List<Audio>>(listOf())
    val audioList = _audioList.asSharedFlow()

    init {
        getAudioList()
    }

    private fun getAudioList() {
        viewModelScope.launch(Dispatchers.IO) {
            _audioList.value = audioRepository.getAudioList()
        }
    }
}