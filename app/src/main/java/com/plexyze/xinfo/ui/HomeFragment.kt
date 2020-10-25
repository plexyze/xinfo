package com.plexyze.xinfo.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.FragmentHomeBinding
import com.plexyze.xinfo.model.*
import com.plexyze.xinfo.viewmodel.HomeViewModel
import com.plexyze.xinfo.viewmodel.viewModelProvider
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false)

        viewModel = viewModelProvider(this){
            HomeViewModel(context!!)
        }

        binding.homeViewModel = viewModel
        binding.passlist.adapter = viewModel.adapter

        return binding.root
    }

}

