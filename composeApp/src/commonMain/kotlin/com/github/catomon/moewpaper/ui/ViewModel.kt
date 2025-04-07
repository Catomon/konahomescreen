package com.github.catomon.moewpaper.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.catomon.moewpaper.desktopFolder
import com.github.catomon.moewpaper.userDataFolder
import com.github.catomon.moewpaper.utils.SystemIconUtils
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.coroutines.launch
import kotlinx.io.files.Path

class MoeViewModel() : ViewModel() {

    val bottomBarItems = mutableStateListOf<Item>()

    val homeItems = mutableStateListOf<Item>()

    val desktopItems = mutableStateListOf<Item>()

    val store: KStore<List<Item>> = storeOf(file = Path(userDataFolder.path + "/bottom_bar.json"))

    init {
        userDataFolder.mkdirs()

        val files =
            (desktopFolder.listFiles()?.toMutableList() ?: mutableListOf()).toMutableStateList()
        desktopItems.addAll(files.map { file ->
            Item(
                file.nameWithoutExtension,
                SystemIconUtils.extractIconFromFileAndCache(file.path)
                    ?: throw NullPointerException("extractIconFromFileAndCache fail: ${file.path}."),
                file.toURI().toString()
            )
        })

        updateBottomPanelItems()
    }

    private fun updateBottomPanelItems() {
        viewModelScope.launch {

            val storeItems = store.get() ?: let {
                store.set(emptyList())
                store.get() ?: error("Could not set bottomBarItems KStore.")
            }

            bottomBarItems.addAll(storeItems)
        }
    }

    fun addItemToBottomPanel(item: Item) {
        viewModelScope.launch {
            bottomBarItems.removeIf { it.uri == item.uri }
            bottomBarItems.add(item)

            store.update {
                it?.plus(item)
            }
        }
    }

    fun removeItemFromBottomPanel(item: Item) {
        viewModelScope.launch {
            bottomBarItems.removeIf { it.uri == item.uri }
            bottomBarItems.add(item)

            store.update {
                it?.filter { it.uri != item.uri }
            }
        }
    }
}