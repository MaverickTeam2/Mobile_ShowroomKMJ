package com.maverick.kmjshowroom.ui.car

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

    inner class CarViewHolder(val binding: CardMobilBinding) :
        RecyclerView.ViewHolder(binding.root)

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

        // Judul
        b.txtTitle.text = item.nama_mobil

        // Status
        b.txtStatus.text = item.status

        // Tahun
        b.txtYear.text = item.tahun_mobil.toString()

        // Warna
        b.txtWarnaValue.text = item.warna_exterior

        // Jarak Tempuh
        b.txtJaraktempuhValue.text = "${item.jarak_tempuh} km"

        // Bahan Bakar
        b.txtBahanabakarValue.text = item.tipe_bahan_bakar

        // DP & Angsuran
        b.txtAngsuran.text = "Rp ${item.angsuran} x ${item.tenor}"
        b.txtDp.text = "DP Rp ${item.dp}"

        // Foto mobil
        Glide.with(b.root.context)
            .load(item.foto)
            .centerCrop()
            .into(b.imgCar)

        // 🔥 Klik Card -> DetailMobilActivity
        b.cardRoot.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<MobilItem>) {
        list = newList
        notifyDataSetChanged()
    }
}
