package com.maverick.kmjshowroom.ui.car

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maverick.kmjshowroom.Model.MobilItem
import com.maverick.kmjshowroom.databinding.CardMobilBinding

class CarAdapter(
    private var list: List<MobilItem>,
    private val onItemClick: (MobilItem) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    init {
        setHasStableIds(true)
    }

    inner class CarViewHolder(val binding: CardMobilBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = CardMobilBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val item = list[position]
        val b = holder.binding

        b.txtTitle.text = item.nama_mobil
        b.txtStatus.text = item.status
        b.txtYear.text = item.tahun_mobil.toString()
        b.txtWarnaValue.text = item.warna_exterior
        b.txtJaraktempuhValue.text = "${item.jarak_tempuh} km"
        b.txtBahanabakarValue.text = item.tipe_bahan_bakar
        b.txtAngsuran.text = "Rp ${item.angsuran} x ${item.tenor}"
        b.txtDp.text = "DP Rp ${item.dp}"

        // Foto mobil
        Glide.with(b.root.context)
            .load(item.foto)
            .centerCrop()
            .into(b.imgCar)

        // Apply Warna Status
        applyStatusUI(item.status, b)

        // Klik
        b.cardRoot.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemId(position: Int): Long {
        return list[position].kode_mobil.hashCode().toLong()
    }

    fun updateData(newList: List<MobilItem>) {
        if (list == newList) return
        list = newList
        notifyDataSetChanged()
    }

    // =============================
    //      STATUS UI FUNCTION
    // =============================
    private fun applyStatusUI(status: String, b: CardMobilBinding) {
        when (status.lowercase()) {

            "available" -> {
                b.txtStatus.setBackgroundResource(com.maverick.kmjshowroom.R.drawable.bg_status_completed)
                b.txtStatus.setTextColor(Color.parseColor("#1E8449"))
            }

            "reserved" -> {
                b.txtStatus.setBackgroundResource(com.maverick.kmjshowroom.R.drawable.bg_status_pending)
                b.txtStatus.setTextColor(Color.parseColor("#AF5E24"))
            }

            "sold" -> {
                b.txtStatus.setBackgroundResource(com.maverick.kmjshowroom.R.drawable.bg_status_canceled)
                b.txtStatus.setTextColor(Color.parseColor("#8D3939"))
            }

            "shipping" -> {
                b.txtStatus.setBackgroundResource(com.maverick.kmjshowroom.R.drawable.bg_status_pending)
                b.txtStatus.setTextColor(Color.parseColor("#AF5E24"))
            }

            "delivered" -> {
                b.txtStatus.setBackgroundResource(com.maverick.kmjshowroom.R.drawable.bg_status_pending)
                b.txtStatus.setTextColor(Color.parseColor("#AF5E24"))
            }
        }
    }
}
