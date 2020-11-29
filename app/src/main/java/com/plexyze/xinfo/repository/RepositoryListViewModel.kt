package com.plexyze.xinfo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.cardlist.CardListAdapter
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.PasswordDao
import com.plexyze.xinfo.model.RepositoryDao
import com.plexyze.xinfo.ui.SimpleListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class RepositoryListViewModel: ViewModel() {

    @Inject
    lateinit var repositoryDao: RepositoryDao

    @Inject
    lateinit var adapter:SimpleListAdapter

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    var onChoices:(String)->Unit = {}
    var onChangeSelected:(Set<String>)->Unit = {}

    val selected
        get()=adapter.selected

    override fun onCleared() {
        super.onCleared()
        onChoices = {}
        onChangeSelected = {}
        job.cancel()
    }

    init {
        App.appComponent.inject(this)
        adapter.onChoice = {onChoices(it)}
        adapter.onChangeSelected = {
            onChangeSelected(it)
        }
        load()
    }

    fun load(){
        uiScope.launch {
            val repositoryList = repositoryDao.getAll()
            val rows = repositoryList.map(){ SimpleListAdapter.Row(id=it, name = it)}.toMutableList()
            adapter.clearSelected()
            adapter.submitList(rows)
        }
    }

}