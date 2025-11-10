package com.maverick.kmjshowroom.ui.setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.ui.setting.fragments.TimePickerFragment

class SlotAdapter(private val context: Context, private val slots: MutableList<String>) :
    RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    inner class SlotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnStart: Button = view.findViewById(R.id.btnStartTime)
        val btnEnd: Button = view.findViewById(R.id.btnEndTime)
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_slot_time, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val data = slots[position].split("-")
        holder.btnStart.text = data.getOrNull(0) ?: "09:00"
        holder.btnEnd.text = data.getOrNull(1) ?: "17:00"

        // Ambil FragmentManager
        val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager

        holder.btnStart.setOnClickListener {
            fragmentManager?.let {
                TimePickerFragment { time ->
                    holder.btnStart.text = time
                    updateSlot(position, time, isStart = true)
                }.show(it, "timePickerStart")
            }
        }

        holder.btnEnd.setOnClickListener {
            fragmentManager?.let {
                TimePickerFragment { time ->
                    holder.btnEnd.text = time
                    updateSlot(position, time, isStart = false)
                }.show(it, "timePickerEnd")
            }
        }

        holder.btnDelete.setOnClickListener {
            val pos = holder.adapterPosition
            slots.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }

    override fun getItemCount(): Int = slots.size

    private fun updateSlot(position: Int, newTime: String, isStart: Boolean) {
        val parts = slots[position].split("-").toMutableList()
        if (parts.size < 2) parts.add("")
        if (isStart) parts[0] = newTime else parts[1] = newTime
        slots[position] = "${parts[0]}-${parts[1]}"
    }
}
