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

    suspend fun getAll() = fileManager.files()

    suspend fun read(repository: String) = fileManager.read(repository)


    suspend fun create(repository:String, password:String):Int{
        if(fileManager.isExists(repository)){
            return R.string._already_exists
        }

        if(repository.validateFileName().isNotEmpty()){
            return R.string.repository_is_not_correct
        }

        if(password.validatePassword().isNotEmpty()){
            return R.string.password_is_not_correct
        }

        val passEntity = PasswordsEntity()
        passEntity.password = password
        if(fileManager.writePasswords(repository,passEntity)){
            return R.string.ok
        }
        return R.string.repository_write_error
    }

    suspend fun editPassword(repository:String, password:String, newPassword:String):Int{
        if(!fileManager.isExists(repository)){
            return R.string.repository_not_found
        }
        if(password.isEmpty()){
            return R.string.password_is_not_correct
        }
        val passEntity = fileManager.readPasswords(repository,password)
        if(passEntity.password != password){
            return R.string.password_is_not_correct
        }
        if(newPassword.validatePassword().isNotEmpty()){
            return R.string.new_password_is_not_correct
        }
        passEntity.password = newPassword
        if(fileManager.writePasswords(repository,passEntity)){
            return R.string.ok
        }
        return R.string.repository_write_error
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