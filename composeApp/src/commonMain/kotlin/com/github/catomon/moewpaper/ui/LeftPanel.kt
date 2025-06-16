package com.github.catomon.moewpaper.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.moewpaper.utils.DesktopUtils
import com.github.catomon.moewpaper.utils.toggleMinimized
import com.sun.jna.platform.DesktopWindow
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
        modifier.fillMaxHeight().width(125.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
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
            TooltipArea(
                tooltip = {
                    Box(
                        modifier = Modifier.Companion.background(
                            Color.Companion.White, shape = RoundedCornerShape(8.dp)
                        ), contentAlignment = Alignment.Companion.Center
                    ) {
                        Text(
                            window.title,
                            fontSize = 16.sp,
                            modifier = Modifier.Companion.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }, tooltipPlacement = TooltipPlacement.CursorPoint(
                    offset = DpOffset(16.dp, 16.dp)
                ), delayMillis = 300
            ) {

                val iconName = remember(window.filePath) { DesktopUtils.getIcon(window) }
                Column {
                    if (iconName != null)
                        CachedIcon(
                            iconName,
                            Modifier.padding(12.dp).size(64.dp)
                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp)).clickable {
                                    window.toggleMinimized()
                                }
                        )

//                    TextButton({
//                        DesktopUtils.closeWindowsOfProcess(DesktopUtils.getWindowProcessId(item.hwnd))
//                    }) {
//                        Text(item.title.take(10))
//                    }
                }
            }
        }
    }
}