package com.plexyze.xinfo.cardinfo

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.CardEntity
import com.plexyze.xinfo.model.PasswordDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class CardInfoViewModel(private var id:Long): ViewModel() {

    @Inject
    lateinit var passwordDao:PasswordDao

    val state = MutableLiveData<State>().apply { value=State.Load()}

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    init {
        App.appComponent.inject(this)
        if(id == 0L){
            state.value=State.Edit()
        }else{
            load()
        }
    }

    fun btnEdit(){
        state.value=State.Load()
        uiScope.launch {
            val card = passwordDao.getCard(id)
            id = card.id
            state.value = State.Edit(
                name = card.name,
                login = card.login,
                password = card.password,
                email = card.email)
        }
    }

    fun btnSave(name:String,login:String,password:String,email:String){
        state.value = State.Load()
        val cardNew = CardEntity(
            id = id,
            name = name,
            login = login,
            password = password,
            email = email
        )

        uiScope.launch {
            id = passwordDao.changeCard(cardNew)
            load()
        }
    }

    fun load(){
        state.value=State.Load()
        uiScope.launch {
            val card = passwordDao.getCard(id)
            id = card.id
            state.value = State.Info(
                name = card.name,
                login = card.login,
                password = card.password,
                email = card.email)
        }
    }


    sealed class State{
        class Info(var name:String,var login:String, var password:String,var email:String):State()
        class Edit(var name:String="",var login:String = "", var password:String="",var email:String="",var error:String=""):State()
        class Load():State()
    }
}