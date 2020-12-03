package com.plexyze.xinfo.explorer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.RenameDirectoryFragmentBinding
import com.plexyze.xinfo.viewmodel.viewModelProvider

class RenameDirectoryFragment : Fragment() {

    lateinit var icons:List<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding: RenameDirectoryFragmentBinding =
            DataBindingUtil.inflate(inflater,R.layout.rename_directory_fragment,container,false)
        val parentId = arguments?.getString("parentId") ?:""
        val directoryId = arguments?.getString("directoryId") ?:""

        icons = resources.getStringArray(R.array.icon).toList()

        val viewModel = viewModelProvider(this){
            RenameDirectoryViewModel(parentId = parentId,directoryId = directoryId)
        }
        viewModel.onEdited = {
            findNavController().popBackStack()
        }
        binding.viewModel = viewModel

        binding.thisFragment = this

        binding.setLifecycleOwner(this)
        return binding.root
    }

}