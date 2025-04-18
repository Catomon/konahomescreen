package com.github.catomon.moewpaper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.moewpaper.theme.AppTheme
import com.github.catomon.moewpaper.theme.Colors
import com.github.catomon.moewpaper.ui.BottomPanel
import com.github.catomon.moewpaper.ui.Item
import com.github.catomon.moewpaper.ui.ItemsGridList
import com.github.catomon.moewpaper.ui.MoeViewModel
import com.github.catomon.moewpaper.ui.Options
import com.github.catomon.moewpaper.ui.utils.loadBackgroundImage
import com.mohamedrejeb.compose.dnd.DragAndDropContainer
import com.mohamedrejeb.compose.dnd.DragAndDropState
import com.mohamedrejeb.compose.dnd.drop.dropTarget
import com.mohamedrejeb.compose.dnd.rememberDragAndDropState
import kotlinx.coroutines.delay
import moe_wallpaper.composeapp.generated.resources.Res
import moe_wallpaper.composeapp.generated.resources.`lucky bg`
import org.jetbrains.compose.resources.painterResource
import org.koin.java.KoinJavaComponent.get
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class Pages {
    NONE, ITEMS, OPTIONS
}

data class UiState(
    val shownPage: Pages = Pages.NONE,
    val backgroundImageUpdatedCount: Int = 0,
)

