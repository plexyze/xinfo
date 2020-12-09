package com.plexyze.xinfo.files

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.*

/**
 * Sandbox file information
 * @property name file name
 * @property size file size
 * @property lastModified date of last change
 */
data class FileInfo(var name:String, var size:Long, var lastModified:Long)

/**
 * File access to the directory
 * @property rootDirectory absolute path
 */
class FileManager(private val rootDirectory: String) {
    /**
     * @property mutex access synchronization
     */
    private val mutex = Mutex()

    /**
     * Check the path to the directory. If the directory does not exist create it
     * @return true if directory exists
     */
    private suspend fun check():Boolean{
        val dir = File(rootDirectory)
        if(dir.isDirectory){
            return true
        }
        return try {
            dir.mkdir()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get all files
     * @return all [FileInfo]
     */
    suspend fun files():List<FileInfo>{
        mutex.withLock {
            return if(check()){
                @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                return File(rootDirectory).listFiles()
                    .mapNotNull {  FileInfo(
                        name = it.name,
                        size = it.length(),
                        lastModified = it.lastModified()) }
                    .toList()
            }else{
                listOf()
            }
        }
    }

    /**
     * Write file
     * @return true if ok
     */
    suspend fun write(fileName:String,byteArray: ByteArray):Boolean{
        mutex.withLock {
            return if(check()){
                val file = File(rootDirectory,fileName)
                val renamedFile = File(rootDirectory,fileName+".bak_"+ Date().time)
                if(file.exists()){
                    file.renameTo(renamedFile)
                }
                file.writeBytes(byteArray)
                if(renamedFile.exists()){
                    renamedFile.delete()
                }
                return true
            }else{
                false
            }
        }
    }

    /**
     * Read file
     * @return [ByteArray] or empty [ByteArray] if file not exist
     */
    suspend fun read(fileName:String):ByteArray{
        mutex.withLock {
            return if(check()){
                val f = File(rootDirectory,fileName)
                return if(f.exists()) f.readBytes()
                else ByteArray(0)
            }else{
                ByteArray(0)
            }
        }
    }

    /**
     * Delete file
     * @return true if ok
     */
    suspend fun delete(fileName:String):Boolean{
        mutex.withLock {
            return if(check()){
                val file = File(rootDirectory,fileName)
                return if(file.exists()) file.delete()
                else false
            }else{
                false
            }
        }
    }

    /**
     * Check file
     * @return true if file is exist
     */
    suspend fun isExists(fileName:String):Boolean{
        mutex.withLock {
            return if(check()){
                val file = File(rootDirectory,fileName)
                file.isFile
            }else{
                false
            }
        }
    }

}