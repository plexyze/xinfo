package com.plexyze.xinfo.explorer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.NodeRef
import com.plexyze.xinfo.model.NodeType
import com.plexyze.xinfo.model.PasswordDao
import com.plexyze.xinfo.ui.SimpleListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExplorerViewModel : ViewModel() {
    @Inject
    lateinit var passwordDao: PasswordDao

    @Inject
    lateinit var adapter: SimpleListAdapter

    var directoryId = ""
        private set

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)
    private var refsList:List<NodeRef> = listOf()

    val choice = MutableLiveData<Choice>().apply { value = Choice.Non }
    val selected = MutableLiveData<Select>().apply { value = Select.Non }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    init {
        App.appComponent.inject(this)
        adapter.onChoice = ::changeNodeId
        adapter.onChangeSelected = {selectedSet ->
            val selectedId = selectedSet.firstOrNull()?:""
            if (selectedId.isEmpty()){
                selected.value = Select.Non
            }else refsList.find { it.id == selectedId }?.let { ref->
                selected.value = when (ref.type) {
                    NodeType.CARD -> Select.Card(selectedId)
                    NodeType.DIRECTORY -> Select.Directory(selectedId)
                }
            }
        }
    }

    private fun changeNodeId(newId:String){
        if(newId == ""){
            directoryId = newId
            load()
        }else uiScope.launch {
            val (result,card) = passwordDao.getNode(newId)
            if(result == R.string.ok){
                if(card.type == NodeType.CARD){
                    choice.value = Choice.Card(card.id)
                }else{
                    directoryId = newId
                    load()
                }
            }
        }
    }

    fun load(){
        choice.value = Choice.Non
        selected.value = Select.Non
        uiScope.launch {
            val (result,refs) = passwordDao.explore(directoryId)
            if(result == R.string.ok){
                submitList(refs)
            }
        }
    }

    private fun submitList(refs:List<NodeRef>){
        refsList = refs
        val rows = refs.map(){ SimpleListAdapter.Row(
            id = it.id,
            name = it.name,
            icon = it.icon,
            comment = it.comment)}.toList()
        adapter.clearSelected()
        adapter.submitList(rows)
    }

    sealed class Choice{
        object Non : Choice()
        data class Card(val id:String):Choice()
    }
    sealed class Select{
        object Non : Select()
        data class Card(val id:String):Select()
        data class Directory(val id:String):Select()
    }
}