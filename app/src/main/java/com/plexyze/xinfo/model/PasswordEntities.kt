package com.plexyze.xinfo.model

import kotlinx.serialization.Serializable

@Serializable
data class CardEntity(
    var id: Long = 0,
    var date: Long = 0,
    var path: String = "",
    var name: String = "",
    var login: String = "",
    var password: String = "",
    var email: String = "",
    var description: String = "",
)

@Serializable
data class PasswordsEntity(
    var password: String = "",
    var passlist: MutableList<CardEntity> = mutableListOf()
)