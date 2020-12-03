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
import com.plexyze.xinfo.databinding.DeleteRepositoryFragmentBinding
import com.plexyze.xinfo.databinding.EditPasswordRepositoryFragmentBinding
import com.plexyze.xinfo.viewmodel.viewModelProvider

class DeleteRepositoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DeleteRepositoryFragmentBinding = DataBindingUtil.inflate(inflater,
            R.layout.delete_repository_fragment,container,false)
        val repository = arguments?.getString("repository") ?:""

        val viewModel = viewModelProvider(this){
            DeleteRepositoryViewModel(repository)
        }

        viewModel.onDeletedRepository = {
            findNavController().popBackStack()
        }

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }

}