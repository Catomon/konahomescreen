package com.github.catomon.moewpaper.ui.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.catomon.moewpaper.userDataFolder
import org.jetbrains.skia.Image

fun loadCustomBackground(): ImageBitmap? {
    val file = userDataFolder.resolve("custom_background.image")
    if (!file.exists()) return null
    try {
        val imageBitmap = Image.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
        return imageBitmap
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}