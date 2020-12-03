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
        fields.forEach{
            when(it.type){
                FieldType.LOGIN -> addLogin(it.value)
                FieldType.PASSWORD -> addPassword(it.value)
                FieldType.EMAIL -> addEmail(it.value)
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
    private fun addLogin(text:String){
        context?.let { context ->
            val login = TitleTextInput(context)
            login.title = getString(R.string.login)
            login.inputType = InputType.TYPE_CLASS_TEXT
            login.text = text
            login.isCopy = true
            binding.fields.addView(login)
        }
    }
    private fun addPassword(text:String){
        context?.let { context ->
            val password = TitleTextInput(context)
            password.title = getString(R.string.password)
            password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            password.text = text
            password.isCopy = true
            binding.fields.addView(password)
        }
    }
    private fun addEmail(text:String){
        context?.let { context ->
            val email = TitleTextInput(context)
            email.title = getString(R.string.email)
            email.inputType = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
            email.text = text
            email.isCopy = true
            binding.fields.addView(email)
        }
    }
}