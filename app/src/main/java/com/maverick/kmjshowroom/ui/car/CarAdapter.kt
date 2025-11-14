package com.maverick.kmjshowroom.ui.car

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.maverick.kmjshowroom.Model.CarData
import com.maverick.kmjshowroom.databinding.CardMobilBinding

class CarAdapter(private val carList: List<CarData>) :
    RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    inner class CarViewHolder(private val binding: CardMobilBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(car: CarData) {
            binding.imgCar.setImageResource(car.imageRes)
            binding.txtTitle.text = car.title
            binding.txtYear.text = car.year
            binding.txtWarnaValue.text = car.warna
            binding.txtStatus.text = car.status
            binding.txtJaraktempuhValue.text = car.jarakTempuh
            binding.txtBahanabakarValue.text = car.bahanBakar
            binding.txtAngsuran.text = car.angsuran
            binding.txtDp.text = car.dp

            val context = binding.root.context
            binding.btnEdit.setOnClickListener {
                Toast.makeText(context, "Edit ${car.title}", Toast.LENGTH_SHORT).show()
            }
            binding.btnDelete.setOnClickListener {
                Toast.makeText(context, "Hapus ${car.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = CardMobilBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return carList.size
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(carList[position])
    }
}
