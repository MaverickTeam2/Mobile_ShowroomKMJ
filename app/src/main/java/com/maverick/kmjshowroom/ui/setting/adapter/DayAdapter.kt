package com.maverick.kmjshowroom.ui.setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.ui.setting.model.DaySchedule

class DayAdapter(private val context: Context, private val dayList: List<DaySchedule>) :
    RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    inner class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDayName)
        val switchAvail: Switch = view.findViewById(R.id.switchAvailable)
        val rvSlots: RecyclerView = view.findViewById(R.id.rvSlots)
        val tvAddSlot: TextView = view.findViewById(R.id.tvAddSlot)
        val tvUnavailable: LinearLayout = view.findViewById(R.id.layoutUnavailable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_schedule_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val item = dayList[position]
        holder.tvDay.text = item.dayName

        val slotAdapter = SlotAdapter(context, item.slots)
        holder.rvSlots.layoutManager = LinearLayoutManager(context)
        holder.rvSlots.adapter = slotAdapter

        holder.switchAvail.isChecked = item.available
        holder.tvUnavailable.visibility = if (item.available) View.GONE else View.VISIBLE
        holder.rvSlots.visibility = if (item.available) View.VISIBLE else View.GONE
        holder.tvAddSlot.visibility = if (item.available) View.VISIBLE else View.GONE

        holder.switchAvail.setOnCheckedChangeListener { _, isChecked ->
            item.available = isChecked
            holder.tvUnavailable.visibility = if (isChecked) View.GONE else View.VISIBLE
            holder.rvSlots.visibility = if (isChecked) View.VISIBLE else View.GONE
            holder.tvAddSlot.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        holder.tvAddSlot.setOnClickListener {
            item.slots.add("09:00-09:00")
            slotAdapter.notifyItemInserted(item.slots.size - 1)
        }
    }

    override fun getItemCount(): Int = dayList.size
}
