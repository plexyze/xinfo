package com.plexyze.xinfo.files

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.util.*

class FileManager(val rootDirectory: String) {
    val mutex = Mutex()

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

    suspend fun files():List<String>{
        mutex.withLock {
            return if(check()){
                @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                return File(rootDirectory).listFiles().mapNotNull { it.name }.toList()
            }else{
                listOf()
            }
        }
    }

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