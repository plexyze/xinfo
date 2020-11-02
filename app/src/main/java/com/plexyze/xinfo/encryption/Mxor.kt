package com.plexyze.xinfo.encryption

import kotlin.experimental.inv
import kotlin.experimental.xor

/**
 * Prepares known data for the boolean operation a xor
 * @param [known] known byte
 * @param [key] next key byte
 * @param [n] iteration number
 * @return the result will participate in the logical operation xor
 */
typealias FunctionCrypt = (known:Byte,key:Byte,n:Int)->Byte

/**
 * The [FunctionCrypt] implementation
 */
val MxorfunctionCrypt:FunctionCrypt = {
    known:Byte,key:Byte,n:Int->( known + key + n).toByte().inv()
}

/**
 * swaps elements in an array under indices [index1] and [index2]
 */
fun MutableList<Byte>.swap(index1:Int, index2:Int){
    val old = this[index1]
    this[index1] = this[index2]
    this[index2] = old
}

/**
 * Encrypts each byte of the array with the key [key].
 */
fun MutableList<Byte>.mxorEncrypt(key:Byte,functionCrypt:FunctionCrypt = MxorfunctionCrypt){
    for(index in this.indices){
        this.swap(0,index)
        var state = key
        for(byte in 1 until this.size){
            state = state xor functionCrypt(this[byte],key,index + byte)
        }
        this[0] = this[0] xor functionCrypt(state,key, index)
    }
}

/**
 * Decrypt each byte of the array with the key [key].
 */
fun MutableList<Byte>.mxorDecrypt(key:Byte,functionCrypt:FunctionCrypt = MxorfunctionCrypt){
    for(index in this.indices.reversed()){
        var state = key
        for(byte in 1 until this.size){
            state = state xor functionCrypt(this[byte],key,index + byte)
        }
        this[0] = this[0] xor functionCrypt( state, key,index)
        this.swap(0,index)
    }
}

/**
 * Encrypts each byte of the array with the key [keys].
 */
fun MutableList<Byte>.mxorEncrypt(keys:ByteArray,functionCrypt:FunctionCrypt = MxorfunctionCrypt){
    for(key in keys){
        mxorEncrypt(key,functionCrypt)
    }
}

/**
 * Decrypt each byte of the array with the key [keys].
 */
fun MutableList<Byte>.mxorDecrypt(keys:ByteArray,functionCrypt:FunctionCrypt = MxorfunctionCrypt){
    for(key in keys.reversed()){
        mxorDecrypt(key,functionCrypt)
    }
}
