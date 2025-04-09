package com.github.catomon.moewpaper.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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

    val userItems = mutableStateListOf<Item>()

    val desktopItems = mutableStateListOf<Item>()

    val bottomBarStore: KStore<List<Item>> =
        storeOf(file = Path(userDataFolder.path + "/bottom_bar.json"))

    val homeStore: KStore<List<Item>> = storeOf(file = Path(userDataFolder.path + "/home.json"))

    val userStore: KStore<List<Item>> = storeOf(file = Path(userDataFolder.path + "/user.json"))

    var showItemNames = mutableStateOf(false)

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

        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            val storeItems = bottomBarStore.get() ?: let {
                bottomBarStore.set(emptyList())
                bottomBarStore.get() ?: error("Could not set bottomBarItems KStore.")
            }

            val homeStoreItems = homeStore.get() ?: let {
                homeStore.set(emptyList())
                homeStore.get() ?: error("Could not set homeStore KStore.")
            }

            val userStoreItems = userStore.get() ?: let {
                userStore.set(emptyList())
                userStore.get() ?: error("Could not set userStore KStore.")
            }

            bottomBarItems.addAll(storeItems)
            homeItems.addAll(homeStoreItems)
            userItems.addAll(userStoreItems)
        }
    }

    fun addItemToBottomPanel(item: Item) {
        viewModelScope.launch {
            bottomBarItems.removeIf { it.uri == item.uri }
            bottomBarItems.add(item)

            bottomBarStore.addItem(item)
        }
    }

    private suspend fun KStore<List<Item>>.addItem(item: Item) {
        update { list ->
            if (list == null) return@update list
            val existingItem = list.firstOrNull { it.uri == item.uri }

            (if (existingItem != null)
                list.minus(existingItem)
            else
                list).plus(item)
        }
    }

    fun removeItemFromBottomPanel(item: Item) {
        viewModelScope.launch {
            bottomBarItems.removeIf { it.uri == item.uri }

            bottomBarStore.update {
                it?.filter { it.uri != item.uri }
            }
        }
    }

    fun addItemToHome(item: Item) {
        viewModelScope.launch {
            homeItems.removeIf { it.uri == item.uri }
            homeItems.add(item)

            homeStore.addItem(item)
        }
    }

    fun addItemToUser(item: Item) {
        viewModelScope.launch {
            userItems.removeIf { it.uri == item.uri }
            userItems.add(item)

            userStore.addItem(item)
        }
    }

    fun removeItemFromHome(item: Item) {
        viewModelScope.launch {
            homeItems.removeIf { it.uri == item.uri }

            homeStore.update {
                it?.filter { it.uri != item.uri }
            }
        }
    }

    fun removeItemFromUser(item: Item) {
        viewModelScope.launch {
            userItems.removeIf { it.uri == item.uri }

            userStore.update {
                it?.filter { it.uri != item.uri }
            }
        }
    }
}