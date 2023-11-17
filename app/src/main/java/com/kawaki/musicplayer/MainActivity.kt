package com.kawaki.musicplayer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.util.UnstableApi
import com.kawaki.musicplayer.navigation.MusicNavigation
import com.kawaki.musicplayer.notification.ACTIONS
import com.kawaki.musicplayer.notification.AudioService
import com.kawaki.musicplayer.ui.theme.MusicTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalMaterial3Api
@UnstableApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.White.toArgb(),
                darkScrim = Color.Black.toArgb()
                ),
                navigationBarStyle = SystemBarStyle.auto(
                    lightScrim = Color.White.toArgb(),
                    darkScrim = Color.Black.toArgb()
                )
            )
            MusicTheme {
                MusicPlayerApp()
            }
            Intent(applicationContext, AudioService::class.java).also {
                it.action = ACTIONS.START.toString()
                startService(it)
            }
        }
    }
}

@ExperimentalMaterial3Api
@UnstableApi
@Composable
fun MusicPlayerApp() {
    MusicNavigation()
}

@ExperimentalMaterial3Api
@UnstableApi
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicTheme {
        MusicPlayerApp()
    }
}