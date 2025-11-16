package com.maverick.kmjshowroom.ui.car

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maverick.kmjshowroom.Model.MobilItem
import com.maverick.kmjshowroom.databinding.CardMobilBinding

class CarAdapter(
    private var list: List<MobilItem>,
    private val onClick: (MobilItem) -> Unit
) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    inner class CarViewHolder(val binding: CardMobilBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = CardMobilBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
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

        b.txtAngsuran.text = "Rp ${item.angsuran}"

        b.txtDp.text = "DP Rp ${item.dp}"

        Glide.with(b.root.context)
            .load(item.foto)
            .centerCrop()
            .into(b.imgCar)

        b.root.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<MobilItem>) {
        list = newList
        notifyDataSetChanged()
    }
}
