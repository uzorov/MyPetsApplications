package com.example.mypetsapplications.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val DarkColorPalette = darkColors(
    background = Color.Black,
    primary = NormallyPink,
    onPrimary = Color.White,
    primaryVariant = NormallyPink,
    secondary = NormallyPink,
    surface = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = BearsEar,
)

private val LightColorPalette = lightColors(
    background = Color.White,
    primary = NormallyPink,
    onPrimary = BearsEar,
    primaryVariant = NormallyPink,
    secondary = NormallyPink,
    surface = Color.White,
    onSecondary = BearsEar,
    onBackground = Color.Black,
    onSurface = Color.Black,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun MyPetsApplicationsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}