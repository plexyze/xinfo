package com.plexyze.xinfo.encryption

fun String.mxorEncrypt(password:String):ByteArray{
    return this.toByteArray().mxorEncrypt(password.toByteArray())
}

fun ByteArray.mxorDecrypt(password:String):ByteArray{
    return this.mxorDecrypt(password.toByteArray())
}

fun ByteArray.mxorEncrypt(password:ByteArray):ByteArray{
    val cipherText = mutableListOf<Byte>()
    val encryptStream = EncryptStream(password){
        cipherText.add(it)
    }
    this.forEach { encryptStream.push(it) }
    encryptStream.complete()
    return cipherText.toByteArray()
}

fun ByteArray.mxorDecrypt(password:ByteArray):ByteArray{
    val decryptedText = mutableListOf<Byte>()
    val decryptStream = DecryptStream(password, { }){
        decryptedText.add(it)
    }
    this.forEach{
        decryptStream.push(it)
    }
    decryptStream.complete()
    return decryptedText.toByteArray()
}