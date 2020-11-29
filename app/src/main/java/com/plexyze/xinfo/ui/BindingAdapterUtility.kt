package com.plexyze.xinfo.ui

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("app:eventChange")
fun setEventChange(titleTextInput: TitleTextInput, eventChange: (String)->Unit) {
    titleTextInput.eventChange = eventChange
}

@BindingAdapter("app:adapter")
fun setAdapter(recyclerView: RecyclerView, adapter:SimpleListAdapter) {
    recyclerView.adapter = adapter
}