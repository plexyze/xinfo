package com.plexyze.xinfo.model

/**
 * Logic for highlighting list lines
 * A Listener [onChangeSelected] that can be attached to a RowSelector to get notified whenever
 * is change selection state of rows with [ids].
 */
class RowSelector( val onChangeSelected:(ids:Set<String>)->Unit ) {
    /**
     * @property selected selected set of ids
     */
    var selected = setOf<String>()
        private set

    /**
     * @property isMultiSelect allows to multi select rows
     */
    var isMultiSelect = false
        set(v){
            field = v
            if(!v){
                selected = setOf<String>()
            }
        }


    fun isEmpty() = selected.isEmpty()

    fun contains(id:String) = selected.contains(id)

    /**
     * Changes the selection state of the line with the [id]
     */
    fun change(id:String){
        val newSelected = selected.toMutableSet()
        if(newSelected.contains(id)){
            newSelected.remove(id)
        }else{
            if(!isMultiSelect){
                newSelected.clear()
            }
            newSelected.add(id)
        }
        if(newSelected != selected){
            selected = newSelected
            onChangeSelected(selected)
        }
    }

    /**
     * Filters the set of selected identifiers by the list of valid identifiers
     */
    fun filter( ids:List<String> ){
        val newSelected = ids.filter { selected.contains(it) }.toSet()
        if(newSelected != selected){
            selected = newSelected
            onChangeSelected(selected)
        }
    }

    fun clear(){
        selected = setOf()
        onChangeSelected(selected)
    }
}