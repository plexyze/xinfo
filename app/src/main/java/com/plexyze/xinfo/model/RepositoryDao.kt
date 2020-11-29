package com.plexyze.xinfo.model

import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.files.FileManager
import javax.inject.Inject

class RepositoryDao {
    @Inject
    lateinit var fileManager: FileManager

    init {
        App.appComponent.inject(this)
    }

    suspend fun getAll():List<String> = fileManager.files()


    suspend fun create(repository:String, password:String):Boolean{
        if(fileManager.isExists(repository)){
            return false
        }
        val passEntity = PasswordsEntity()
        passEntity.password = password
        return fileManager.writePasswords(repository,passEntity)
    }

    suspend fun editPassword(repository:String, password:String, newPassword:String):Int{

        if(!fileManager.isExists(repository)){
            return R.string.repository_not_found
        }
        val passEntity = fileManager.readPasswords(repository,password)
        if(passEntity.password != password){
            return R.string.password_is_not_correct
        }
        passEntity.password = newPassword
        fileManager.writePasswords(repository,passEntity)
        return R.string.ok
    }

    suspend fun delete(repository:String):Int{
        if(!fileManager.isExists(repository)){
            return R.string.repository_not_found
        }
        if(fileManager.delete(repository)){
            return R.string.ok
        }
        return R.string.oops
    }

}