package com.github.catomon.moewpaper.di

import com.github.catomon.moewpaper.ui.MoeViewModel
import org.koin.dsl.module

val appModule = module {
    factory { MoeViewModel() }
}