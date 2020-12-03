package com.plexyze.xinfo.model

import android.content.Context
import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.files.FileManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import javax.inject.Inject

data class NodeRef(var type:NodeType,var id:String,var name:String,var comment:String,var icon:String)

data class NodeRefResult(var result:Int,var refs:List<NodeRef> = listOf())
data class NodeResult(var result:Int,var node:Node = Node())

class PasswordDao(private val context:Context){
    @Inject
    lateinit var fileManager: FileManager

    val mutex = Mutex()

    init {
        App.appComponent.inject(this)
    }

    private var passwordsEntity = PasswordsEntity()
    private var repositoryOpened = ""

    private fun isOpened():Boolean{
        return !repositoryOpened.isEmpty()
    }

    suspend fun openRepository(repository:String, password:String):Int{
        mutex.withLock {
            if(password.isEmpty()){
                return R.string.password_not_specified
            }
            if(!fileManager.isExists(repository)){
                return R.string.repository_not_found
            }
            val passEntity = fileManager.readPasswords(repository,password)

            if(passEntity.password != password){
                return R.string.password_is_not_correct
            }
            passwordsEntity = passEntity
            repositoryOpened = repository
            return R.string.ok
        }
    }

    suspend fun explore(id:String):NodeRefResult{
        mutex.withLock {
            if(!isOpened()){
                return NodeRefResult(R.string.repository_is_not_connected)
            }
            val refs = mutableListOf<NodeRef>()
            passwordsEntity.nodes.filter { it.id == id }
                .forEach(){node -> refs.add( NodeRef(
                    type = node.type,
                    id = node.parentId,
                    name = "...",
                    icon = """🔙""",
                    comment = "")) }

            passwordsEntity.nodes.filter { it.parentId == id }
                .forEach(){node -> refs.add( NodeRef(
                    type = node.type,
                    id = node.id,
                    name = node.name,
                    icon = node.icon,
                    comment = "")) }
            return NodeRefResult(R.string.ok,refs)
        }
    }

    suspend fun createDirectory(parentId:String, name: String, icon: String = """📁"""):NodeResult{
        mutex.withLock {
            if(!isOpened()){
                return NodeResult(R.string.repository_is_not_connected)
            }

            if(passwordsEntity.nodes.any{it.parentId == parentId && it.name == name}){
                return NodeResult(R.string._already_exists)
            }

            val id = passwordsEntity.nodes.newId()

            val newNode = Node(type = NodeType.DIRECTORY,
                parentId = parentId,
                id = id,
                name = name,
                icon = icon,
            )
            passwordsEntity.nodes.add(newNode)

            if(!fileManager.writePasswords(repositoryOpened, passwordsEntity)){
                return NodeResult(R.string.repository_write_error)
            }
            return NodeResult(R.string.ok,newNode)
        }
    }

    suspend fun renameDirectory(id:String, name: String, icon:String):NodeResult{
        mutex.withLock {
            if(!isOpened()){
                return NodeResult(R.string.repository_is_not_connected)
            }

            passwordsEntity.nodes.find{it.id == id}?.let{ node ->
                if(passwordsEntity.nodes.any{it.parentId == node.parentId && it.name == name && it.id != id}){
                    return NodeResult(R.string._already_exists)
                }
                node.name = name
                node.icon = icon
                if(!fileManager.writePasswords(repositoryOpened, passwordsEntity)){
                    return NodeResult(R.string.repository_write_error)
                }
                return NodeResult(R.string.ok,node.copy())
            }
            return NodeResult(R.string.node_not_found)
        }
    }

    suspend fun saveCard(cardNode:Node):NodeResult{
        mutex.withLock {
            if(!isOpened()){
                return NodeResult(R.string.repository_is_not_connected,cardNode)
            }

            if(cardNode.id.isEmpty()){
                cardNode.id = passwordsEntity.nodes.newId()
            }
            if(cardNode.name.isEmpty()){
                return NodeResult(R.string.field_name_is_not_specified,cardNode)
            }
            if(passwordsEntity.nodes.any{it.parentId == cardNode.parentId && it.name == cardNode.name && it.id != cardNode.id}){
                return NodeResult(R.string._already_exists,cardNode)
            }

            var index = -1
            passwordsEntity.nodes.forEachIndexed{i,n->
                if(n.id == cardNode.id){
                    index = i
                }
            }

            if(index == -1){
                passwordsEntity.nodes.add(cardNode)
            }else{
                passwordsEntity.nodes[index] = cardNode
            }

            if(!fileManager.writePasswords(repositoryOpened, passwordsEntity)){
                return NodeResult(R.string.repository_write_error)
            }
            return NodeResult(R.string.ok,cardNode)
        }
    }

    suspend fun getNode(id:String):NodeResult{
        mutex.withLock {
            if(!isOpened()){
                return NodeResult(R.string.repository_is_not_connected)
            }
            passwordsEntity.nodes.find { it.id == id }?.let {
                return NodeResult(R.string.ok,it)
            }
            return NodeResult(R.string.node_not_found)
        }
    }

    fun List<Node>.newId():String{
        val time = Date().time.toString()
        var count = 1L
        var id = "$time-$count"
        while(any{it.id == id}){
            count++
            id = "$time-$count"
        }
        return id
    }

}
