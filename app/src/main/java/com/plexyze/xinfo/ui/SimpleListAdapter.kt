package com.plexyze.xinfo.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.plexyze.xinfo.R
import com.plexyze.xinfo.databinding.SimpleRowBinding
import com.plexyze.xinfo.model.RowSelector


class SimpleListAdapter: ListAdapter<SimpleListAdapter.Row, SimpleListAdapter.ViewHolder>(
    DiffCallback()
) {

    var onChoice:(String)->Unit = {}
    var onChangeSelected:(Set<String>)->Unit = {}

    val selected
        get() = selector.selected


    private val selector = RowSelector(){
        for(i in 0 until itemCount){
            val row = getItem(i)
            row.selected = it.contains(row.id)
        }
        notifyDataSetChanged()
        onChangeSelected(it)
    }

    fun clearSelected() = selector.clear()


    var isMultiSelect
        get() = selector.isMultiSelect
        set(v){selector.isMultiSelect = v}

    private val onClick:(String)->Unit = {
        if(selector.isEmpty()){
            onChoice(it)
        }else{
            selector.change(it)
        }
    }

    private val onLongClick:(String)->Unit = {
        selector.change(it)
    }

    override fun submitList(list: List<Row>?) {
        if(list != null){
            selector.filter(list.map{it.id})
            list.forEach(){it.selected = selector.contains(it.id)}
        }
        super.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),onClick,onLongClick)
    }

    class ViewHolder private constructor(private val binding: SimpleRowBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(row: Row, onClick:(String)->Unit, onLongClick:(String)->Unit){
            binding.mainLayout.setBackgroundResource(if(row.selected) R.color.color_selected_row else R.color.color_no_selected_row)
            binding.icon.text = row.icon
            binding.nameRow.text = row.name
            binding.comment.text = row.comment
            binding.mainLayout.setOnClickListener(){
                onClick(row.id)
            }
            binding.mainLayout.setOnLongClickListener() {
                onLongClick(row.id)
                true
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SimpleRowBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Row>() {
        override fun areItemsTheSame(oldItem: Row, newItem: Row): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Row, newItem: Row): Boolean {
            return oldItem == newItem
        }
    }

    data class Row(var id:String, var name:String, var icon:String, var comment:String = "", var selected:Boolean = false  )
}