package com.maverick.kmjshowroom.ui.setting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.GenericScheduleResponse
import com.maverick.kmjshowroom.Model.ScheduleItem
import com.maverick.kmjshowroom.Model.UpdateScheduleRequest
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.ui.setting.fragments.TimePickerFragment
import com.maverick.kmjshowroom.utils.LoadingDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SlotAdapter(
    private val context: Context,
    private val slots: MutableList<ScheduleItem>,
    private val onSlotUpdated: () -> Unit
) : RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {

    inner class SlotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnStart: Button = view.findViewById(R.id.btnStartTime)
        val btnEnd: Button = view.findViewById(R.id.btnEndTime)
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteSlot)
    }

    private val loadingDialog = LoadingDialog(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_slot_time, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]

        holder.btnStart.text = slot.jam_buka
        holder.btnEnd.text = slot.jam_tutup

        val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager

        holder.btnStart.setOnClickListener {
            fragmentManager?.let { fm ->
                TimePickerFragment { time ->
                    val oldStart = slot.jam_buka
                    slot.jam_buka = time
                    if (isOverlapping(slot)) {
                        slot.jam_buka = oldStart
                        holder.btnStart.text = oldStart
                        Toast.makeText(context, "Jadwal bentrok!", Toast.LENGTH_SHORT).show()
                        return@TimePickerFragment
                    }
                    holder.btnStart.text = time
                    updateSlot(slot)
                }.show(fm, "startPicker")
            }
        }

        holder.btnEnd.setOnClickListener {
            fragmentManager?.let { fm ->
                TimePickerFragment { time ->
                    val oldEnd = slot.jam_tutup
                    slot.jam_tutup = time
                    if (isOverlapping(slot)) {
                        slot.jam_tutup = oldEnd
                        holder.btnEnd.text = oldEnd
                        Toast.makeText(context, "Jadwal bentrok!", Toast.LENGTH_SHORT).show()
                        return@TimePickerFragment
                    }
                    holder.btnEnd.text = time
                    updateSlot(slot)
                }.show(fm, "endPicker")
            }
        }

        holder.btnDelete.setOnClickListener {
            loadingDialog.show("Menghapus slot...")

            ApiClient.apiService.deleteSchedule(slot.id_schedule)
                .enqueue(object : Callback<GenericScheduleResponse> {
                    override fun onResponse(
                        call: Call<GenericScheduleResponse>,
                        response: Response<GenericScheduleResponse>
                    ) {
                        loadingDialog.dismiss()
                        Toast.makeText(
                            context,
                            response.body()?.message ?: "Slot dihapus",
                            Toast.LENGTH_SHORT
                        ).show()

                        val pos = holder.adapterPosition
                        if (pos != RecyclerView.NO_POSITION) {
                            slots.removeAt(pos)
                            notifyItemRemoved(pos)
                        }

                        onSlotUpdated.invoke()
                    }

                    override fun onFailure(call: Call<GenericScheduleResponse>, t: Throwable) {
                        loadingDialog.dismiss()
                        Toast.makeText(context, "Kesalahan jaringan", Toast.LENGTH_SHORT).show()
                    }
                })
        }

    }

    override fun getItemCount(): Int = slots.size

    private fun isOverlapping(slot: ScheduleItem): Boolean {
        val newStart = slot.jam_buka.replace(":", "").toInt()
        val newEnd = slot.jam_tutup.replace(":", "").toInt()

        if (newStart >= newEnd) return true

        slots.forEach {
            if (it.id_schedule != slot.id_schedule) {
                val start = it.jam_buka.replace(":", "").toInt()
                val end = it.jam_tutup.replace(":", "").toInt()

                if (newStart < end && newEnd > start) {
                    return true
                }
            }
        }
        return false
    }

    private fun updateSlot(slot: ScheduleItem) {

        if (isOverlapping(slot)) {
            Toast.makeText(context, "Jadwal bentrok dengan slot lain!", Toast.LENGTH_SHORT).show()
            onSlotUpdated.invoke()
            return
        }

        loadingDialog.show("Updating...")

        val req = UpdateScheduleRequest(
            id_schedule = slot.id_schedule,
            hari = slot.hari,
            slot_index = slot.slot_index,
            jam_buka = slot.jam_buka,
            jam_tutup = slot.jam_tutup,
            is_active = slot.is_active
        )

        ApiClient.apiService.updateSchedule(req)
            .enqueue(object : Callback<GenericScheduleResponse> {
                override fun onResponse(
                    call: Call<GenericScheduleResponse>,
                    response: Response<GenericScheduleResponse>
                ) {
                    loadingDialog.dismiss()
                    Toast.makeText(
                        context,
                        response.body()?.message ?: "Berhasil update",
                        Toast.LENGTH_SHORT
                    ).show()
                    onSlotUpdated.invoke()
                }

                override fun onFailure(call: Call<GenericScheduleResponse>, t: Throwable) {
                    loadingDialog.dismiss()
                    Toast.makeText(context, "Gagal update slot", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
