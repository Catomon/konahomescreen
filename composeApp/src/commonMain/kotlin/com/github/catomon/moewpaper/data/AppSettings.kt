package com.github.catomon.moewpaper.data

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val backgroundAlpha: Float = 1f,
    val customBackground: Boolean = false,
    val backgroundEffect: Boolean = true,
    val backgroundScale: Float = 1f,
    val tour: Boolean = true
)
