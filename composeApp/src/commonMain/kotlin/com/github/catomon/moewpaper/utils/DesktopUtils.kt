package com.github.catomon.moewpaper.utils

import java.awt.Desktop
import java.io.File

object DesktopUtils {
    fun openFile(file: File) : Boolean {
        val desktop = Desktop.getDesktop()
        try {
            desktop.open(file)
            return true
        } catch (e: Exception) {
            println("error opening a file: ${file.path}")
            return false
        }
    }
}