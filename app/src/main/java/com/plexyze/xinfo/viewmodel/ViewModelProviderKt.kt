package com.plexyze.xinfo.viewmodel

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders


class ViewModelProviderFactoryImpl<T: ViewModel> (val createObj:()->T): ViewModelProvider.Factory{
    override fun <VM : ViewModel?> create(modelClass: Class<VM>): VM {
        @Suppress("UNCHECKED_CAST")
        return createObj() as VM
    }
}

inline fun <reified  T: ViewModel> viewModelProvider(fragmentActivity: FragmentActivity, noinline createObj:()->T):T{
    val factory = ViewModelProviderFactoryImpl(createObj)
    return ViewModelProviders.of(fragmentActivity,factory).get(T::class.java)
}

inline fun <reified  T: ViewModel> viewModelProvider(fragment:Fragment, noinline createObj:()->T):T{
    val factory = ViewModelProviderFactoryImpl(createObj)
    return ViewModelProviders.of(fragment,factory).get(T::class.java)
}
