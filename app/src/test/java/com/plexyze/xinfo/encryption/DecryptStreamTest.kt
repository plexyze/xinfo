package com.plexyze.xinfo.encryption

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


class DecryptStreamTest {


    val password = "1234".toByteArray()
    val badPassword = "12345".toByteArray()
    val text = "Test text".toByteArray()
    val cipherText = mutableListOf<Byte>()
    var isError = false

    @Before
    fun setUp() {
        isError = false
        cipherText.clear()
        val encryptStream = EncryptStream(password){
            cipherText.add(it)
        }
        text.forEach { encryptStream.push(it) }
        encryptStream.complete()
    }

    @Test
    fun incompleteDataDecrypt() {
        val decryptedText = mutableListOf<Byte>()
        val decryptStream = DecryptStream(password, { isError = true }){
            decryptedText.add(it)
        }
        for(i in 0 until cipherText.size - 5){
            decryptStream.push(cipherText[i])
        }
        decryptStream.complete()

        assertTrue("Incomplete ciphertext should throw an error",isError )

        assertEquals("Incomplete ciphertext should not be decrypted",
            decryptedText.size, 0
        )
    }

    @Test
    fun decrypt() {
        val decryptedText = mutableListOf<Byte>()
        val decryptStream = DecryptStream(password, { isError = true }){
            decryptedText.add(it)
        }
        cipherText.forEach{
            decryptStream.push(it)
        }
        decryptStream.complete()

        assertFalse( isError )
        assertEquals("Encrypted and decrypted data do not match",
            decryptedText, text.toMutableList()
        )
    }

    @Test
    fun badKey() {
        val decryptedText = mutableListOf<Byte>()
        val decryptStream = DecryptStream(badPassword, { isError = true }){
            decryptedText.add(it)
        }
        cipherText.forEach{
            decryptStream.push(it)
        }
        decryptStream.complete()

        assertFalse( isError )
        assertNotEquals("Incorrect key should not decrypt data",
            decryptedText, text.toMutableList()
        )
    }
}