@Composable
internal fun App(
    viewModel: MoeViewModel = get(MoeViewModel::class.java),
    modifier: Modifier = Modifier,
    exitApplication: () -> Unit = { }
) = AppTheme {

    val state by viewModel.uiState.collectAsState()

    val appSettings by viewModel.appSettings.collectAsState()

    var dateText by mutableStateOf("")

    var totalDragDistance by remember { mutableStateOf(0f) }
    val dragAndDropState = rememberDragAndDropState<Item>()

    val backgroundPainter = remember(state.backgroundImageUpdatedCount) {
        loadBackgroundImage()?.let { BitmapPainter(it) }
    }

    LaunchedEffect(Unit) {
        while (true) {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("h:mm a MMMM d, EEEE", Locale.ENGLISH)
            dateText = currentDateTime.format(formatter)
            delay(1000)
        }
    }

    DragAndDropContainer(
        state = dragAndDropState, Modifier.fillMaxSize()
    ) {
        Box(
            Modifier.background(color = Colors.mainBackground)
                .fillMaxSize() then modifier.pointerInput(
                Unit
            ) {
                detectVerticalDragGestures(onDragStart = { offset ->
                    totalDragDistance = 0f
                }, onVerticalDrag = { change, dragAmount ->
                    totalDragDistance += dragAmount
                    if (totalDragDistance >= 200) {
                        if (state.shownPage == Pages.OPTIONS) {
                            viewModel.showPage(Pages.NONE)
                            totalDragDistance = 0f
                        } else viewModel.showPage(Pages.ITEMS)
                    }
                    if (totalDragDistance <= -200) {
                        if (state.shownPage == Pages.ITEMS) {
                            viewModel.showPage(Pages.NONE)
                            totalDragDistance = 0f
                        } else viewModel.showPage(Pages.OPTIONS)
                    }
                }, onDragEnd = {
                    totalDragDistance = 0f
                })
            }, contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier.padding(start = 250.dp, end = 250.dp, bottom = 125.dp, top = 75.dp).clip(
                    RoundedCornerShape(30.dp)
                ), contentAlignment = Alignment.Center
            ) {
                Image(
                    key(backgroundPainter) {
                        backgroundPainter ?: painterResource(Res.drawable.`lucky bg`)
                    },
                    "background",
                    modifier = Modifier.clip(
                        RoundedCornerShape(34.dp)
                    ).alpha(appSettings.backgroundAlpha),
                    contentScale = ContentScale.Crop
                )

                AnimatedVisibility(
                    state.shownPage == Pages.ITEMS,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Tabs(viewModel, dragAndDropState)
                }

                AnimatedVisibility(
                    state.shownPage == Pages.OPTIONS,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Options(viewModel, Modifier.fillMaxSize(), exitApplication = exitApplication)
                }
            }

            Text(
                dateText,
                modifier = Modifier.padding(top = 16.dp).align(Alignment.TopCenter),
                fontSize = 32.sp,
                color = Color.White
            )

            BottomPanel(
                viewModel,
                viewModel.bottomBarItems,
                Modifier.align(Alignment.BottomCenter).dropTarget(state = dragAndDropState,
                    key = "bottom panel",
                    onDrop = { dragItemState ->
                        val item = dragItemState.data
                        viewModel.addItemToBottomPanel(item)
                    },
                    onDragEnter = {

                    })
            )

            if (viewModel.bottomBarItems.isEmpty()) {
                Text(
                    "Swipe down(v) to open desktop. Swipe up(^) to open settings. Swipe opposite to close.",
                    color = Color.White,
                    fontSize = 32.sp,
                    modifier = Modifier.width(225.dp).align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
fun Tabs(state: MoeViewModel, dragAndDropState: DragAndDropState<Item>) {
    var selectedIndex by remember { mutableStateOf(if (state.homeItems.isEmpty()) 0 else 1) }

    val list = listOf("Desktop", "Starred", "User")

    var showItemNames by mutableStateOf(false)

//    val focusRequester = remember { FocusRequester() }
//
//    LaunchedEffect(Unit) {
//        while(true) {
//            focusRequester.requestFocus()
//            delay(100)
//        }
//    }

    //.focusRequester(focusRequester).focusable().onKeyEvent { keyEvent ->
    //        println("k")
    //            when {
    //                keyEvent.key == Key.AltLeft && keyEvent.type == KeyEventType.KeyDown -> {
    //                    state.showItemNames.value = true
    //                    println("t")
    //                    true
    //                }
    //                keyEvent.key == Key.AltLeft && keyEvent.type == KeyEventType.KeyUp -> {
    //                    state.showItemNames.value = false
    //                    println("f")
    //                    true
    //                }
    //                else -> false
    //            }
    //    }
    Column(Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedIndex,
        ) {
            list.forEachIndexed { index, text ->
                val selected = selectedIndex == index
                Tab(
                    selected = selected,
                    onClick = { selectedIndex = index },
                    text = { Text(text = text, fontSize = 16.sp, color = Color.White) },
                    modifier = Modifier.dropTarget(state = dragAndDropState,
                        key = text,
                        onDrop = { dragItemState ->
                            val item = dragItemState.data
                            when (index) {
                                0 -> {
                                    //state.addItemToHome(item)
                                }

                                1 -> {
                                    state.addItemToHome(item)
                                }

                                2 -> {
                                    state.addItemToUser(item)
                                }
                            }
                        },
                        onDragEnter = {

                        })
                )
            }
        }

        Box(Modifier.fillMaxSize().background(color = Color(1593835520))) {
            androidx.compose.animation.AnimatedVisibility(
                selectedIndex == 0, enter = fadeIn(),
                exit = fadeOut()
            ) {
                ItemsGridList(
                    state.desktopItems,
                    Modifier.fillMaxSize(),
                    dragAndDropState,
                    showNames = showItemNames
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                selectedIndex == 1, enter = fadeIn(),
                exit = fadeOut()
            ) {
                ItemsGridList(
                    state.homeItems,
                    Modifier.fillMaxSize(),
                    dragAndDropState,
                    onRemove = state::removeItemFromHome,
                    showNames = showItemNames
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                selectedIndex == 2, enter = fadeIn(),
                exit = fadeOut()
            ) {
                ItemsGridList(
                    state.userItems,
                    Modifier.fillMaxSize(),
                    dragAndDropState,
                    onRemove = state::removeItemFromUser,
                    showNames = showItemNames
                )
            }
        }
    }
}