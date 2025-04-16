package com.github.catomon.moewpaper.ui

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.github.catomon.moewpaper.cacheFolder
import com.github.catomon.moewpaper.utils.DesktopUtils
import com.github.catomon.moewpaper.utils.ItemListener
import com.github.catomon.moewpaper.utils.ItemOpener
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.awt.Desktop
import java.io.File
import java.net.URI

@Composable
fun ItemButton(
    item: Item,
    modifier: Modifier = Modifier,
    onRemove: ((Item) -> Unit)?,
    onClear: (() -> Unit)? = null
) {
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(loading) {
        if (loading) {
            delay(1000)
            loading = false
        }
    }

    var running by remember { mutableStateOf(false) }
    var currentRunningId by remember { mutableStateOf("") }

    val itemListener: ItemListener = remember {
        ItemListener(onStart = {
            currentRunningId = it
            running = true
        }, onClose = {
            running = false
            if (currentRunningId == it) currentRunningId = ""
        })
    }

    ContextMenuArea(items = {
        listOfNotNull(
            if (onRemove != null) ContextMenuItem("Remove", onClick = {
                onRemove(item)
            }) else null,
            if (currentRunningId.isNotBlank()) ContextMenuItem("Close App", onClick = {
                try {
                    DesktopUtils.killProcess(currentRunningId.toLong())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }) else null,
            if (onClear != null) ContextMenuItem("Clear All", onClick = {
                onClear()
            }) else null,
        )
    }) {
        Box(modifier.padding(12.dp).size(64.dp).let {
            if (running) it.drawWithContent {
                drawContent()

                drawCircle(color = Color.White, center = Offset.Zero, radius = 8f)
            }
            else it
        }, contentAlignment = Alignment.Center) {
            CachedIcon(item.cachedIconId,
                Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)).clickable {
                    if (!running) {
                        // if null assume its not bottom bar
                        if (onRemove == null)
                            Desktop.getDesktop().open(File(URI.create(item.uri)))
                        else
                            ItemOpener.open(item, if (running) null else itemListener)
                    } else {
//                    try {
//                        DesktopUtils.killProcess(currentRunningId.toLong())
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }


                        //does not work properly
//                    try {
//                        BringWindowToForeground.bringMainWindowToForeground(currentRunningId.toLong())
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
                    }

                    loading = true
                })

            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.width(32.dp), color = Color.White
                )
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
    cachedImageId: String, modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(
        cacheFolder.toURI().path + "/$cachedImageId", filterQuality = FilterQuality.High
    )

    Image(
        painter,
        modifier = modifier,
        contentDescription = "DeskIcon",
        contentScale = ContentScale.Crop
    )
}