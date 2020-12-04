package com.plexyze.xinfo.repository

import android.text.format.DateFormat
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.RepositoryDao
import com.plexyze.xinfo.ui.SimpleListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RepositoryListViewModel: ViewModel() {

    @Inject
    lateinit var repositoryDao: RepositoryDao

    @Inject
    lateinit var adapter:SimpleListAdapter

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    var onChoices: (String)->Unit = {}
    var onChangeSelected: (Set<String>)->Unit = {}

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
            val rows = repositoryList.map(){ SimpleListAdapter.Row(
                id = it.name,
                name = it.name,
                icon = """🔐""",
                comment = "${it.size.toByteString()}  ${it.lastModified.toDate()}"
            )}.toMutableList()
            adapter.clearSelected()
            adapter.submitList(rows)
        }
    }

    private fun Long.toByteString():String{
        return if(this<100){
            "${toString()}B"
        }else if(this<100000){
            "${(this/100).toDouble()/10}KB"
        }else{
            "${(this/100000).toDouble()/10}MB"
        }
    }

    private fun Long.toDate():String{
        val df = SimpleDateFormat("dd.MM.yyyy HH:mm")
        return df.format(Date(this))
    }

}