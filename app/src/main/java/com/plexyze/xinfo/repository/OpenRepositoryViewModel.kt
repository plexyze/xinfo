package com.plexyze.xinfo.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.PasswordDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class OpenRepositoryViewModel() : ViewModel() {
    @Inject
    lateinit var passwordDao: PasswordDao

    @Inject
    lateinit var context: Context

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    var onOpened:()->Unit = {}

    val repository = MutableLiveData<String>().apply { value = ""}
    val password = MutableLiveData<String>().apply { value = ""}
    val log = MutableLiveData<String>().apply { value = ""}
    val editable = MutableLiveData<Boolean>().apply { value = true }

    val repositoryChange:(String)->Unit = {
        repository.value = it
    }
    val passwordChange:(String)->Unit = {
        password.value = it
    }

    fun openRepository(){
        editable.value = false
        log.value = "Wait"
        uiScope.launch {
            val result = passwordDao.openRepository(
                repository = repository.value?:"",
                password = password.value?:"")
            if(result == R.string.ok){
                onOpened()
            }
            log.value = context.getString(result)
            editable.value = true
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