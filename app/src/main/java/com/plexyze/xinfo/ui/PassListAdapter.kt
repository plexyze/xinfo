package com.plexyze.xinfo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.plexyze.xinfo.R
import com.plexyze.xinfo.model.PassRowEntity

class TextItemViewHolder(val textView: TextView): RecyclerView.ViewHolder(textView)

class PassListAdapter: RecyclerView.Adapter<TextItemViewHolder>() {
    var data = listOf<PassRowEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.item_pass, parent, false) as TextView
        return TextItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        holder.textView.text = "${data[position].login} ${data[position].password}"
    }

    override fun getItemCount() = data.size
}