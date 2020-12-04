package com.plexyze.xinfo.repository

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.FragmentRepositoryListBinding
import com.plexyze.xinfo.databinding.FragmentTextFileBinding
import com.plexyze.xinfo.databinding.OpenRepositoryFragmentBinding
import com.plexyze.xinfo.model.RepositoryDao
import com.plexyze.xinfo.viewmodel.viewModelProvider
import javax.inject.Inject


class TextFileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: FragmentTextFileBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_text_file,container,false)

        val viewModel = viewModelProvider(this){
            TextFileViewModel()
        }
        val repository = arguments?.getString("repository") ?:""
        viewModel.repositoryChange(repository)
        viewModel.openTextFile()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

}