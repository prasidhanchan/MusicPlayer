package com.kawaki.musicplayer

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
import com.kawaki.musicplayer.ui.theme.MusicTheme
import com.kawaki.musicplayer.ui.screens.home.HomeScreen
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
                MusicPlayerHome()
            }
        }
    }
}

@ExperimentalMaterial3Api
@UnstableApi
@Composable
fun MusicPlayerHome() {
    HomeScreen()
}

@ExperimentalMaterial3Api
@UnstableApi
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MusicTheme {
        MusicPlayerHome()
    }
}