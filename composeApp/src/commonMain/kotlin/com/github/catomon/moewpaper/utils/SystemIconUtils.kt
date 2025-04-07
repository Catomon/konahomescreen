package com.github.catomon.moewpaper.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.github.catomon.moewpaper.cacheFolder
import me.marnic.jiconextract2.JIconExtract
import org.jetbrains.skia.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import javax.imageio.ImageIO
import javax.swing.ImageIcon
import javax.swing.filechooser.FileSystemView

typealias AwtImage = java.awt.Image

object SystemIconUtils {
    fun saveImageToCache(imageBitmap: ImageBitmap, fileName: String, cacheDir: File = cacheFolder) {
        val skiaImage = Image.makeFromBitmap(imageBitmap.asSkiaBitmap())
        val pngBytes = skiaImage.encodeToData()?.bytes ?: return

        val cacheFile = File(cacheDir, fileName)
//        cacheFile.mkdirs()
        cacheFile.createNewFile()
        FileOutputStream(cacheFile).use { fos ->
            fos.write(pngBytes)
        }
    }

    fun loadImageFromCache(fileName: String, cacheDir: File = cacheFolder): ImageBitmap? {
        val cacheFile = File(cacheDir, fileName)
        if (!cacheFile.exists()) return null

        val byteArray = Files.readAllBytes(cacheFile.toPath())
        return Image.makeFromEncoded(byteArray).toComposeImageBitmap()
    }

    fun extractIconFromFileAndCache(filePath: String): String? {
        val filePathHashString = filePath.hashCode().toString()
        try {
            val cached = loadImageFromCache(filePathHashString)
            if (cached != null) return filePathHashString
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val bi = JIconExtract.getIconForFile(128, 128, filePath)

            val baos = ByteArrayOutputStream()
            ImageIO.write(bi, "png", baos)

            val byteArray = baos.toByteArray()
            baos.flush()

            val composeImage = Image.makeFromEncoded(byteArray).toComposeImageBitmap()

            try {
                saveImageToCache(composeImage, filePathHashString)

                return filePathHashString
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun extractIconFromFile16(filePath: String): ImageBitmap? {
        val icon = getSystemIcon(filePath)
        val image = awtImageToComposeImage(icon?.image ?: return null)
        return image.toComposeImageBitmap()
    }

    private fun getSystemIcon(filePath: String): ImageIcon? {
        val file = File(filePath)
        val systemIcon = FileSystemView.getFileSystemView().getSystemIcon(file)
        if (systemIcon is ImageIcon) {
            return systemIcon
        }
        return null
    }

    private fun awtImageToComposeImage(image: AwtImage): Image {
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