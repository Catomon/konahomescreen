package com.github.catomon.moewpaper.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun Options(
    modifier: Modifier = Modifier,
    exitApplication: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.background(color = Color(1593835520))
    ) {
        Button(onClick = {
            exitApplication()
        }) {
            Text("Exit App")
        }

        Text(
            "Desktop",
            fontSize = 32.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()
                .background(Color(1157627905)),
            textAlign = TextAlign.Center,
        )
    }
}