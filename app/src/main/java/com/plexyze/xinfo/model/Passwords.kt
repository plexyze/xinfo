package com.plexyze.xinfo.model

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


@Serializable
data class PassRowEntity(
    var name: String = "",
    var login: String = "",
    var mail: String = "",
    var password: String = ""
)

@Serializable
data class PasswordsEntity(
    var name: String = "Passwords",
    var passlist: List<PassRowEntity> = listOf()
)

suspend fun File.writePasswordsEntity(passwordsEntity: PasswordsEntity)=
    GlobalScope.launch(Dispatchers.IO) {
        val jsonString = Json.encodeToString(passwordsEntity)
        writeText(jsonString)

}.join()



suspend fun File.readPasswordsEntity():PasswordsEntity{
    var out = PasswordsEntity()
    GlobalScope.launch(Dispatchers.IO) {
        if (exists()) {
            val jsonT = readText() // Read file
            out = Json.decodeFromString(jsonT)
        }
    }.join()
    return out
}
