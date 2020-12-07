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
import com.plexyze.xinfo.model.toIcon
import com.plexyze.xinfo.ui.TitleTextInput
import com.plexyze.xinfo.viewmodel.viewModelProvider
import kotlinx.android.synthetic.main.new_repository_fragment.*
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

        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun updateFields(fields:List<FieldEntity>){
        binding.fields.removeAllViews()
        fields.forEach {field->
            context?.let { context ->
                val input = TitleTextInput(context)
                input.title = field.toIcon()+ when(field.type){
                    FieldType.LOGIN -> getString(R.string.login)
                    FieldType.PASSWORD -> getString(R.string.password)
                    FieldType.EMAIL -> getString(R.string.email)
                    FieldType.PAYMENT_CARD -> getString(R.string.payment_card)
                    else->""
                }
                input.inputType = when(field.type){
                    FieldType.PASSWORD ->InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
                    else->InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
                }
                input.text = field.value
                input.isEnabled = true
                input.eventChange = {
                    field.value = it
                }
                binding.fields.addView(input)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_card_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val index = binding.fields.children.indexOfFirst { it.hasFocus() } + 1
        val type = when (item.itemId) {
            R.id.add_login ->FieldType.LOGIN
            R.id.add_password ->FieldType.PASSWORD
            R.id.add_email ->FieldType.EMAIL
            R.id.payment_card ->FieldType.PAYMENT_CARD
            else -> FieldType.NON
        }
        if(type == FieldType.NON){
            return super.onOptionsItemSelected(item)
        }
        viewModel.addField(index,type)
        return true

    }

}