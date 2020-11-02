package com.plexyze.xinfo.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.encryption.*
import com.plexyze.xinfo.model.PassRowEntity
import com.plexyze.xinfo.model.PasswordsEntity
import com.plexyze.xinfo.model.readPasswordsEntity
import com.plexyze.xinfo.model.writePasswordsEntity
import com.plexyze.xinfo.ui.PassListAdapter
import kotlinx.coroutines.*
import java.io.File

class HomeViewModel(val context:Context):ViewModel() {

    val adapter = PassListAdapter()
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    init {
        load()
    }

    fun load(){
        uiScope.launch {
            val file = File(context.filesDir.absolutePath, "xinfo.data")
            adapter.data = file.readPasswordsEntity().passlist
        }
    }

    fun save(){
        val rows = listOf(
            PassRowEntity(name= "mails", login = "denis", password = "pass1", mail="non"),
            PassRowEntity(name= "mails", login = "flusa", password = "pass234"),
            PassRowEntity(name= "telegramm", login = "2flusa", password = "2pass234"),
            PassRowEntity(name= "watsup", login = "loggin", password = "654321"),
            PassRowEntity(name= "mails", login = "denis", password = "pass1", mail="non"),
            PassRowEntity(name= "mails", login = "flusa", password = "pass234"),
            PassRowEntity(name= "telegramm", login = "2flusa", password = "2pass234"),
            PassRowEntity(name= "watsup", login = "loggin", password = "654321"),
            PassRowEntity(name= "mails", login = "ioi888", password = "654321")

        )
        val pass = PasswordsEntity(passlist = rows)
        val file = File(context.filesDir.absolutePath, "xinfo.data")

        uiScope.launch  {
            file.writePasswordsEntity(pass)
            adapter.data = file.readPasswordsEntity().passlist
        }
    }


}