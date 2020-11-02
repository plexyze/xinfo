package com.plexyze.xinfo.encryption

import org.junit.Test

import org.junit.Assert.*

class MxorKtTest {

    val password = "1234".toByteArray()
    val text = "Test text".toByteArray()

    @Test
    fun mxorDecrypt() {
        val cipherText = text.toMutableList()
        cipherText.mxorEncrypt(password)
        cipherText.mxorDecrypt(password)
        assertEquals("Decrypted data is different from encrypted",
            cipherText,text.toMutableList())
    }

    @Test
    fun mxorEncrypt() {
        val cipherText = text.toMutableList()
        cipherText.mxorEncrypt(password)
        assertNotEquals("Data is not encrypted",
            cipherText, text.toMutableList())
    }
}