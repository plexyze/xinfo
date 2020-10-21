package com.plexyze.xinfo.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.plexyze.xinfo.R
import com.plexyze.xinfo.model.PassRowEntity
import com.plexyze.xinfo.model.Passwords
import com.plexyze.xinfo.model.PasswordsEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var passwords: Passwords
    val adapter = PassListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let { passwords = Passwords(it) }
        passlist.adapter = adapter
        load();

        btnTest.setOnClickListener { v: View ->
            save()
            load()
        }
    }

    fun load(){
        val dispose = passwords.load()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({passwordsEntity ->
                adapter.data = passwordsEntity.passlist
            },{
                adapter.data = listOf(PassRowEntity("non","non"))
            })
    }
    fun save(){
        val rows = listOf(  PassRowEntity(login = "denis", password = "pass1", mail="non"),
                            PassRowEntity(login = "flusa", password = "pass234"),
                            PassRowEntity(login = "loggin", password = "654321")
        )
        val pass = PasswordsEntity(passlist = rows)
        val dispose = passwords.save(pass)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ok ->
                Log.i("EE","Ok")
            },{
                Log.i("EE","Error")
            })
    }



}

