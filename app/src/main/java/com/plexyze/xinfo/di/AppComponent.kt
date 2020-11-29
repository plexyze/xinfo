package com.plexyze.xinfo.di

import com.plexyze.xinfo.activity.MainActivity
import com.plexyze.xinfo.cardinfo.CardInfoViewModel
import com.plexyze.xinfo.cardlist.CardListViewModel
import com.plexyze.xinfo.model.PasswordDao
import com.plexyze.xinfo.model.RepositoryDao
import com.plexyze.xinfo.repository.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class,UiModule::class))
interface AppComponent {

    //ViewModules
    fun inject(viewModel: RepositoryListViewModel)
    fun inject(viewModel: NewRepositoryViewModel)
    fun inject(viewModel: OpenRepositoryViewModel)
    fun inject(viewModel: EditPasswordRepositoryViewModel)
    fun inject(viewModel: DeleteRepositoryViewModel)
    fun inject(viewModel:CardListViewModel)
    fun inject(viewModel: CardInfoViewModel)

    //Dao
    fun inject(dao: RepositoryDao)
    fun inject(dao: PasswordDao)

}