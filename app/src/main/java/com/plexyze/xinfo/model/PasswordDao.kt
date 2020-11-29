package com.plexyze.xinfo.model

import android.content.Context
import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.files.FileManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.*
import javax.inject.Inject

class PasswordDao(private val context:Context){
    @Inject
    lateinit var fileManager: FileManager

    init {
        App.appComponent.inject(this)
    }

    private var passwordsEntity = PasswordsEntity()
    private var repositoryOpened = ""

    fun isOpened():Boolean{
        return !repositoryOpened.isEmpty()
    }

    suspend fun openRepository(repository:String, password:String):Int{
        if(password.isEmpty()){
            return R.string.password_not_specified
        }
        if(!fileManager.isExists(repository)){
            return R.string.repository_not_found
        }
        val passEntity = fileManager.readPasswords(repository,password)

        if(passEntity.password != password){
            return R.string.password_is_not_correct
        }
        passwordsEntity = passEntity
        repositoryOpened = repository
        return R.string.ok

    }

    suspend fun getCard(id:Long):CardEntity{
        if(!isOpened()){
            return CardEntity()
        }
        val list = passwordsEntity.passlist
        return list.filter {it.id == id}.getOrElse(0){CardEntity()}

    }

    suspend fun changeCard(cardEntity: CardEntity):Long{
        val entity = cardEntity.copy()
        entity.date = Date().time
        val list = passwordsEntity.passlist
        if(entity.id == 0L){
            entity.id = list.newId()
            list.add(entity)
        }else{
            for(index in list.indices){
                if(list[index].id == entity.id){
                    list[index] = entity
                    break;
                }
            }
        }
        if(isOpened()){
            fileManager.writePasswords(repositoryOpened,passwordsEntity)
        }
        return entity.id
    }

    suspend fun getAll():List<CardEntity>{
        if (!isOpened()) {
            return listOf()
        }
        return passwordsEntity.passlist.toList()
    }

    private fun List<CardEntity>.newId():Long{
        if(isEmpty()){
            return 1
        }
        return sortedBy { it.id }.last().id + 1
    }
}
