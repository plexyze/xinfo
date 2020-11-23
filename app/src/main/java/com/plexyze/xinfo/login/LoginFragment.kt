package com.plexyze.xinfo.login

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.plexyze.xinfo.R
import com.plexyze.xinfo.cardlist.CardListFragmentDirections
import com.plexyze.xinfo.databinding.FragmentLoginBinding
import com.plexyze.xinfo.di.App
import com.plexyze.xinfo.model.PasswordDao
import kotlinx.coroutines.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    @Inject
    lateinit var passwordDao: PasswordDao

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main +  job)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding:FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false)

        binding.btnLogin.setOnClickListener(){
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()
            val isOk:()->Unit = {
                val directions = LoginFragmentDirections.actionLoginFragmentToCardListFragment()
                findNavController().navigate(directions)
            }
            val isError:(String)->Unit = {
                binding.log.visibility = View.VISIBLE
                binding.log.text = it
            }
            uiScope.launch {
                passwordDao.login(login,password,isOk,isError)
            }
        }

        binding.btnRegistration.setOnClickListener(){
            val login = binding.login.text.toString()
            val password = binding.password.text.toString()
            val isOk:()->Unit = {
                binding.log.visibility = View.VISIBLE
                binding.log.text = "Successful"
            }
            val isError:(String)->Unit = {
                binding.log.visibility = View.VISIBLE
                binding.log.text = it
            }
            uiScope.launch {
                passwordDao.registration(login,password,isOk,isError)
            }

        }

        return binding.root
    }

    init {
        App.appComponent.inject(this)
    }

}