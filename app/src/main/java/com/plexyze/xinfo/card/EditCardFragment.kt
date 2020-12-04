package com.plexyze.xinfo.card

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.EditCardFragmentBinding
import com.plexyze.xinfo.model.FieldEntity
import com.plexyze.xinfo.model.FieldType
import com.plexyze.xinfo.ui.TitleTextInput
import com.plexyze.xinfo.viewmodel.viewModelProvider
import androidx.core.view.forEachIndexed as forEachIndexed1

class EditCardFragment: Fragment() {

    lateinit var icons:List<String>
    lateinit var binding:EditCardFragmentBinding
    lateinit var viewModel: EditCardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding=
            DataBindingUtil.inflate(inflater,R.layout.edit_card_fragment,container,false)
        val parentId = arguments?.getString("parentId") ?:""
        val cardId = arguments?.getString("cardId") ?:""

        icons = resources.getStringArray(R.array.icon).toList()

        viewModel = viewModelProvider(this){
            EditCardViewModel(_parentId = parentId,cardId = cardId)
        }
        viewModel.saved.observe(viewLifecycleOwner) {
            if(it) findNavController().popBackStack()
        }
        viewModel.fields.observe(viewLifecycleOwner){
            updateFields(it)
        }
        binding.viewModel = viewModel

        binding.thisFragment = this

        binding.lifecycleOwner = this

        binding.btnSave.setOnClickListener {
            saveCard()
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun updateFields(fields:List<FieldEntity>){
        binding.fields.removeAllViews()
        fields.forEach{
            when(it.type){
                FieldType.LOGIN -> addLogin(it.value)
                FieldType.PASSWORD -> addPassword(it.value)
                FieldType.EMAIL -> addEmail(it.value)
            }
        }
        if(fields.isEmpty()){
            context?.let { context ->
                val text = TextView(context)
                text.text = getString(R.string.to_add_fields_press_menu)
                binding.fields.addView(text)
                addPassword("")
                addEmail("")
                addLogin("")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_card_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_login ->{
                addLogin("")
                true
            }
            R.id.add_password ->{
                addPassword("")
                true
            }
            R.id.add_email ->{
                addEmail("")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun addLogin(text:String){
        context?.let { context ->
            val login = TitleTextInput(context)
            login.title = getString(R.string.login)
            login.inputType = InputType.TYPE_CLASS_TEXT
            login.text = text
            login.isEnabled = true
            addField(login)
        }
    }
    private fun addPassword(text:String){
        context?.let { context ->
            val password = TitleTextInput(context)
            password.title = getString(R.string.password)
            password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            password.text = text
            password.isEnabled = true
            addField(password)
        }
    }
    private fun addEmail(text:String){
        context?.let { context ->
            val email = TitleTextInput(context)
            email.title = getString(R.string.email)
            email.inputType =InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
            email.text = text
            email.isEnabled = true
            addField(email)
        }
    }

    private fun addField(view:View){
        var index = 0
        binding.fields.children.forEachIndexed(){ i, v->
            if (v.hasFocus()){
                index = i+1
            }
        }
        binding.fields.addView(view,index)
    }

    private fun saveCard(){
        val fields = binding.fields.children
            .filter { it is TitleTextInput }.map{it as TitleTextInput}
            .filter { it.text.isNotEmpty() }
            .map { FieldEntity(
                type = it.title.toTypeFields(),
                value = it.text) }.toList()

        viewModel.saveCard(fields)
    }

    private fun String.toTypeFields():FieldType{
        return when(this) {
            getString(R.string.login)-> FieldType.LOGIN
            getString(R.string.password)-> FieldType.PASSWORD
            getString(R.string.email)-> FieldType.EMAIL
            else ->FieldType.NON
        }
    }
}