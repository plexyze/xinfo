package com.plexyze.xinfo.di

import android.content.Context
import com.plexyze.xinfo.files.FileManager
import com.plexyze.xinfo.ui.SimpleListAdapter
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Job
import javax.inject.Singleton

@Module
class UiModule {

    @Provides
    fun crateSimpleListAdapter(): SimpleListAdapter {
        return SimpleListAdapter()
    }
}