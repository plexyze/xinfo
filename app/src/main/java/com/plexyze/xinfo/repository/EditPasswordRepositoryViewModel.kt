package com.plexyze.xinfo.repository

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.RepositoryDao
import com.plexyze.xinfo.model.validatePassword
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditPasswordRepositoryViewModel() : ViewModel() {
    @Inject
    lateinit var repositoryDao: RepositoryDao

    @Inject
    lateinit var context: Context

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    val edited = MutableLiveData<Boolean>().apply { value = false }
    val repository = MutableLiveData<String>().apply { value = ""}
    val repositoryError = MutableLiveData<String>().apply { value = ""}
    val oldPassword = MutableLiveData<String>().apply { value = ""}
    val oldPasswordError = MutableLiveData<String>().apply { value = ""}
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

    val oldPasswordChange:(String)->Unit = {
        oldPassword.value = it
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
            || oldPasswordError.value?.isNotEmpty()?:false
            || passwordError.value?.isNotEmpty()?:false
            || confirmError.value?.isNotEmpty()?:false

    private fun validate(){
        val repositoryText = repository.value?:""
        val oldPasswordText = oldPassword.value?:""
        val passwordText = password.value?:""
        val confirmText = confirm.value?:""

        repositoryError.value =
            if(repositoryText.isEmpty()) context.getString(R.string.not_filled)
            else ""


        oldPasswordError.value =
            if(oldPasswordText.isEmpty()) context.getString(R.string.not_filled)
            else ""


        val passwordErrors = passwordText.validatePassword()
        passwordError.value = passwordErrors
            .joinToString("\n") { context.getString(it) }

        confirmError.value =
            if(passwordText!=confirmText) context.getString(R.string.сonfirm_password_is_different)
            else ""

        bntEnabled.value = isNotError()
        log.value =""
    }

    fun editPassword(){
        log.value = context.getString(R.string.wait)
        uiScope.launch {
            val result = repositoryDao.editPassword(
                repository = repository.value?:"",
                password = oldPassword.value?:"",
                newPassword = password.value?:"")
            edited.value = result == R.string.ok
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