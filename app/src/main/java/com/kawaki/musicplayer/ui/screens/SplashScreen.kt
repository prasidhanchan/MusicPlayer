package com.kawaki.musicplayer.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kawaki.musicplayer.R
import com.kawaki.musicplayer.navigation.NavScreens
import com.kawaki.musicplayer.ui.components.checkStoragePermission
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
        ) {

        val scale = remember { Animatable(0f) }

        LaunchedEffect(key1 = true) {
            scale.animateTo(targetValue = 1f, animationSpec = tween(500))
            scale.animateTo(targetValue = 0.8f, animationSpec = tween(500))
            delay(1000)
            if (checkStoragePermission(context)) {
                navController.navigate(NavScreens.HOMESCREEN.name)
            } else {
                navController.navigate(NavScreens.REQUESTPERMISSIONSCREEN.name)
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.music_app_icon),
                contentDescription = "Icon",
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale.value)
                    .clip(RoundedCornerShape(20.dp))
            )
        }
    }
}