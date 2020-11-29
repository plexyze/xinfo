package com.plexyze.xinfo.model

import com.plexyze.xinfo.encryption.mxorDecrypt
import com.plexyze.xinfo.encryption.mxorEncrypt
import com.plexyze.xinfo.files.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.lang.Exception


suspend fun FileManager.writePasswords(fileName:String,passwordsEntity: PasswordsEntity):Boolean{
    var result = false
    GlobalScope.launch(Dispatchers.Default) {
        val jsonString = Json.encodeToString(passwordsEntity)
        val chapterText = jsonString.mxorEncrypt(passwordsEntity.password)
        GlobalScope.launch(Dispatchers.IO) {
            result = write(fileName,chapterText)
        }.join()
    }.join()
    return result
}


suspend fun FileManager.readPasswords(fileName:String,password:String):PasswordsEntity{
    var out = PasswordsEntity()
    GlobalScope.launch(Dispatchers.IO) {
        if (isExists(fileName)) {
            val chapterText = read(fileName)
            GlobalScope.launch(Dispatchers.Default){
                val jsonT = chapterText.mxorDecrypt(password).decodeToString()
                try{
                    out = Json.decodeFromString(jsonT)
                }catch (e:Exception){
                }
            }.join()
        }
    }.join()
    return out
}



