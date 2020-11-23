package com.plexyze.xinfo.model

import com.plexyze.xinfo.encryption.mxorDecrypt
import com.plexyze.xinfo.encryption.mxorEncrypt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.lang.Exception


suspend fun File.writePasswords(passwordsEntity: PasswordsEntity){
    GlobalScope.launch(Dispatchers.Default) {
        val jsonString = Json.encodeToString(passwordsEntity)
        val chapterText = jsonString.mxorEncrypt(passwordsEntity.password)
        GlobalScope.launch(Dispatchers.IO) {
            writeBytes(chapterText)
        }.join()
    }.join()
}


suspend fun File.readPasswords(password:String):PasswordsEntity{
    var out = PasswordsEntity()
    GlobalScope.launch(Dispatchers.IO) {
        if (exists()) {
            val chapterText = readBytes()
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



