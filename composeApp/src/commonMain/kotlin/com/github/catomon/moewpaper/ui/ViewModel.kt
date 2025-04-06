package com.github.catomon.moewpaper.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.github.catomon.moewpaper.desktopFolder
import com.github.catomon.moewpaper.utils.DesktopUtils
import com.github.catomon.moewpaper.utils.SystemIconUtils

class MoeViewModel() : ViewModel() {

    val bottomBarItems = mutableStateListOf<Item>()

    val homeItems = mutableStateListOf<Item>()

    val desktopItems = mutableStateListOf<Item>()

    init {
        val files =
            (desktopFolder.listFiles()?.toMutableList() ?: mutableListOf()).toMutableStateList()
        desktopItems.addAll(files.map { file ->
            Item(file.nameWithoutExtension,
                SystemIconUtils.getSystemIconImage(file.path)!!,
                file.toURI().toString(),
                open = {
                    DesktopUtils.openFile(file)
                })
        })

        val files2 =
            (desktopFolder.listFiles()?.toMutableList() ?: mutableListOf()).toMutableStateList()
        desktopItems.addAll(files.map { file ->
            Item(file.nameWithoutExtension,
                SystemIconUtils.getSystemIconImage(file.path)!!,
                file.toURI().toString(),
                open = {
                    DesktopUtils.openFile(file)
                })
        })
    }

    fun addItemToBottomPanel(item: Item) {
        bottomBarItems.removeIf { it.uri == item.uri }
        bottomBarItems.add(item)
    }
}