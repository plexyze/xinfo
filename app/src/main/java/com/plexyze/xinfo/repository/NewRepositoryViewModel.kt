package com.plexyze.xinfo.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.RepositoryDao
import com.plexyze.xinfo.model.validateFileName
import com.plexyze.xinfo.model.validatePassword
import kotlinx.coroutines.*
import javax.inject.Inject

class NewRepositoryViewModel() : ViewModel() {
    @Inject
    lateinit var repositoryDao: RepositoryDao

    @Inject
    lateinit var context: Context

    private var exists = listOf<String>()
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    val created = MutableLiveData<Boolean>().apply { value = false }
    val repository = MutableLiveData<String>().apply { value = ""}
    val repositoryError = MutableLiveData<String>().apply { value = ""}
    val password = MutableLiveData<String>().apply { value = ""}
    val passwordError = MutableLiveData<String>().apply { value = ""}
    val confirm = MutableLiveData<String>().apply { value = ""}
    val confirmError = MutableLiveData<String>().apply { value = ""}
    val log = MutableLiveData<String>().apply { value = ""}
    val bntEnabled = MutableLiveData<Boolean>().apply { value = false}



    val repositoryChange:(String)->Unit = {
        repository.value = it
        validate()
    }
    val passwordChange:(String)->Unit = {
        password.value = it
        validate()
    }
    val confirmChange:(String)->Unit = {
        confirm.value = it
        validate()
    }

    fun isNotError() = !isError()
    fun isError() = repositoryError.value?.isNotEmpty()?:false
            || passwordError.value?.isNotEmpty()?:false
            || confirmError.value?.isNotEmpty()?:false

    fun validate(){
        val repositoryText = repository.value?:""
        val passwordText = password.value?:""
        val confirmText = confirm.value?:""

        val repositoryErrors = repositoryText.validateFileName().toMutableList()
        if(exists.contains(repositoryText)){
            repositoryErrors.add(R.string._already_exists)
        }
        repositoryError.value = repositoryErrors
            .joinToString("\n") { context.getString(it) }

        val passwordErrors = passwordText.validatePassword()
        passwordError.value = passwordErrors
            .joinToString("\n") { context.getString(it) }

        confirmError.value =
            if(passwordText!=confirmText) context.getString(R.string.сonfirm_password_is_different)
            else ""

        bntEnabled.value = isNotError()
        log.value =""
    }

    fun createRepository(){
        log.value = context.getString(R.string.wait)
        val repositoryText = repository.value?:""
        val passwordText = password.value?:""
        uiScope.launch {
            val result = repositoryDao.create(
                repository = repositoryText,
                password = passwordText)
            if(result == R.string._already_exists){
                exists = listOf(repositoryText)
            }
            created.value = result == R.string.ok
            log.value = context.getString(result)
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