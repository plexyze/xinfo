package com.plexyze.xinfo.model

import kotlinx.serialization.Serializable

/**
 * Field type
 */
@Serializable
enum class FieldType(){
    NON, LOGIN, PASSWORD, EMAIL, PAYMENT_CARD
}

/**
 * Node type
 */
@Serializable
enum class NodeType(){
    DIRECTORY, CARD,
}

/**
 * Field entity
 */
@Serializable
data class FieldEntity(
    var type: FieldType = FieldType.NON,
    var value: String = "",
)


/**
 * Node
 */
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

/**
 * Password repository entity
 */
@Serializable
data class PasswordsEntity(
    var password: String = "",
    var nodes: MutableList<Node> = mutableListOf()
)