package com.github.catomon.moewpaper.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.compose.dnd.DragAndDropState
import com.mohamedrejeb.compose.dnd.drag.DraggableItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemsGridList(
    items: MutableList<Item>,
    modifier: Modifier = Modifier,
    dragAndDropState: DragAndDropState<Item>,
    onRemove: ((Item) -> Unit)? = null,
    showNames: Boolean = false
) {
    LazyVerticalGrid(
        columns = GridCells.FixedSize(100.dp),
        modifier,
        horizontalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(items) { item ->
            Box(Modifier.size(100.dp), contentAlignment = Alignment.Center) {
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
                    },
                    tooltipPlacement = TooltipPlacement.CursorPoint(
                        offset = DpOffset(16.dp, 16.dp)
                    ),
                    delayMillis = 300,
                ) {
                    DraggableItem(
                        state = dragAndDropState,
                        key = item.uri,
                        data = item,
                    ) {
                        ItemButton(
                            item,
//                            Modifier.padding(vertical = 4.dp).background(
//                                color = Color(939524096), shape = RoundedCornerShape(12.dp)
//                            ).padding(0.dp),
                            onRemove = onRemove,
                        )
                    }
//                }

                    if (showNames || item.type == ItemType.FOLDER) {
                        Box(
                            modifier = Modifier.background(
                                Color.White, shape = RoundedCornerShape(8.dp)
                            ).align(Alignment.BottomCenter), contentAlignment = Alignment.Center
                        ) {
                            Text(
                                item.name,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
