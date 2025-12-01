package com.maverick.kmjshowroom.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.Model.MenuModel
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.ItemMenuBinding

class AllMenuAdapter(
    private val list: List<MenuModel>,
    private val onClick: (MenuModel) -> Unit
) : RecyclerView.Adapter<AllMenuAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val img = itemView.findViewById<ImageView>(R.id.imgMenu)
        private val text = itemView.findViewById<TextView>(R.id.txtMenu)

        fun bind(model: MenuModel) {
            img.setImageResource(model.icon)
            text.text = model.name

            itemView.setOnClickListener { onClick(model) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}

