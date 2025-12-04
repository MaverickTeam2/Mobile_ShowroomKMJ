package com.maverick.kmjshowroom.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Model.ManageAkun
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.ItemAkunBinding

class ManageAkunAdapter(
    private var list: List<ManageAkun>,
    private val onClick: (ManageAkun) -> Unit,
    private val onToggleStatus: (ManageAkun) -> Unit,
    private val onDelete: (ManageAkun) -> Unit
) : RecyclerView.Adapter<ManageAkunAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAkunBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ManageAkun) {
            binding.nama.text = item.full_name
            binding.email.text = item.email
            binding.role.text = item.role.uppercase()

            // Format last login
            binding.lastLogin.text = item.formattedLastLogin

            // Update badge status
            binding.badgeStatus.text = item.statusText
            if (item.isActive) {
                binding.badgeStatus.setBackgroundResource(R.drawable.bg_badge_active)
                binding.badgeStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
            } else {
                binding.badgeStatus.setBackgroundResource(R.drawable.bg_badge_inactive)
                binding.badgeStatus.setTextColor(binding.root.context.getColor(android.R.color.darker_gray))
            }

            // Update badge role
            when(item.role.lowercase()) {
                "owner" -> {
                    binding.role.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
                }
                "admin" -> {
                    binding.role.setBackgroundResource(R.drawable.bg_badge_admin)
                    binding.role.setTextColor(binding.root.context.getColor(android.R.color.holo_orange_dark))
                }
                else -> {
                    binding.role.setBackgroundResource(R.drawable.bg_badge_admin)
                }
            }

            // Load foto
            val imageUrl = ApiClient.getImageUrl(item.avatar_url)
            Glide.with(binding.root.context)
                .load(imageUrl.ifEmpty { R.drawable.sample_profile })
                .placeholder(R.drawable.sample_profile)
                .error(R.drawable.sample_profile)
                .circleCrop()
                .into(binding.foto)

            // Klik item
            binding.root.setOnClickListener { onClick(item) }

            // PENTING: Lepas listener dulu sebelum set checked
            // Ini mencegah trigger callback saat bind
            binding.statusSwitch.setOnCheckedChangeListener(null)
            binding.statusSwitch.isChecked = item.isActive

            // Baru set listener setelah checked di-set
            binding.statusSwitch.setOnCheckedChangeListener { _, isChecked ->
                // Cegah Owner dinonaktifkan
                if (item.role.equals("owner", ignoreCase = true) && !isChecked) {
                    // Kembalikan ke aktif tanpa trigger callback
                    binding.statusSwitch.setOnCheckedChangeListener(null)
                    binding.statusSwitch.isChecked = true
                    binding.statusSwitch.setOnCheckedChangeListener { _, _ ->
                        // Re-attach listener kosong
                    }

                    android.widget.Toast.makeText(
                        binding.root.context,
                        "Akun Owner tidak dapat dinonaktifkan!",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    return@setOnCheckedChangeListener
                }

                // Pastikan state benar-benar berubah
                if (isChecked != item.isActive) {
                    onToggleStatus(item)
                }
            }

            // Tombol delete
            binding.btnDelete.setOnClickListener {
                onDelete(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAkunBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<ManageAkun>) {
        list = newList
        notifyDataSetChanged()
    }
}