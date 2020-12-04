package com.plexyze.xinfo.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.plexyze.xinfo.R

class ConfirmDialog(val message:Int, val ok:()->Unit): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("ConfirmDialog")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(R.string.ok) { _, _ -> ok() }
                .setNegativeButton(R.string.cancel){ _, _ ->  }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

fun Fragment.confirmDialog(message:Int, ok:()->Unit){
    val confirmDialog = ConfirmDialog(message,ok)
    confirmDialog.show(parentFragmentManager,"")
}