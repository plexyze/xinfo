package com.plexyze.xinfo.explorer

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.ExplorerFragmentBinding
import com.plexyze.xinfo.viewmodel.viewModelProvider
import java.lang.Exception

class ExplorerFragment : Fragment() {

    private lateinit var viewModel: ExplorerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: ExplorerFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.explorer_fragment, container, false
        )
        viewModel = viewModelProvider(this){
            ExplorerViewModel()
        }

        viewModel.choice.observe(viewLifecycleOwner){
            if(it is ExplorerViewModel.Choice.Card){
                val directions = ExplorerFragmentDirections.actionExplorerFragmentToCardFragment(it.id)
                findNavController().navigate(directions)
            }
        }

        binding.viewModel = viewModel

        viewModel.load()
        setHasOptionsMenu(true)
        binding.lifecycleOwner = this
        Log.e("onCreateView",this.toString())
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.explorer_menu, menu)
        viewModel.selected.observe(viewLifecycleOwner){select ->
            when(select){
                is ExplorerViewModel.Select.Non->{
                    menu.children.forEach (::updateDeselected)
                }
                is ExplorerViewModel.Select.Card->{
                    menu.children.forEach (::updateSelectedCard)
                }
                is ExplorerViewModel.Select.Directory->{
                    menu.children.forEach (::updateSelectedDirectory)
                }
                else->{
                    menu.children.forEach{it.isEnabled = false}
                }
            }
        }
    }

    private fun updateDeselected(item: MenuItem){
        when (item.itemId) {
            R.id.new_directory -> item.isEnabled = true
            R.id.new_card -> item.isEnabled = true
            else -> item.isEnabled = false
        }
    }

    private fun updateSelectedCard(item: MenuItem){
        when (item.itemId) {
            R.id.edit_card -> item.isEnabled = true
            else -> item.isEnabled = false
        }
    }

    private fun updateSelectedDirectory(item: MenuItem){
        when (item.itemId) {
            R.id.rename_directory -> item.isEnabled = true
            else -> item.isEnabled = false
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_directory ->{
                val directions = ExplorerFragmentDirections.actionExplorerFragmentToEditDirectoryFragment(viewModel.directoryId,"")
                findNavController().navigate(directions)
                true
            }
            R.id.new_card ->{
                val directions = ExplorerFragmentDirections.actionExplorerFragmentToRenameCardFragment(viewModel.directoryId,"")
                findNavController().navigate(directions)
                true
            }
            R.id.edit_card ->{
                val selected = viewModel.selected.value
                if(selected is ExplorerViewModel.Select.Card){
                    val directions = ExplorerFragmentDirections.actionExplorerFragmentToRenameCardFragment(viewModel.directoryId,selected.id)
                    findNavController().navigate(directions)
                }
                true
            }
            R.id.rename_directory ->{
                val selected = viewModel.selected.value
                if(selected is ExplorerViewModel.Select.Directory){
                    val directions = ExplorerFragmentDirections.actionExplorerFragmentToEditDirectoryFragment(viewModel.directoryId,selected.id)
                    findNavController().navigate(directions)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}