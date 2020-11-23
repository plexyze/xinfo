package com.plexyze.xinfo.encryption

import kotlin.random.Random

/**
 * Data encryption stream
 * @param [key] Encryption key
 * @param [ciphertext] lambda to get encrypted data
 */
class EncryptStream( private val key: ByteArray, private val ciphertext:(Byte)->Unit = {}) {

    private val text = mutableListOf<Byte>()

    /**
     * Add next byte
     */
    fun push(byte:Byte){
        text.add(byte)
        if(text.size < 248){
            return
        }
        complete()
    }

    /**
     * Finish adding data and encrypt
     */
    fun complete(){
        if(text.size == 0){
            return
        }
        val addSize = text.size
        text.add(addSize.toByte())
        text.swap(0,addSize)
        while( text.size < 256){
            text.add(Random.nextInt().toByte())
        }
        text.mxorEncrypt(key)
        text.forEach(ciphertext)
        text.clear()
    }
}