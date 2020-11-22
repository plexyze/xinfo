package com.plexyze.xinfo.model

import android.content.Context
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.*

class PasswordDao(private val context:Context){
    val mutex = Mutex()
    private var file:File? = null //= File(context.filesDir.absolutePath, filePath)
    var passwordsEntity = PasswordsEntity()

    fun isLogined():Boolean{
        return file != null
    }


    suspend fun login(login:String, password:String,isOk:()->Unit,isError:(String)->Unit){
        mutex.withLock {
            val checkFile = File(context.filesDir.absolutePath, "$login.data")
            if(!checkFile.isFile){
                isError("No file")
                return
            }
            val passEntity = checkFile.readPasswords()
            if(passEntity.password != password){
                isError("error password")
                return
            }

            passwordsEntity = passEntity
            file = checkFile
            isOk()
        }
    }

    suspend fun registration(login:String, password:String,isOk:()->Unit,isError:(String)->Unit){
        mutex.withLock {
            val checkFile = File(context.filesDir.absolutePath, "$login.data")
            if(checkFile.isFile){
                isError("File already exists ")
                return
            }
            val passEntity = PasswordsEntity()
            passEntity.password = password
            checkFile.writePasswords(passEntity)
            isOk()
        }
    }

    suspend fun getCard(id:Long):CardEntity{
        mutex.withLock {
            if(file == null){
                return CardEntity()
            }
            val list = passwordsEntity.passlist
            return list.filter {it.id == id}.getOrElse(0){CardEntity()}
        }
    }

    suspend fun changeCard(cardEntity: CardEntity):Long{
        mutex.withLock {
            if(file == null){

            }
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
            file?.writePasswords(passwordsEntity)
            return entity.id
        }

    }

    suspend fun getAll():List<CardEntity>{
        mutex.withLock {
            if (file == null) {
                return listOf()
            }
            return passwordsEntity.passlist.toList()
        }
    }

    private fun List<CardEntity>.newId():Long{
        if(isEmpty()){
            return 1
        }
        return sortedBy { it.id }.last().id + 1
    }
}
