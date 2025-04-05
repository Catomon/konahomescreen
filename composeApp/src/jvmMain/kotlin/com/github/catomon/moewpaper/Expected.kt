package com.github.catomon.moewpaper

import java.io.File

val osName = System.getProperty("os.name").lowercase()
actual val userDataFolder: File =
    File(
        System.getProperty("user.home"),
        if (osName.contains("win")) "AppData/Roaming/Kagamin" else ".local/share/Kagamin"
    )