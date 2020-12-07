package com.plexyze.xinfo.repository

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.EditPasswordRepositoryFragmentBinding
import com.plexyze.xinfo.viewmodel.viewModelProvider

class EditPasswordRepositoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: EditPasswordRepositoryFragmentBinding = DataBindingUtil.inflate(inflater,
            R.layout.edit_password_repository_fragment,container,false)
        val repository = arguments?.getString("repository") ?:""

        val viewModel = viewModelProvider(this){
            EditPasswordRepositoryViewModel()
        }

        viewModel.repositoryChange(repository)

        viewModel.edited.observe(viewLifecycleOwner){
            if(it) findNavController().popBackStack()
        }

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }



}