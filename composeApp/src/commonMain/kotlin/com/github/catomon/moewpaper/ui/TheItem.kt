package com.github.catomon.moewpaper.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ItemButton(item: Item, modifier: Modifier = Modifier) {
    Box(modifier.padding(12.dp).size(64.dp), contentAlignment = Alignment.Center) {

        var loading by remember { mutableStateOf(false) }

        LaunchedEffect(loading) {
            if (loading) {
                delay(1000)
                loading = false
            }
        }

        ItemIcon(item.icon, Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)).clickable {
            item.open()

            loading = true
        })

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.width(48.dp),
                color = Color.White
            )
        }
    }
}

class Item(
    val name: String,
    val icon: ImageBitmap,
    val uri: String,
    val open: () -> Boolean
)

@Composable
fun ItemIcon(
    image: ImageBitmap,
    modifier: Modifier = Modifier
) {
    Image(
        image,
        modifier = modifier,
        contentDescription = "DeskIcon",
        contentScale = ContentScale.Crop,
        filterQuality = FilterQuality.Medium
    )
}