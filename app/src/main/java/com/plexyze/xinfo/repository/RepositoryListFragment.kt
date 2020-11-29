package com.plexyze.xinfo.repository

import android.os.Bundle
import android.view.*
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.FragmentRepositoryListBinding
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
        viewModel.onChoices = {
            val directions = RepositoryListFragmentDirections.actionRepositoryListFragmentToOpenRepositoryFragment(it)
            findNavController().navigate(directions)
        }

        binding.viewModel = viewModel

        viewModel.load()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.repository_list_menu, menu)
        updateMenuEnabled(menu)
        viewModel.onChangeSelected = {
            updateMenuEnabled(menu)
        }
    }

    private fun updateMenuEnabled(menu: Menu){
        menu.children.forEach {
            updateOptionsItemEnabled(it)
        }
    }

    private fun updateOptionsItemEnabled(item: MenuItem){
        if (viewModel.selected.isEmpty()){
            when (item.itemId) {
                R.id.new_repository -> item.isEnabled = true
                else -> item.isEnabled = false
            }
        }else{
            when (item.itemId) {
                R.id.open_repository -> item.isEnabled = true
                R.id.delete_repository -> item.isEnabled = true
                R.id.edit_password -> item.isEnabled = true
                else -> item.isEnabled = false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_repository ->{
                viewModel.selected.forEach(){
                    val directions = RepositoryListFragmentDirections.actionRepositoryListFragmentToOpenRepositoryFragment(it)
                    findNavController().navigate(directions)
                }
                true
            }
            R.id.new_repository -> {
                val directions = RepositoryListFragmentDirections.actionRepositoryListFragmentToNewRepositoryFragment()
                findNavController().navigate(directions)
                true
            }
            R.id.edit_password -> {
                viewModel.selected.forEach(){
                    val directions = RepositoryListFragmentDirections.actionRepositoryListFragmentToEditPasswordRepositoryFragment(it)
                    findNavController().navigate(directions)
                }
                true
            }
            R.id.delete_repository -> {
                viewModel.selected.forEach(){
                    val directions = RepositoryListFragmentDirections.actionRepositoryListFragmentToDeleteRepositoryFragment(it)
                    findNavController().navigate(directions)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}