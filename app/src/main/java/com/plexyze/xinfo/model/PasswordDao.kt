package com.plexyze.xinfo.model

import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.files.FileManager
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import javax.inject.Inject

/**
 * Link to the node.
 */
data class NodeRef(var type:NodeType, var id:String,var name:String, var icon:String,var comment:String)

/**
 * Return value with result
 */
data class NodeRefResult(var result:Int,var refs:List<NodeRef> = listOf())

/**
 * Return value with result
 */
data class NodeResult(var result:Int,var node:Node = Node())

/**
 * Convert field to icon
 * @return unicode char icon
 */
fun FieldEntity.toIcon()=when(type){
    FieldType.LOGIN-> """👤"""
    FieldType.EMAIL-> """📧"""
    FieldType.PAYMENT_CARD-> """💳"""
    FieldType.PASSWORD-> """🔑"""
    else->"☛"
}
/**
 * Returns the values of the field with the icon
 * @return unicode char icon + value
 */
fun FieldEntity.toIconValue():String{
    val icon = toIcon()
    return when(type){
        FieldType.PAYMENT_CARD-> """$icon *${value.takeLast(4)}"""
        FieldType.PASSWORD-> """$icon ***"""
        else->"""$icon $value"""
    }
}

/**
 *The [PasswordDao] class provides access to the password repository
 */
class PasswordDao(){

    /**
     * @property fileManager provides access to the password repository file
     */
    @Inject
    lateinit var fileManager: FileManager

    init {
        App.appComponent.inject(this)
    }

    /**
     * @property mutex access synchronization
     */
    private val mutex = Mutex()

    /**
     * @property passwordsEntity is the loaded password structure
     */
    private var passwordsEntity = PasswordsEntity()

    /**
     * @property repositoryOpened to determine the repository loaded
     */
    private var repositoryOpened = ""

    /**
     * @return true when the repository is loaded and available for modification
     */
    private fun isOpened():Boolean{
        return !repositoryOpened.isEmpty()
    }

    /**
     * Decrypts the repository and fill [passwordsEntity]
     * @return the resource identifier of the string. R.string.ok is ok
     */
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

    /**
     * Get a list of nodes whose has parent a [id]. If a node with [id] has a parent, then it is
     * added first, then directories and cards
     * @return R.string.ok if ok, or string resource id of error
     */
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

            passwordsEntity.nodes.filter { it.parentId == id && it.type == NodeType.DIRECTORY}
                .sortedBy { it.name }
                .forEach(){node -> refs.add(NodeRef(
                        type = node.type,
                        id = node.id,
                        name = node.name,
                        icon = node.icon,
                        comment = """📁(${passwordsEntity.nodes.filter{it.parentId == node.id}.size})""")) }

            passwordsEntity.nodes.filter { it.parentId == id && it.type == NodeType.CARD}
                .sortedBy { it.name }
                .forEach(){node -> refs.add(NodeRef(
                    type = node.type,
                    id = node.id,
                    name = node.name,
                    icon = node.icon,
                    comment = if(node.fields.isEmpty()){
                        """📝${node.name}"""
                    }else{
                        node.fields.filter {it.type!=FieldType.PASSWORD  }.joinToString(" "){it.toIconValue()}
                    }
                    )) }

            return NodeRefResult(R.string.ok,refs)
        }
    }

    /**
     * Delete node by [nodeId]
     * @return R.string.ok if ok, or string resource id of error
     */
    suspend fun deleteNode(nodeId:String):Int{
        mutex.withLock {
            if(!isOpened()){
                return R.string.repository_is_not_connected
            }

            val index = passwordsEntity.nodes.indexOfFirst { it.id == nodeId }

            if(index < 0){
                return R.string.node_not_found
            }

            passwordsEntity.nodes.removeAt(index)
            passwordsEntity.nodes = passwordsEntity.nodes.gc()

            if(!fileManager.writePasswords(repositoryOpened, passwordsEntity)){
                return R.string.repository_write_error
            }
            return R.string.ok
        }
    }
    /**
     * Create directory
     * @param parentId id of the node in which you want to create a directory. If id is empty then
     * directory created in the root
     * @param name name of the new directory.
     * @param icon icon of the new directory
     * @return R.string.ok if ok, or string resource id of error
     */
    suspend fun createDirectory(parentId:String, name: String, icon: String = """📁"""):NodeResult{
        mutex.withLock {
            if(!isOpened()){
                return NodeResult(R.string.repository_is_not_connected)
            }

            if(passwordsEntity.nodes.any{it.parentId == parentId && it.name.equals(name, ignoreCase = true)}){
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
    /**
     * Rename directory
     * @param id id of the directory
     * @param name new name of the directory.
     * @param icon new icon of the directory.
     * @return R.string.ok if ok, or string resource id of error
     */
    suspend fun renameDirectory(id:String, name: String, icon:String):NodeResult{
        mutex.withLock {
            if(!isOpened()){
                return NodeResult(R.string.repository_is_not_connected)
            }

            passwordsEntity.nodes.find{it.id == id}?.let{ node ->
                if(passwordsEntity.nodes.any{it.parentId == node.parentId
                            && it.name.equals(name, ignoreCase = true)
                            && it.id != id}){
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
    /**
     * Save card
     * @return R.string.ok if ok, or string resource id of error
     */
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
            if(passwordsEntity.nodes.any{it.parentId == cardNode.parentId && it.name.equals(cardNode.name, ignoreCase = true) && it.id != cardNode.id}){
                return NodeResult(R.string._already_exists,cardNode)
            }

            val index = passwordsEntity.nodes.indexOfFirst { it.id == cardNode.id }

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

    /**
     * Get node by id
     */
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

    /**
     * Garbage collection. All nodes inaccessible from the root directory will be removed.
     */
    private fun List<Node>.gc():MutableList<Node>{
        var repeat = true
        var lastList = this
        while(repeat){
            val all = lastList.map { it.id }.toSet()
            val newList = lastList.filter { it.parentId == "" || all.contains(it.parentId) }
            repeat = newList.size != lastList.size
            lastList = newList
        }
        return lastList.toMutableList()
    }

    /**
     * Generate unique id
     */
    private fun List<Node>.newId():String{
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
