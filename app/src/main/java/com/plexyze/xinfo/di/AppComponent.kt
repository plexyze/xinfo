package com.plexyze.xinfo.di

import com.plexyze.xinfo.card.CardViewModel
import com.plexyze.xinfo.explorer.RenameDirectoryViewModel
import com.plexyze.xinfo.explorer.ExplorerViewModel
import com.plexyze.xinfo.card.EditCardViewModel
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
    fun inject(viewModel: ExplorerViewModel)
    fun inject(viewModel: RenameDirectoryViewModel)
    fun inject(viewModel: EditCardViewModel)
    fun inject(viewModel: CardViewModel)

    //Dao
    fun inject(dao: RepositoryDao)
    fun inject(dao: PasswordDao)

}