package com.plexyze.xinfo.model

import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class RowSelectorTest {


    var selectedSet = setOf<String>()
    var selector = RowSelector(){
        selectedSet = it
    }

    @Before
    fun setUp(){
        selectedSet = setOf<String>()
        selector = RowSelector(){
            selectedSet = it
        }
    }

    @Test
    fun multiSelect() {
        selector.isMultiSelect = true

        selector.change("1")
        selector.change("2")
        selector.change("3")
        assertTrue(selectedSet == setOf("1","2","3"))
        assertTrue(selector.selected == setOf("1","2","3"))

        selector.change("2")
        assertTrue(selectedSet == setOf("1","3"))
        assertTrue(selector.selected == setOf("1","3"))

        selector.change("1")
        selector.change("3")
        assertTrue(selectedSet == setOf<String>())
        assertTrue(selector.selected == setOf<String>())
        assertTrue(selector.isEmpty())
    }

    @Test
    fun singleSelect() {
        selector.isMultiSelect = true

        selector.change("1")
        selector.change("2")
        selector.change("3")

        selector.isMultiSelect = false
        assertTrue(selector.isEmpty())


        selector.change("1")
        selector.change("2")
        assertTrue(selectedSet == setOf("2"))
        assertTrue(selector.selected == setOf("2"))
        assertFalse(selector.isEmpty())

        selector.change("3")
        assertTrue(selectedSet == setOf("3"))
        assertTrue(selector.selected == setOf("3"))

        selector.change("3")
        assertTrue(selectedSet == setOf<String>())
        assertTrue(selector.selected == setOf<String>())
        assertTrue(selector.isEmpty())
    }

    @Test
    fun filter() {
        selector.isMultiSelect = true

        selector.change("1")
        selector.change("2")
        selector.change("3")
        selector.change("4")
        selector.filter(listOf("1","5","4","6"))
        assertTrue(selectedSet == setOf("1","4"))
        assertTrue(selector.selected == setOf("1","4"))
        assertFalse(selector.isEmpty())


        selector.filter(listOf("7","8","9","f"))
        assertTrue(selectedSet == setOf<String>())
        assertTrue(selector.selected == setOf<String>())
        assertTrue(selector.isEmpty())

    }
}