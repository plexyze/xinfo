package com.plexyze.xinfo.card

import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.CardFragmentBinding
import com.plexyze.xinfo.model.FieldEntity
import com.plexyze.xinfo.model.FieldType
import com.plexyze.xinfo.model.toIcon
import com.plexyze.xinfo.ui.TitleTextInput
import com.plexyze.xinfo.viewmodel.viewModelProvider

class CardFragment : Fragment() {

    lateinit var binding: CardFragmentBinding
    lateinit var viewModel: CardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding=
            DataBindingUtil.inflate(inflater,R.layout.card_fragment,container,false)
        val cardId = arguments?.getString("cardId") ?:""

        viewModel = viewModelProvider(this){
            CardViewModel(cardId = cardId)
        }
        viewModel.load()

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
                input.isCopy = true
                input.eventChange = {
                    field.value = it
                }
                binding.fields.addView(input)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.card_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_card ->{
                val directions = CardFragmentDirections.actionCardFragmentToEditCardFragment(viewModel.parentId,viewModel.cardId)
                findNavController().navigate(directions)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}