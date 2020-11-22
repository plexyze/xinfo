package com.plexyze.xinfo.di

import com.plexyze.xinfo.cardinfo.CardInfoViewModel
import com.plexyze.xinfo.cardlist.CardListViewModel
import com.plexyze.xinfo.login.LoginFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    //ViewModules
    fun inject(viewModel:CardListViewModel)
    fun inject(viewModel: CardInfoViewModel)
    //Fragments
    fun inject(fragment: LoginFragment)
}