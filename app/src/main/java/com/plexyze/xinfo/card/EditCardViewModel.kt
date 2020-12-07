package com.plexyze.xinfo.card

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plexyze.xinfo.R
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.*
import com.plexyze.xinfo.res.Icons
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

    val icons = Icons

    var parentId = _parentId
        private set

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    val name = MutableLiveData<String>().apply { value = ""}
    val nameError = MutableLiveData<String>().apply { value = ""}
    val icon = MutableLiveData<String>().apply { value = """🔑"""}
    val iconError = MutableLiveData<String>().apply { value = ""}
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

    fun addField(index:Int,type:FieldType){
        val mutableFields = fields.value?.toMutableList()?: mutableListOf()
        mutableFields.add(index, FieldEntity(type,""))
        fields.value = mutableFields
    }

    fun isNotError() = !isError()
    fun isError() = nameError.value?.isNotEmpty()?:false
            || iconError.value?.isNotEmpty()?:false

    private fun validate(){
        val name = name.value?:""
        val icon = icon.value?:"""🔑"""

        val nameErrors = name.validateFileName()
        nameError.value = nameErrors
            .joinToString("\n") { context.getString(it) }

        iconError.value =
            if(icon.isEmpty()) context.getString(R.string.not_filled)
            else ""

        bntEnabled.value = isNotError()
        log.value = ""
    }

    fun saveCard(){
        val fieldsSave = fields.value
            ?.filter {it.type!=FieldType.NON && it.value.isNotEmpty() }?: listOf()
        fields.value = fieldsSave
        val sCard = Node(
            type = NodeType.CARD,
            id = cardId,
            parentId = parentId,
            name = name.value?:"",
            icon = icon.value?:"""🔑""",
            fields = fieldsSave
        )
        log.value = context.getString(R.string.wait)
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
        fields.value = listOf(
            FieldEntity(FieldType.EMAIL,""),
            FieldEntity(FieldType.PASSWORD,""))
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