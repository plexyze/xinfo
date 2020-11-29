package com.plexyze.xinfo.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.RepositoryDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteRepositoryViewModel(val repositoryDef:String) : ViewModel() {
    @Inject
    lateinit var repositoryDao: RepositoryDao

    @Inject
    lateinit var context: Context

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    var onDeletedRepository:()->Unit = {}

    val repository = MutableLiveData<String>().apply { value = repositoryDef}
    val confirm = MutableLiveData<String>().apply { value = ""}
    val log = MutableLiveData<String>().apply { value = ""}
    val bntEnabled = MutableLiveData<Boolean>().apply { value = false}


    val confirmChange:(String)->Unit = {
        confirm.value = it
        validate()
    }

    fun validate(){
        val repositoryText = repository.value?:""
        val confirmText = confirm.value?:""

        val nextLog = StringBuffer()

        if(!repositoryText.matches(Regex("[a-z0-9._]{3,50}"))){
            nextLog.appendLine(context.getString(R.string.field_repository_must_contain))
        }

        if(repositoryText != confirmText){
            nextLog.appendLine(context.getString(R.string.repository_name_to_delete))
        }

        log.value = nextLog.toString()
        bntEnabled.value = nextLog.isEmpty()
    }

    fun delete(){
        uiScope.launch {
            val result = repositoryDao.delete(
                repository = repository.value?:"")
            if(result == R.string.ok){
                onDeletedRepository()
            }
            log.value = context.getString(result)
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