package com.plexyze.xinfo.cardlist

import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.*
import kotlinx.coroutines.*
import javax.inject.Inject


class CardListViewModel():ViewModel() {

    @Inject lateinit var passwordDao:PasswordDao

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    val adapter = CardListAdapter()

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun isLogined() = passwordDao.isLogined()

    init {
        App.appComponent.inject(this)
        load()
    }

    fun load(){
        uiScope.launch {
            adapter.submitList(passwordDao.getAll())
        }
    }

}