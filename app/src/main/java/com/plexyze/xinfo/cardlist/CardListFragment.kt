package com.plexyze.xinfo.cardlist

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.FragmentCardListBinding
import com.plexyze.xinfo.viewmodel.viewModelProvider


/**
 * A simple [Fragment] subclass.
 */
class CardListFragment : Fragment() {

    private lateinit var viewModel: CardListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCardListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_card_list, container, false
        )

        viewModel = viewModelProvider(this){
            CardListViewModel()
        }

        viewModel.adapter.onClick = {
            val directions = CardListFragmentDirections.actionHomeFragmentToCardInfoFragment(it.id)
            findNavController().navigate(directions)
        }

        viewModel.load()

        binding.thisFragment = this
        binding.viewModel = viewModel
        binding.passlist.adapter = viewModel.adapter

        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.card_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.newFile -> {
                newFile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun newFile(){
        val directions = CardListFragmentDirections.actionHomeFragmentToCardInfoFragment(0L)
        findNavController().navigate(directions)


    }
}

