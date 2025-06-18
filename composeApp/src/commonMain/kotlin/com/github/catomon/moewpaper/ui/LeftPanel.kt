package com.github.catomon.moewpaper.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.moewpaper.utils.DesktopUtils
import com.github.catomon.moewpaper.utils.toggleMinimized
import com.sun.jna.platform.DesktopWindow
import compose.icons.FeatherIcons
import compose.icons.feathericons.X
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LeftPanel(viewModel: MoeViewModel, modifier: Modifier = Modifier.Companion) {
    var items by remember { mutableStateOf(emptyList<DesktopWindow>()) }

    LaunchedEffect(Unit) {
        while (true) {
            items = withContext(Dispatchers.Default) { DesktopUtils.getWindows() }
            delay(3000)
        }
    }

    LazyColumn(
        modifier.fillMaxHeight().width(230.dp),
        horizontalAlignment = Alignment.Companion.Start,
        verticalArrangement = Arrangement.Center
    ) {
        if (items.isEmpty()) {
            item {
                Text(
                    "Running\n" +
                            "apps", fontSize = 32.sp, color = Color.Companion.White
                )
            }
        } else items(items) { window ->
            val iconName = remember(window.filePath) { DesktopUtils.getIcon(window) }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                if (iconName != null)
                    CachedIcon(
                        iconName,
                        Modifier.padding(12.dp).size(64.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp)).clickable {
                                window.toggleMinimized()
                            }
                    )

                Row(
                    modifier = Modifier.background(
                        Color.White, shape = RoundedCornerShape(8.dp)
                    ).padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        window.title,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Icon(rememberVectorPainter(FeatherIcons.X), "Exit process", Modifier.clickable {
                        DesktopUtils.closeWindowsOfProcess(DesktopUtils.getWindowProcessId(window.hwnd))
                    }.requiredSize(16.dp))
//                        Text("X", Modifier.padding(horizontal = 4.dp).clickable {
//                            DesktopUtils.closeWindowsOfProcess(DesktopUtils.getWindowProcessId(window.hwnd))
//                        })
                }
            }
        }
    }
}