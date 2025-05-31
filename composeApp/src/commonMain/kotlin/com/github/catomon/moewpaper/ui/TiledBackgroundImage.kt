package com.github.catomon.moewpaper.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.runtime.getValue
import com.github.catomon.moewpaper.theme.Colors

private val tint = ColorFilter.tint(color = Colors.lucky_star)

@Composable
fun TiledBackgroundImage(image: ImageBitmap, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val animX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = image.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val animY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = image.height.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val imageWidth = image.width.toFloat()
        val imageHeight = image.height.toFloat()

        var y = -animY
        while (y < canvasHeight) {
            var x = -animX
            while (x < canvasWidth) {
                drawImage(image, topLeft = Offset(x, y), colorFilter = tint)
                x += imageWidth
            }
            y += imageHeight
        }
    }
}

//@Composable
//fun TiledBackgroundImage(image: ImageBitmap, modifier: Modifier = Modifier) {
//    Canvas(modifier = modifier) {
//        val imageWidth = image.width.toFloat()
//        val imageHeight = image.height.toFloat()
//        val canvasWidth = size.width
//        val canvasHeight = size.height
//
//        var y = 0f
//        while (y < canvasHeight) {
//            var x = 0f
//            while (x < canvasWidth) {
//                drawImage(image, topLeft = Offset(x, y), colorFilter = tint)
//                x += imageWidth
//            }
//            y += imageHeight
//        }
//    }
//}