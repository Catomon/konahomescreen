package com.github.catomon.moewpaper.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import me.marnic.jiconextract2.JIconExtract
import org.jetbrains.skia.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.filechooser.FileSystemView


typealias AwtImage = java.awt.Image

object SystemIconUtils {

    fun getSystemIconImage(filePath: String): ImageBitmap? {
        try {
            val bi = JIconExtract.getIconForFile(128, 128, filePath)

            val baos = ByteArrayOutputStream()
            ImageIO.write(bi, "png", baos)

            val byteArray = baos.toByteArray()
            baos.flush()

            return Image.makeFromEncoded(byteArray).toComposeImageBitmap()
        } catch (e: Exception) {
            return null
        }
    }

    fun getSystemIconImage16(filePath: String): ImageBitmap? {
        val icon = extractIcon(filePath)
        val image = imageIconToComposeImage(icon?.image ?: return null)
        return image.toComposeImageBitmap()
    }

    private fun extractIcon(filePath: String): ImageIcon? {
        val file = File(filePath)
        val systemIcon = FileSystemView.getFileSystemView().getSystemIcon(file)
        if (systemIcon is ImageIcon) {
            return systemIcon
        }
        return null
    }

    private fun imageIconToComposeImage(image: AwtImage): Image {
        val bi =
            BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        val g2d = bi.createGraphics()
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        val baos = ByteArrayOutputStream()
        ImageIO.write(bi, "png", baos)

        val byteArray = baos.toByteArray()
        baos.flush()

        return Image.makeFromEncoded(byteArray)
    }
}