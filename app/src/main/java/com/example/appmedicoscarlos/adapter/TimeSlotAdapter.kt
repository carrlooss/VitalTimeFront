package com.example.appmedicoscarlos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.appmedicoscarlos.R

class TimeSlotAdapter(
    private var items: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.VH>() {

    private var selectedPos = RecyclerView.NO_POSITION

    fun updateItems(newItems: List<String>) {
        items = newItems
        selectedPos = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_time_slot, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val slot = items[position]
        holder.tv.text = slot

        holder.card.isSelected = (position == selectedPos)
        holder.itemView.setOnClickListener {
            val prev = selectedPos
            selectedPos = holder.adapterPosition
            notifyItemChanged(prev)
            notifyItemChanged(selectedPos)
            onClick(slot)
        }
    }

    override fun getItemCount(): Int = items.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tv: TextView = view.findViewById(R.id.tvTime)
        val card: CardView = view.findViewById(R.id.cardTime)
    }
}
