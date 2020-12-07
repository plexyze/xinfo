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
const val SALT_XINFO = "*Q3PahT3~ex#?kSyv*?N3ygj*"

suspend fun FileManager.writePasswords(fileName:String,passwordsEntity: PasswordsEntity,salt:String = SALT_XINFO):Boolean{
    var result = false
    GlobalScope.launch(Dispatchers.Default) {
        val jsonString = Json.encodeToString(passwordsEntity)
        val chapterText = jsonString.mxorEncrypt(salt+passwordsEntity.password)
        GlobalScope.launch(Dispatchers.IO) {
            result = write(fileName,chapterText)
        }.join()
    }.join()
    return result
}


suspend fun FileManager.readPasswords(fileName:String,password:String,salt:String = SALT_XINFO):PasswordsEntity{
    var out = PasswordsEntity()
    GlobalScope.launch(Dispatchers.IO) {
        if (isExists(fileName)) {
            val chapterText = read(fileName)
            GlobalScope.launch(Dispatchers.Default){
                val jsonT = chapterText.mxorDecrypt(salt+password).decodeToString()
                try{
                    out = Json.decodeFromString(jsonT)
                }catch (e:Exception){
                }
            }.join()
        }
    }.join()
    return out
}



