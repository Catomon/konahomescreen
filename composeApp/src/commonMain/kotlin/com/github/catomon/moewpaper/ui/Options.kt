package com.github.catomon.moewpaper.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.github.catomon.moewpaper.desktopFolder
import com.github.catomon.moewpaper.userDataFolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.swing.JFileChooser

@Composable
fun Options(
    viewModel: MoeViewModel,
    modifier: Modifier = Modifier,
    exitApplication: () -> Unit,
) {
    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveSettings()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.background(color = Color(1593835520))
    ) {
        Column {
            Column {
                Text("Background alpha:", color = Color.White    )
                Slider(viewModel.appSettings.backgroundAlpha, onValueChange = {
                    viewModel.appSettings = viewModel.appSettings.copy(backgroundAlpha = it)
                }, modifier = Modifier.width(250.dp))
            }

            Column {
                Text("Background:", color = Color.White)

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.width(250.dp)) {
                    Button(onClick = {
                        val fileChooser = JFileChooser(desktopFolder)
                        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
                        fileChooser.isMultiSelectionEnabled = false
                        val result = fileChooser.showOpenDialog(null)

                        if (result == JFileChooser.APPROVE_OPTION) {
                            val selectedFile: File = fileChooser.selectedFile
                            Files.copy(selectedFile.toPath(), userDataFolder.toPath().resolve("custom_background.image"), StandardCopyOption.REPLACE_EXISTING)
                        }

                        //to trigger recomposition
                        viewModel.viewModelScope.launch {
                            viewModel.appSettings = viewModel.appSettings.copy(backgroundImage = "")
                            delay(500)
                            viewModel.appSettings = viewModel.appSettings.copy(backgroundImage = "custom_background")
                        }
                    }) {
                        Text("Change")
                    }

                    Button(onClick = {
                        userDataFolder.resolve("custom_background.image").delete()

                        viewModel.appSettings = viewModel.appSettings.copy(backgroundImage = "")
                    }) {
                        Text("Remove")
                    }
                }
            }

            Button(onClick = {
                viewModel.saveSettings()
                exitApplication()
            }) {
                Text("Exit App")
            }
        }

        Text(
            "Options",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth()
                .background(Color.White),
            textAlign = TextAlign.Center,
        )
    }
}