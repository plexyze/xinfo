package com.plexyze.xinfo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.plexyze.xinfo.R
import com.plexyze.xinfo.model.PassRowEntity



class PassListAdapter: RecyclerView.Adapter<PassListAdapter.ViewHolder>() {
    var data = listOf<PassRowEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.item_pass, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size
    class ViewHolder(private val view: View): RecyclerView.ViewHolder(view){
        private val textName:TextView = view.findViewById(R.id.textName)
        fun bind(passRowEntity: PassRowEntity){
            textName.text = "${passRowEntity.name} - ${passRowEntity.login}"
        }
    }
}