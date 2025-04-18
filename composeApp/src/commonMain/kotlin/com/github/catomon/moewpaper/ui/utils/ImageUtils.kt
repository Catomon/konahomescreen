package com.github.catomon.moewpaper.ui.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.catomon.moewpaper.userDataFolder
import org.jetbrains.skia.Image
import java.io.File

fun readImageBitmapFromFile(file: File): ImageBitmap? {
    if (!file.exists()) return null
    try {
        val imageBitmap = Image.makeFromEncoded(file.readBytes()).toComposeImageBitmap()
        return imageBitmap
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}

fun loadBackgroundImage(): ImageBitmap? {
    return readImageBitmapFromFile(userDataFolder.resolve("custom_background.image"))
}