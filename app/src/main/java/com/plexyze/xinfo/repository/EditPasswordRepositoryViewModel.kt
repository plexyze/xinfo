package com.plexyze.xinfo.repository

import android.content.Context
import android.util.Log
import android.view.View
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

class EditPasswordRepositoryViewModel(val repositoryDef:String) : ViewModel() {
    @Inject
    lateinit var repositoryDao: RepositoryDao

    @Inject
    lateinit var context: Context

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    var onEditedRepository:()->Unit = {}

    val repository = MutableLiveData<String>().apply { value = repositoryDef}
    val oldPassword = MutableLiveData<String>().apply { value = ""}
    val password = MutableLiveData<String>().apply { value = ""}
    val confirm = MutableLiveData<String>().apply { value = ""}
    val log = MutableLiveData<String>().apply { value = ""}
    val bntEnabled = MutableLiveData<Boolean>().apply { value = false}


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

    fun validate(){
        val repositoryText = repository.value?:""
        val oldPasswordText = password.value?:""
        val passwordText = password.value?:""
        val confirmText = confirm.value?:""

        val nextLog = StringBuffer()

        if(!repositoryText.matches(Regex("[a-z0-9._]{3,50}"))){
            nextLog.appendLine(context.getString(R.string.field_repository_must_contain))
        }

        if(passwordText.isEmpty() || confirmText.isEmpty() || oldPasswordText.isEmpty()){
            nextLog.appendLine(context.getString(R.string.all_fields_must_be_filled))
        }

        if(passwordText != confirmText){
            nextLog.appendLine(context.getString(R.string.сonfirm_password_is_different))
        }

        log.value = nextLog.toString()
        bntEnabled.value = nextLog.isEmpty()
    }

    fun editPassword(){
        uiScope.launch {
            val result = repositoryDao.editPassword(
                repository = repository.value?:"",
                password = oldPassword.value?:"",
                newPassword = password.value?:"")
            if(result == R.string.ok){
                onEditedRepository()
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