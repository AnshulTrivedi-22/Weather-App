package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.GridItemsBinding


class AdapterForTheGrid(val items: MutableList<ItemsOfGrid>):
    RecyclerView.Adapter<AdapterForTheGrid.ViewHolder>() {
    inner class ViewHolder (private val binding: GridItemsBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(items:ItemsOfGrid){
            binding.imageView.setImageResource(items.icon)
            binding.textView.text = items.update
            binding.textView9.text = items.des
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val bind = GridItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(bind)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("Pos", "$position")
        val items = items[position]
        return holder.bind(items)
    }







       /* if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                // Swap items from "fromPosition" to "toPosition
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                // Swap items from "fromPosition" to "toPosition"
                Collections.swap(items, i, i - 1)
            }
        }
        //Log.d("onItemMove", "Moving item from $fromPosition to $toPosition")
        notifyItemMoved(fromPosition, toPosition)
        //notifyItemRemoved(position1, position2)*/

}