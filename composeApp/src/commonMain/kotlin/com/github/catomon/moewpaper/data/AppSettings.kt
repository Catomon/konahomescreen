package com.github.catomon.moewpaper.data

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val backgroundAlpha: Float = 1f,
    val backgroundImage: String = ""
)
