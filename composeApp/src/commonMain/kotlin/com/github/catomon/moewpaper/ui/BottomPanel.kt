package com.github.catomon.moewpaper.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomPanel(viewModel: MoeViewModel, items: MutableList<Item>, modifier: Modifier = Modifier) {
    LazyRow(
        modifier.fillMaxWidth().height(125.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (items.isEmpty()) {
            item {
                Text("Drag and drop items here", fontSize = 32.sp, color = Color.White)
            }
        } else items(items) { item ->
            TooltipArea(
                tooltip = {
                    Box(
                        modifier = Modifier.background(
                            Color.White, shape = RoundedCornerShape(8.dp)
                        ), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            item.name,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }, tooltipPlacement = TooltipPlacement.CursorPoint(
                    offset = DpOffset(16.dp, 16.dp)
                ), delayMillis = 300
            ) {
                ItemButton(
                    item, onRemove = {
                        viewModel.removeItemFromBottomPanel(item)
                    },
                    onClear = {
                        viewModel.clearBottomPanel()
                    })
            }
        }
    }
}
