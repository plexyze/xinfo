package com.plexyze.xinfo.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.lang.Exception


suspend fun File.writePasswords(passwordsEntity: PasswordsEntity)=
    GlobalScope.launch(Dispatchers.IO) {
        val jsonString = Json.encodeToString(passwordsEntity)
        writeText(jsonString)
}.join()

suspend fun File.readPasswords():PasswordsEntity{
    var out = PasswordsEntity()
    GlobalScope.launch(Dispatchers.IO) {
        if (exists()) {
            val jsonT = readText() // Read file
            try{
                out = Json.decodeFromString(jsonT)
            }catch (e:Exception){
            }
        }
    }.join()
    return out
}



