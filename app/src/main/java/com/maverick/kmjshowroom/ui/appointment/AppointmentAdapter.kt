package com.maverick.kmjshowroom.ui.appointment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.maverick.kmjshowroom.Model.Appointment
import com.maverick.kmjshowroom.R

class AppointmentAdapter(
    private var appointments: List<Appointment>,
    private val onRespondClick: (Appointment) -> Unit,
    private val onCancelClick: (Appointment) -> Unit,
    private val onMarkDoneClick: (Appointment) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvBadge: TextView = itemView.findViewById(R.id.tv_badge)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        val tvDatetime: TextView = itemView.findViewById(R.id.tv_datetime)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        val tvPhone: TextView = itemView.findViewById(R.id.tv_phone)
        val tvMobil: TextView = itemView.findViewById(R.id.tv_mobil)
        val tvNote: TextView = itemView.findViewById(R.id.tv_note)
        val btnRespond: MaterialButton = itemView.findViewById(R.id.btn_respond)
        val btnBatalkan: MaterialButton = itemView.findViewById(R.id.btn_batalkan)
        val btnTandaiSelesai: Button = itemView.findViewById(R.id.btn_tandai_selesai)
        val layoutButtons: View = itemView.findViewById(R.id.layout_buttons)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]

        // Set data
        holder.tvName.text = appointment.namaUser ?: "Unknown"

        // Set badge berdasarkan uji_beli
        holder.tvBadge.text = when (appointment.ujiBeli) {
            "1" -> "Test Drive"
            "2" -> "Beli Online"
            else -> appointment.jenisJanji // fallback ke jenisJanji jika nilai lain
        }

        holder.tvDatetime.text = "${appointment.tanggal} ${appointment.waktu}"
        holder.tvEmail.text = "Email: ${appointment.emailUser ?: "-"}"
        holder.tvPhone.text = "Phone: ${appointment.noTelp}"
        holder.tvMobil.text = "Mobil: ${appointment.namaMobil ?: "-"}"
        holder.tvNote.text = appointment.note ?: "Tidak ada catatan"

        // Set status dengan warna
        when (appointment.status.lowercase()) {
            "pending" -> {
                holder.tvStatus.text = "Pending"
                holder.tvStatus.setTextColor(Color.parseColor("#FF9800"))
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
                holder.layoutButtons.visibility = View.VISIBLE
                holder.btnTandaiSelesai.visibility = View.GONE
            }
            "responded" -> {
                holder.tvStatus.text = "Responded"
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_responded)
                holder.layoutButtons.visibility = View.GONE
                holder.btnTandaiSelesai.visibility = View.VISIBLE
            }
            "closed", "canceled" -> {
                holder.tvStatus.text = if (appointment.status == "closed") "Selesai" else "Dibatalkan"
                holder.tvStatus.setTextColor(Color.parseColor("#F44336"))
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_canceled)
                holder.layoutButtons.visibility = View.GONE
                holder.btnTandaiSelesai.visibility = View.GONE
            }
        }

        // Click listeners
        holder.btnRespond.setOnClickListener {
            onRespondClick(appointment)
        }

        holder.btnBatalkan.setOnClickListener {
            onCancelClick(appointment)
        }

        holder.btnTandaiSelesai.setOnClickListener {
            onMarkDoneClick(appointment)
        }
    }

    override fun getItemCount(): Int = appointments.size

    fun updateData(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }
}