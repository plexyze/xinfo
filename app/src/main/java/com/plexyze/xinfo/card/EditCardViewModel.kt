package com.plexyze.xinfo.card

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.FieldEntity
import com.plexyze.xinfo.model.Node
import com.plexyze.xinfo.model.NodeType
import com.plexyze.xinfo.model.PasswordDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class EditCardViewModel(private val _parentId:String, private var cardId:String) : ViewModel() {
    @Inject
    lateinit var passwordDao: PasswordDao

    @Inject
    lateinit var context: Context

    var parentId = _parentId
        private set

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    val name = MutableLiveData<String>().apply { value = ""}
    val icon = MutableLiveData<String>().apply { value = """🔑"""}
    val log = MutableLiveData<String>().apply { value = ""}
    val bntEnabled = MutableLiveData<Boolean>().apply { value = false}
    val saved = MutableLiveData<Boolean>().apply { value = false}
    val fields = MutableLiveData< List<FieldEntity> >().apply { value = listOf()}

    val nameChange:(String)->Unit = {
        name.value = it
        validate()
    }

    val iconChange:(String)->Unit = {
        icon.value = it.take(2)
        validate()
    }

    private fun validate(){
        val name = name.value?:""
        val icon = icon.value?:"""🔑"""

        val nextLog = StringBuffer()

        if(!name.matches(Regex("[a-zA-Z0-9_]{1,50}"))){
            nextLog.appendLine(context.getString(R.string.field_directory_must_contain))
        }

        if(icon.isEmpty() ){
            nextLog.appendLine(context.getString(R.string.all_fields_must_be_filled))
        }

        log.value = nextLog.toString()
        bntEnabled.value = nextLog.isEmpty()
    }

    fun saveCard(fields:List<FieldEntity>){
        val sCard = Node(
            type = NodeType.CARD,
            id = cardId,
            parentId = parentId,
            name = name.value?:"",
            icon = icon.value?:"""🔑""",
            fields = fields
        )
        uiScope.launch {
            val (result, node) = passwordDao.saveCard(sCard)
            if(result == R.string.ok){
                cardId = node.id
                saved.value = true
                load()
            }
            log.value = context.getString(result)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    init {
        App.appComponent.inject(this)
        load()
    }

    private fun load(){
        icon.value = """🔑"""
        if(cardId.isNotEmpty()) uiScope.launch {
            val(result,node) = passwordDao.getNode(cardId)
            if(result == R.string.ok){
                parentId = node.parentId
                icon.value = node.icon
                name.value = node.name
                fields.value = node.fields
            }
        }
    }
}