package com.plexyze.xinfo.encryption

import android.util.Log
import kotlin.experimental.xor

/**
 * Data decryption stream
 * @param [key] Encryption key
 * @param [isError] lambda is called when data is incomplete
 * @param [outByte] to output decrypted data
 */
class DecryptStream (private val key: ByteArray, private val isError:()->Unit={}, private val outByte:(Byte)->Unit = {}){
    private val ciphertext = mutableListOf<Byte>()

    /**
     * Add next byte
     */
    fun push(byte:Byte){
        ciphertext.add(byte)
        if(ciphertext.size < 256){
            return
        }
        complete()
    }

    /**
     * Finish adding data and decrypt
     */
    fun complete(){
        if(ciphertext.size == 0){
            return
        }
        if(ciphertext.size < 256){
            isError()
            ciphertext.clear()
            return
        }
        ciphertext.mxorDecrypt(key)
        val size = ciphertext[255].toInt() and 0xFF
        for (byte in 0 until size){
            outByte(ciphertext[byte])
        }
        ciphertext.clear()
    }
}