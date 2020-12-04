package com.plexyze.xinfo.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.PasswordDao
import com.plexyze.xinfo.model.RepositoryDao
import com.plexyze.xinfo.ui.SimpleListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TextFileViewModel () : ViewModel() {
    @Inject
    lateinit var repositoryDao: RepositoryDao


    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    var onOpened:()->Unit = {}

    val repository = MutableLiveData<String>().apply { value = ""}
    val textFile = MutableLiveData<String>().apply { value = ""}

    val repositoryChange:(String)->Unit = {
        repository.value = it
    }

    fun openTextFile(){
        uiScope.launch {
            val bytes = repositoryDao.read( repository = repository.value?:"")
            textFile.value  = bytes.decodeToString()
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    init {
        App.appComponent.inject(this)
    }


}