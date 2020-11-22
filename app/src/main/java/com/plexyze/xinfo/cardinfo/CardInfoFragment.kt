package com.plexyze.xinfo.cardinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.FragmentCardInfoBinding
import com.plexyze.xinfo.viewmodel.viewModelProvider


private const val ARG_ID = "id"

/**
 * A simple [CardInfoFragment] subclass.
 * Use the [CardInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CardInfoFragment : Fragment() {

    lateinit var binding: FragmentCardInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_card_info, container, false)
        val id = arguments?.getLong(ARG_ID) ?:0

        val viewModel = viewModelProvider(this){
            CardInfoViewModel(id)
        }

        viewModel.state.observe(this){
            updateState(it)
        }

        binding.btnSave.setOnClickListener(){
            viewModel.btnSave(
                name = binding.fileName.text,
                login = binding.login.text,
                password = binding.password.text,
                email = binding.email.text
            )
        }

        binding.btnEdit.setOnClickListener(){
            viewModel.btnEdit()
        }

        return binding.root
    }

    fun updateState(state: CardInfoViewModel.State){
        when (state){

            is CardInfoViewModel.State.Load -> {
                binding.fileName.isEnabled = false
                binding.login.isEnabled = false
                binding.password.isEnabled = false
                binding.email.isEnabled = false
                binding.btnSave.isEnabled = false
                binding.btnEdit.isEnabled = false
            }

            is CardInfoViewModel.State.Edit ->{
                binding.fileName.isEnabled = true
                binding.login.isEnabled = true
                binding.password.isEnabled = true
                binding.email.isEnabled = true
                binding.fileName.text = state.name
                binding.login.text = state.login
                binding.password.text = state.password
                binding.email.text = state.email
                binding.btnSave.isEnabled = true
                binding.btnEdit.isEnabled = false
            }

            is CardInfoViewModel.State.Info ->{
                binding.fileName.isEnabled = false
                binding.login.isEnabled = false
                binding.password.isEnabled = false
                binding.email.isEnabled = false
                binding.fileName.text = state.name
                binding.login.text = state.login
                binding.password.text = state.password
                binding.email.text = state.email
                binding.btnSave.isEnabled = false
                binding.btnEdit.isEnabled = true
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param [id] password card id
         * @return A new instance of fragment CardInfoFragment.
         */
        @JvmStatic
        fun newInstance(id: Long) =
            CardInfoFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ID, id)
                }
            }
    }


}