package com.plexyze.xinfo.cardlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.plexyze.xinfo.databinding.ItemPassBinding
import com.plexyze.xinfo.model.CardEntity
import java.text.SimpleDateFormat
import java.util.*


class CardListAdapter: ListAdapter<CardEntity, CardListAdapter.ViewHolder>(
    DiffCallback()
) {
    var onClick:(CardEntity)->Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)){onClick(it)}
    }

    class ViewHolder private constructor(private val binding: ItemPassBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(cardEntity: CardEntity, onClick:(CardEntity)->Unit){
            binding.textName.text = cardEntity.name
            val date = Date(cardEntity.date)
            val format = "dd.MM.yyyy HH:mm"
            val simpleDateFormat = SimpleDateFormat(format)
            binding.date.text = simpleDateFormat.format(date)
            binding.mainLayout.setOnClickListener(){
                onClick(cardEntity)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemPassBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<CardEntity>() {
        override fun areItemsTheSame(oldItem: CardEntity, newItem: CardEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CardEntity, newItem: CardEntity): Boolean {
            return oldItem == newItem
        }
    }

}