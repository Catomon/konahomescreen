package com.github.catomon.moewpaper

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.catomon.moewpaper.theme.AppTheme
import com.github.catomon.moewpaper.ui.BottomPanel
import com.github.catomon.moewpaper.ui.MoeViewModel
import moe_wallpaper.composeapp.generated.resources.Res
import moe_wallpaper.composeapp.generated.resources.`lucky bg`
import org.jetbrains.compose.resources.painterResource
import org.koin.java.KoinJavaComponent.get
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun App(
    state: MoeViewModel = get(MoeViewModel::class.java),
    modifier: Modifier = Modifier
) = AppTheme {

    Box(
        Modifier.background(color = Color(-16119286)).fillMaxSize() then modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier.padding(start = 200.dp, end = 200.dp, bottom = 125.dp, top = 75.dp).clip(
                RoundedCornerShape(30.dp)
            ), contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(Res.drawable.`lucky bg`), "background", modifier = Modifier.clip(
                    RoundedCornerShape(30.dp)
                ), contentScale = ContentScale.FillHeight
            )
        }

        Text(
            "4:43 April 5, Saturday",
            modifier = Modifier.padding(top = 16.dp).align(Alignment.TopCenter),
            fontSize = 32.sp,
            color = Color.White
        )

        BottomPanel(state.bottomBarItems, Modifier.align(Alignment.BottomCenter))
    }
}

val desktopFolder: File = File(System.getProperty("user.home"), "Desktop")