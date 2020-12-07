package com.plexyze.xinfo.explorer

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.PasswordDao
import com.plexyze.xinfo.model.validateFileName
import com.plexyze.xinfo.res.Icons
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class RenameDirectoryViewModel(private val parentId:String, private var directoryId:String) : ViewModel() {
    @Inject
    lateinit var passwordDao: PasswordDao

    @Inject
    lateinit var context: Context

    var icons = Icons

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    var edited = MutableLiveData<Boolean>().apply { value = false}

    val directory = MutableLiveData<String>().apply { value = ""}
    val directoryError = MutableLiveData<String>().apply { value = ""}
    val icon = MutableLiveData<String>().apply { value = ""}
    val iconError = MutableLiveData<String>().apply { value = ""}
    val log = MutableLiveData<String>().apply { value = ""}
    val bntEnabled = MutableLiveData<Boolean>().apply { value = false}

    val directoryChange:(String)->Unit = {
        directory.value = it
        validate()
    }

    val iconChange:(String)->Unit = {
        icon.value = it.take(2)
        validate()
    }

    fun isNotError() = !isError()
    fun isError() = directoryError.value?.isNotEmpty()?:false
            || iconError.value?.isNotEmpty()?:false

    private fun validate(){
        val directory = directory.value?:""
        val icon = icon.value?:""

        val repositoryErrors = directory.validateFileName()
        directoryError.value = repositoryErrors
            .joinToString("\n") { context.getString(it) }

        iconError.value =
            if(icon.isEmpty()) context.getString(R.string.not_filled)
            else ""

        bntEnabled.value = isNotError()
        log.value = ""
    }

    fun saveDirectory(){
        log.value = context.getString(R.string.wait)
        uiScope.launch {
            val directory = directory.value?:""
            val icon = icon.value?:""
            if(directoryId.isEmpty()){
                 val (result, node) = passwordDao.createDirectory(
                    parentId = parentId,
                    name = directory,
                    icon = icon
                )
                edited.value = result == R.string.ok
                log.value = context.getString(result)
            }else{
                val (result, node) = passwordDao.renameDirectory(
                    id = directoryId,
                    name = directory,
                    icon = icon
                )
                edited.value = result == R.string.ok
                log.value = context.getString(result)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    init {
        App.appComponent.inject(this)
        load()
    }

    fun load(){
        edited.value = false
        icon.value = """📁"""
        if(!directoryId.isEmpty()) uiScope.launch {
            val(result,node) = passwordDao.getNode(directoryId)
            if(result == R.string.ok){
                icon.value = node.icon
                directory.value = node.name
            }
        }
    }
}