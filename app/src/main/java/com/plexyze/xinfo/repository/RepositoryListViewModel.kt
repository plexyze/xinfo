package com.plexyze.xinfo.repository

import android.text.format.DateFormat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.R
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

    val choices = MutableLiveData<String>().apply { value = "" }
    val selected = MutableLiveData<Select>().apply { value = Select.Non }


    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    init {
        App.appComponent.inject(this)
        adapter.onChoice = {
            choices.value = it
        }
        adapter.onChangeSelected = {
            selected.value =
                if(it.isNotEmpty()) Select.Selected(it.first())
                else Select.Non
        }
        load()
    }

    fun load(){
        choices.value = ""
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

    fun deleteRepository(rep:String){
        uiScope.launch {
            repositoryDao.delete( repository = rep )
            load()
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


    sealed class Select{
        object Non : Select()
        data class Selected(val name:String):Select()
    }

}