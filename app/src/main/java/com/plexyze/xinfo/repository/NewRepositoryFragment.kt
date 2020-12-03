package com.plexyze.xinfo.repository

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.NewRepositoryFragmentBinding
import com.plexyze.xinfo.viewmodel.viewModelProvider

class NewRepositoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: NewRepositoryFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.new_repository_fragment, container, false
        )
        val viewModel = viewModelProvider(this){
            NewRepositoryViewModel()
        }
        viewModel.onCreateRepository = {
            findNavController().popBackStack()
        }
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }
}