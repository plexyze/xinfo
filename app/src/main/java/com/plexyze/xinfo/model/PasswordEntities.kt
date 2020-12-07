package com.plexyze.xinfo.model

import kotlinx.serialization.Serializable

@Serializable
enum class FieldType(){
    NON, LOGIN, PASSWORD, EMAIL, PAYMENT_CARD
}

@Serializable
enum class NodeType(){
    DIRECTORY, CARD,
}

@Serializable
data class FieldEntity(
    var type: FieldType = FieldType.NON,
    var value: String = "",
)



@Serializable
data class Node(
    var type: NodeType = NodeType.DIRECTORY,
    var parentId: String = "",
    var id: String = "",
    var date: Long = 0,
    var icon: String = """🗁""",
    var name: String = "",
    var fields: List<FieldEntity> = listOf()
)


@Serializable
data class PasswordsEntity(
    var password: String = "",
    var nodes: MutableList<Node> = mutableListOf()
)