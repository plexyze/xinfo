package com.plexyze.xinfo.repository

import android.content.Context
import android.view.View
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.plexyze.xinfo.R
import com.plexyze.xinfo.cardlist.CardListAdapter
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.RepositoryDao
import kotlinx.coroutines.*
import javax.inject.Inject

class NewRepositoryViewModel() : ViewModel() {
    @Inject
    lateinit var repositoryDao: RepositoryDao

    @Inject
    lateinit var context: Context


    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    var onCreateRepository:()->Unit = {}

    val repository = MutableLiveData<String>().apply { value = ""}
    val password = MutableLiveData<String>().apply { value = ""}
    val confirm = MutableLiveData<String>().apply { value = ""}
    val log = MutableLiveData<String>().apply { value = ""}
    val bntEnabled = MutableLiveData<Boolean>().apply { value = false}

    val repositoryChange:(String)->Unit = {
        repository.value = it.toLowerCase()
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
        uiScope.launch {
            val repositoryText = repository.value?:""
            val passwordText = password.value?:""
            val confirmText = confirm.value?:""

            val nextLog = StringBuffer()

            if(!repositoryText.matches(Regex("[a-z0-9._]{3,50}"))){
                nextLog.appendLine(context.getString(R.string.field_repository_must_contain))
            }

            if(passwordText.isEmpty() || confirmText.isEmpty()){
                nextLog.appendLine(context.getString(R.string.all_fields_must_be_filled))
            }

            if(passwordText != confirmText){
                nextLog.appendLine(context.getString(R.string.сonfirm_password_is_different))
            }
            val r = repositoryDao.getAll()
            if(r.contains(repositoryText)){
                nextLog.appendLine("$repositoryText ${context.getString(R.string._already_exists)}")
            }
            log.value = nextLog.toString()
            bntEnabled.value = nextLog.isEmpty()
        }
    }

    fun createRepository(){
        uiScope.launch {
            val ok = repositoryDao.create(
                repository = repository.value?:"",
                password = password.value?:"")
            if(ok){
                onCreateRepository()
                log.value = "Ok"
            }else{
                log.value = "Create error"
            }
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