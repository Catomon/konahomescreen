package com.github.catomon.moewpaper.utils

import com.github.catomon.moewpaper.ui.Item
import java.awt.Desktop
import java.io.File
import java.net.URI
import kotlin.concurrent.thread

object ItemOpener {
    fun open(item: Item, itemListener: ItemListener? = null) {
        val uri = item.uri
        val uriFormat = uri.substringBefore(":")
        val realFileName = LnkParser(File(URI.create(item.uri))).realFilename
        val fileExt = realFileName?.split(".")?.last()
        when (uriFormat) {
            "file" -> {
                when (fileExt) {
                    "exe" -> {
                        thread(isDaemon = true) {
                            val processBuilder = ProcessBuilder(realFileName.substringAfter(":/"))
                            val process = processBuilder.start()
                            itemListener?.onStart?.invoke(process.pid().toString())
                            if (process.isAlive)
                                Thread.sleep(3000)
                            process.waitFor()
                            itemListener?.onClose?.invoke(process.pid().toString())
                        }
                    }

                    null -> {
                        println("ItemOpener.open error: realFilename == null. item uri: ${item.uri}")
                    }

                    else -> {
                        Desktop.getDesktop().open(File(URI.create(item.uri)))
                    }
                }
            }

            else -> {
                Exception("uri not supported").printStackTrace()
            }
        }
    }
}

class ItemListener(
    val onStart: (String) -> Unit = {},
    val onClose: (String) -> Unit = {},
)