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
import com.plexyze.xinfo.databinding.OpenRepositoryFragmentBinding
import com.plexyze.xinfo.viewmodel.viewModelProvider

class OpenRepositoryFragment : Fragment() {

    private lateinit var viewModel: OpenRepositoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding: OpenRepositoryFragmentBinding =
            DataBindingUtil.inflate(inflater,R.layout.open_repository_fragment,container,false)

        val viewModel = viewModelProvider(this){
            OpenRepositoryViewModel()
        }
        val repository = arguments?.getString("repository") ?:""
        viewModel.repositoryChange(repository)
        viewModel.onOpened = {
            var directions = OpenRepositoryFragmentDirections.actionOpenRepositoryFragmentToCardListFragment()
            findNavController().navigate(directions)
        }
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        return binding.root
    }


}