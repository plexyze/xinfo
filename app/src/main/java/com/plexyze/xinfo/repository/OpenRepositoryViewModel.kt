package com.plexyze.xinfo.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.PasswordDao
import com.plexyze.xinfo.model.validateFileName
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

    val opened = MutableLiveData<Boolean>().apply { value = false }
    val repository = MutableLiveData<String>().apply { value = ""}
    val repositoryError = MutableLiveData<String>().apply { value = ""}
    val password = MutableLiveData<String>().apply { value = ""}
    val passwordError = MutableLiveData<String>().apply { value = ""}
    val log = MutableLiveData<String>().apply { value = ""}
    val bntOpenEditable = MutableLiveData<Boolean>().apply { value = false }

    val repositoryChange:(String)->Unit = {
        repository.value = it
        validate()
    }

    val passwordChange:(String)->Unit = {
        password.value = it
        validate()
    }

    private fun validate(){
        val repositoryText = repository.value?:""
        val passwordText = password.value?:""

        val repositoryErrors = repositoryText.validateFileName()
        repositoryError.value = repositoryErrors.joinToString("\n") { context.getString(it) }

        passwordError.value = if(passwordText.isEmpty()) context.getString(R.string.not_filled) else ""

        bntOpenEditable.value = isNotError()
    }

    fun isNotError() = !isError()
    fun isError() = repositoryError.value?.isNotEmpty()?:false
            || passwordError.value?.isNotEmpty()?:false

    fun openRepository(){
        log.value = context.getString(R.string.wait)
        uiScope.launch {
            val result = passwordDao.openRepository(
                repository = repository.value?:"",
                password = password.value?:"")
            opened.value = result == R.string.ok
            log.postValue(context.getString(result))
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    init {
        App.appComponent.inject(this)
        validate()
    }


}