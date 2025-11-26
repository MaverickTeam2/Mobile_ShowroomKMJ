package com.maverick.kmjshowroom.ui.setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.CreateScheduleRequest
import com.maverick.kmjshowroom.Model.GenericScheduleResponse
import com.maverick.kmjshowroom.ui.setting.model.DaySchedule
import com.maverick.kmjshowroom.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DayAdapter(
    private val context: Context,
    private val dayList: List<DaySchedule>,
    private val onToggle: (String, Boolean, (Boolean) -> Unit) -> Unit,
    private val onSlotUpdated: () -> Unit
) : RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    inner class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay: TextView = view.findViewById(R.id.tvDayName)
        val switchAvail: Switch = view.findViewById(R.id.switchAvailable)
        val rvSlots: RecyclerView = view.findViewById(R.id.rvSlots)
        val tvAddSlot: TextView = view.findViewById(R.id.tvAddSlot)
        val tvUnavailable: LinearLayout = view.findViewById(R.id.layoutUnavailable)
        val progressBar: ProgressBar = view.findViewById(R.id.progressToggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_schedule_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val item = dayList[position]
        holder.tvDay.text = item.dayName

        val slotAdapter = SlotAdapter(context, item.slots, onSlotUpdated)
        holder.rvSlots.layoutManager = LinearLayoutManager(context)
        holder.rvSlots.adapter = slotAdapter

        fun updateUI(isAvailable: Boolean) {
            holder.tvUnavailable.visibility = if (isAvailable) View.GONE else View.VISIBLE
            holder.rvSlots.visibility = if (isAvailable) View.VISIBLE else View.GONE
            holder.tvAddSlot.visibility = if (isAvailable) View.VISIBLE else View.GONE
        }

        updateUI(item.available)

        holder.switchAvail.setOnCheckedChangeListener(null)
        holder.switchAvail.isChecked = item.available

        holder.switchAvail.setOnCheckedChangeListener { _, isChecked ->

            // Disable switch & show progress
            holder.switchAvail.isEnabled = false
            holder.progressBar.visibility = View.VISIBLE

            onToggle(item.dayName, isChecked) { backendSuccess ->
                if (backendSuccess) {
                    item.available = isChecked
                    if (!isChecked) {
                        item.slots.forEach { it.is_active = 0 }
                        holder.rvSlots.adapter?.notifyDataSetChanged()
                    }
                }

                // Update UI sesuai server
                updateUI(item.available)
                holder.switchAvail.isChecked = item.available

                // Reset UI state
                holder.switchAvail.isEnabled = true
                holder.progressBar.visibility = View.GONE
            }
        }

        holder.tvAddSlot.setOnClickListener {
            val req = CreateScheduleRequest(
                hari = item.dayName,
                slot_index = item.slots.size + 1,
                jam_buka = "09:00",
                jam_tutup = "10:00",
                is_active = if (item.available) 1 else 0
            )

            ApiClient.apiService.createSchedule(req).enqueue(object :
                Callback<GenericScheduleResponse> {
                override fun onResponse(call: Call<GenericScheduleResponse>, response: Response<GenericScheduleResponse>) {
                    Toast.makeText(context, response.body()?.message ?: "Berhasil", Toast.LENGTH_SHORT).show()
                    onSlotUpdated.invoke()
                }

                override fun onFailure(call: Call<GenericScheduleResponse>, t: Throwable) {
                    Toast.makeText(context, "Gagal menambah slot", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun getItemCount(): Int = dayList.size
}

