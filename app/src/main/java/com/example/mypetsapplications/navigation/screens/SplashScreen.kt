package com.example.mypetsapplications.navigation.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mypetsapplications.R
import com.example.mypetsapplications.database.CredentialsDataViewModel
import com.example.mypetsapplications.navigation.NavigationPaths
import com.example.mypetsapplications.ui.theme.BearsEar
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {


    val color: Color = MaterialTheme.colors.background
    val systemUiController = rememberSystemUiController()
    // Use SideEffect to update the system UI bar colors
    SideEffect {
        // Set the status bar color to magenta
        systemUiController.setStatusBarColor(
            color = color,
        )
    }

    val scale = remember {
        Animatable(0f)
    }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            1f,
            animationSpec = tween(800),
            block = {
                OvershootInterpolator(1.5f).getInterpolation(this.value)
            }
        )
        delay(1000L)


            navController.navigate(NavigationPaths.AUTH_SCREEN)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            ,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .scale(scale.value)
                .clip(shape = CircleShape)
        )


    }
}