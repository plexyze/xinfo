package com.plexyze.xinfo.encryption

import org.junit.Test

import org.junit.Assert.*

class EncryptStreamTest {

    val password = "1234".toByteArray()
    val text = "Test text".toByteArray()

    @Test
    fun cryptoResistance() {
        val cipherText2 = mutableListOf<Byte>()
        val cipherText = mutableListOf<Byte>()

        val encryptStream = EncryptStream(password){
            cipherText.add(it)
        }
        val encryptStream2 = EncryptStream(password){
            cipherText2.add(it)
        }

        text.forEach {
            encryptStream.push(it)
            encryptStream2.push(it)
        }

        encryptStream.complete()
        encryptStream2.complete()

        assertNotEquals("Data is not encrypted",
            cipherText2, text.toMutableList())

    }

    @Test
    fun encrypt() {
        val cipherText = mutableListOf<Byte>()
        val encryptStream = EncryptStream(password){
            cipherText.add(it)
        }
        text.forEach { encryptStream.push(it) }
        encryptStream.complete()

        assertNotEquals("Data is not encrypted",
            cipherText, text.toMutableList())
    }
}