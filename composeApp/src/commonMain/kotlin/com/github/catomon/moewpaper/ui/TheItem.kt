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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.github.catomon.moewpaper.cacheFolder
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.awt.Desktop
import java.io.File
import java.net.URI

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

        CachedIcon(item.cachedIconId, Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)).clickable {
            ItemOpener.open(item)

            loading = true
        })

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.width(32.dp),
                color = Color.White
            )
        }
    }
}

object ItemOpener {
    fun open(item: Item) {
        val uriFormat = item.uri.substringBefore(":")
        when (uriFormat) {
            "file" -> {
                Desktop.getDesktop().open(File(URI.create(item.uri)))
            }

            else -> {
                Exception("uri not supported").printStackTrace()
            }
        }
    }
}

@Serializable
class Item(
    val name: String,
    val cachedIconId: String,
    val uri: String,
)

@Composable
fun CachedIcon(
    cachedImageId: String,
    modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(cacheFolder.toURI().path + "/$cachedImageId", filterQuality = FilterQuality.High)

    Image(painter,
        modifier = modifier,
        contentDescription = "DeskIcon",
        contentScale = ContentScale.Crop)
}