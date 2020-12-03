package com.plexyze.xinfo.explorer

import android.content.Context
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

class RenameDirectoryViewModel(val parentId:String, private var directoryId:String) : ViewModel() {
    @Inject
    lateinit var passwordDao: PasswordDao

    @Inject
    lateinit var context: Context

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    var onEdited:()->Unit = {}

    val directory = MutableLiveData<String>().apply { value = ""}
    val icon = MutableLiveData<String>().apply { value = ""}
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




    private fun validate(){
        val directory = directory.value?:""
        val icon = icon.value?:""

        val nextLog = StringBuffer()

        if(!directory.matches(Regex("[a-zA-Z0-9_]{1,50}"))){
            nextLog.appendLine(context.getString(R.string.field_directory_must_contain))
        }

        if(icon.isEmpty() ){
            nextLog.appendLine(context.getString(R.string.all_fields_must_be_filled))
        }

        log.value = nextLog.toString()
        bntEnabled.value = nextLog.isEmpty()
    }

    fun saveDirectory(){
        uiScope.launch {
            val directory = directory.value?:""
            val icon = icon.value?:""
            if(directoryId.isEmpty()){
                 val (result, node) = passwordDao.createDirectory(
                    parentId = parentId,
                    name = directory,
                    icon = icon
                )
                if(result == R.string.ok){
                    directoryId = node.id
                    onEdited()
                }
                log.value = context.getString(result)
            }else{
                val (result, node) = passwordDao.renameDirectory(
                    id = directoryId,
                    name = directory,
                    icon = icon
                )
                if(result == R.string.ok){
                    onEdited()
                }
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