package com.plexyze.xinfo.di

import android.content.Context
import com.plexyze.xinfo.files.FileManager
import com.plexyze.xinfo.model.PasswordDao
import com.plexyze.xinfo.model.RepositoryDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun crateContext():Context{
        return App.appContext
    }

    @Provides
    @Singleton
    fun cratePasswordDao(context: Context):PasswordDao{
        return PasswordDao(context)
    }

    @Provides
    @Singleton
    fun crateRepositoryDao():RepositoryDao{
        return RepositoryDao()
    }

    @Provides
    @Singleton
    fun crateFileManager(context: Context): FileManager {
        return FileManager(context.filesDir.absolutePath+"/xinforepository/")
    }

}