package com.github.catomon.moewpaper.ui

import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import com.github.catomon.moewpaper.theme.Colors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

//@Composable
//fun TiledBackgroundImage(image: ImageBitmap, modifier: Modifier = Modifier) {
//    val infiniteTransition = rememberInfiniteTransition()
//    val animX by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = image.width.toFloat(),
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 20000, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        )
//    )
//    val animY by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = image.height.toFloat(),
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 20000, easing = LinearEasing),
//            repeatMode = RepeatMode.Restart
//        )
//    )
//
//    Canvas(modifier = modifier) {
//        val canvasWidth = size.width
//        val canvasHeight = size.height
//        val imageWidth = image.width.toFloat()
//        val imageHeight = image.height.toFloat()
//
//        var y = -animY
//        while (y < canvasHeight) {
//            var x = -animX
//            while (x < canvasWidth) {
//                drawImage(image, topLeft = Offset(x, y), colorFilter = tint)
//                x += imageWidth
//            }
//            y += imageHeight
//        }
//    }
//}

private val colorTints =
    listOf(ColorFilter.tint(Colors.pink), ColorFilter.tint(Colors.blue), ColorFilter.tint(Colors.violet))
private val randomTint get() = colorTints.random()

private val colors =
    listOf(Colors.pink, Colors.blue, Colors.violet)
private val randomColor get() = colors.random()

private data class Star(
    var x: Float = Random.nextFloat(),
    var y: Animatable<Float, AnimationVector1D> = Animatable(-0.1f - Random.nextFloat()),
    var rotation: Animatable<Float, AnimationVector1D> = Animatable(0f),
    var color: Animatable<Color, AnimationVector4D> = Animatable<Color, AnimationVector4D>(
        randomColor,
        Color.VectorConverter(randomColor.colorSpace)
    )
)

private suspend fun animateStar(star: Star) {
    star.y.snapTo(-0.1f - Random.nextFloat())
    star.y.animateTo(
        1.1f,
        initialVelocity = 0f,
        animationSpec = tween(
            (12000 + 12000f * (Random.nextFloat() * 4f)).toInt(),
            easing = LinearEasing
        )
    )
}

@Composable
fun Starfall(image: ImageBitmap, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
    ) {
        val stars = remember {
            List(24) {
                Star()
            }
        }

        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            stars.forEach { star ->
                coroutineScope.launch {
                    while (true) {
                        star.rotation.animateTo(
                            targetValue = 360f,
                            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
                        )
                        star.rotation.snapTo(0f)
                    }
                }
            }

            stars.forEach {
                coroutineScope.launch(Dispatchers.Default) {
                    while (true) {
                        delay(Random.nextLong(1000, 6000))
                        it.color.animateTo(randomColor, tween(1500))
                    }
                }

                coroutineScope.launch(Dispatchers.Default) {
                    animateStar(it)
                }
            }

            while (true) {
                stars.forEach {
                    if (it.y.value >= 1.1f) {
                        coroutineScope.launch(Dispatchers.Default) {
                            animateStar(it)
                        }
                    }
                }

                delay(1000)
            }
        }

        Canvas(Modifier.fillMaxSize()) {
            val imageWidth = image.width.toFloat()
            val imageHeight = image.height.toFloat()
            val canvasWidth = size.width
            val canvasHeight = size.height

            stars.forEach { star ->
                val xPos = star.x * (canvasWidth - imageWidth / 2)
                val yPos = star.y.value * (canvasHeight - imageHeight / 2)

                rotate(
                    degrees = star.rotation.value,
                    pivot = Offset(xPos + imageWidth / 2, yPos + imageHeight / 2)
                ) {
                    drawImage(
                        image,
                        topLeft = Offset(xPos, yPos),
                        colorFilter = ColorFilter.tint(star.color.value)
                    )
                }
            }
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