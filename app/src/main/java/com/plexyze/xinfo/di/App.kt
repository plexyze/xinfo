package com.plexyze.xinfo.di

import android.app.Application
import android.content.Context

class App:Application() {

    companion object{
        lateinit var appContext: Context private set
        lateinit var appComponent: AppComponent private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        appComponent = DaggerAppComponent.create()
    }


}