package com.plexyze.xinfo.repository

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.FragmentRepositoryListBinding
import com.plexyze.xinfo.ui.confirmDialog
import com.plexyze.xinfo.viewmodel.viewModelProvider


/**
 * A simple [Fragment] subclass.
 * Use the [RepositoryListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RepositoryListFragment : Fragment() {

    private lateinit var viewModel: RepositoryListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentRepositoryListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_repository_list, container, false
        )

        viewModel = viewModelProvider(this){
            RepositoryListViewModel()
        }
        viewModel.choices.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                val directions = RepositoryListFragmentDirections.actionRepositoryListFragmentToOpenRepositoryFragment(it)
                findNavController().navigate(directions)
            }
        }

        binding.viewModel = viewModel

        viewModel.load()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.repository_list_menu, menu)
        viewModel.selected.observe(viewLifecycleOwner){seleted->
            when(seleted){
                is RepositoryListViewModel.Select.Non ->
                    menu.children.forEach(::updateNonSelected)
                is RepositoryListViewModel.Select.Selected ->
                    menu.children.forEach(::updateSelected)
            }
        }
    }


    private fun updateSelected(item: MenuItem){
        when (item.itemId) {
            R.id.open_repository -> item.isEnabled = true
            R.id.delete_repository -> item.isEnabled = true
            R.id.edit_password -> item.isEnabled = true
            R.id.open_as_text -> item.isEnabled = true
            else -> item.isEnabled = false
        }
    }
    private fun updateNonSelected(item: MenuItem){
        when (item.itemId) {
            R.id.new_repository -> item.isEnabled = true
            else -> item.isEnabled = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val selected = viewModel.selected.value
        return when (item.itemId) {
            R.id.new_repository -> {
                val directions = RepositoryListFragmentDirections.actionRepositoryListFragmentToNewRepositoryFragment()
                findNavController().navigate(directions)
                true
            }
            R.id.open_repository ->{
                if(selected is RepositoryListViewModel.Select.Selected){
                    val directions = RepositoryListFragmentDirections.actionRepositoryListFragmentToOpenRepositoryFragment(selected.name)
                    findNavController().navigate(directions)
                }
                true
            }

            R.id.edit_password -> {
                if(selected is RepositoryListViewModel.Select.Selected){
                    val directions = RepositoryListFragmentDirections.actionRepositoryListFragmentToEditPasswordRepositoryFragment(selected.name)
                    findNavController().navigate(directions)
                }
                true
            }
            R.id.delete_repository -> {
                if(selected is RepositoryListViewModel.Select.Selected){
                    confirmDialog{
                        viewModel.deleteRepository(selected.name)
                    }
                }
                true
            }
            R.id.open_as_text -> {
                if(selected is RepositoryListViewModel.Select.Selected){
                    val directions = RepositoryListFragmentDirections.actionRepositoryListFragmentToTextFileFragment(selected.name)
                    findNavController().navigate(directions)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}