package com.plexyze.xinfo.di

import android.content.Context
import com.plexyze.xinfo.model.PasswordDao
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

}