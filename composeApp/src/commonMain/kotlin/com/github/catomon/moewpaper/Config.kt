package com.github.catomon.moewpaper

import java.io.File

val osName = System.getProperty("os.name").lowercase()

val cacheFolder: File = File(
    System.getProperty("user.home"), "AppData/Local/KonaHomeScreen/cache/"
)

val userDataFolder: File = File(
    System.getProperty("user.home"), "AppData/Roaming/KonaHomeScreen/"
)