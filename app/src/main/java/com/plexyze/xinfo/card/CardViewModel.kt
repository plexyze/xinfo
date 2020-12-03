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

class CardViewModel (val cardId:String) : ViewModel() {
    @Inject
    lateinit var passwordDao: PasswordDao

    @Inject
    lateinit var context: Context

    var parentId = ""
        private set

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    val name = MutableLiveData<String>().apply { value = ""}
    val bntEnabled = MutableLiveData<Boolean>().apply { value = false}
    val fields = MutableLiveData< List<FieldEntity> >().apply { value = listOf()}

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    init {
        App.appComponent.inject(this)
        load()
    }

    fun load(){
        if(cardId.isNotEmpty()) uiScope.launch {
            val(result,node) = passwordDao.getNode(cardId)
            if(result == R.string.ok){
                parentId = node.parentId
                name.value = node.name
                fields.value = node.fields
            }
        }
    }
}