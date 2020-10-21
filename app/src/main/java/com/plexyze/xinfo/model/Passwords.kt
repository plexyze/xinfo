package com.plexyze.xinfo.model

import android.content.Context
import io.reactivex.Single
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class PassRowEntity(
    var login: String = "",
    var mail: String = "",
    var password: String = ""
)

@Serializable
data class PasswordsEntity(
    var name: String = "Passwords",
    var passlist: List<PassRowEntity> = listOf()
)


class Passwords(val context: Context){

    fun load(): Single<PasswordsEntity> {
        return Single.create{ emitter ->
            val file = File(context.filesDir, "xinfo.data")
            if(!file.exists()){
                emitter.onSuccess(PasswordsEntity())
            }else{
                val jsonT = file.readText() // Read file
                emitter.onSuccess(Json.decodeFromString<PasswordsEntity>(jsonT))
            }
        }
    }

    fun save(passwordsEntity: PasswordsEntity):Single<Boolean>{
        return Single.create{ emitter ->
            val jsonT = Json.encodeToString(passwordsEntity)
            val f = context.openFileOutput("xinfo.data", Context.MODE_PRIVATE)
            f.write(jsonT.toByteArray())
            emitter.onSuccess(true)
        }

    }

}