package com.maverick.kmjshowroom.ui.akun

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.FragmentAkunBinding
import com.maverick.kmjshowroom.Model.Akun

class AkunFragment : Fragment() {

    private var _binding: FragmentAkunBinding? = null
    private val binding get() = _binding!!

    // --- Dummy data akun
    private val akunList = listOf(
        Akun("Michael Owen", "michael.owen@gmail.com", "Last login: 2 hours ago", "Admin", 1, true),
        Akun("Samantha Ray", "sam.ray@yahoo.com", "Last login: 3 days ago", "Staff", 1, true),
        Akun(
            "Jonathan Vega",
            "jon.vega@outlook.com",
            "Last login: 2 weeks ago",
            "Viewer",
            0,
            false
        ),
        Akun("Ananta Widayani", "ananta.wi@gmail.com", "Last login: 1 month ago", "Admin", 0, true)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAkunBinding.inflate(inflater, container, false)
        val root: View = binding.root

        populateDummyAkun(inflater)

        return root
    }

    // --- Fungsi untuk menambahkan card akun ke container
    private fun populateDummyAkun(inflater: LayoutInflater) {
        val container: LinearLayout = binding.akunContainer
        container.removeAllViews()

        for (akun in akunList) {
            val itemView = inflater.inflate(R.layout.item_akun, container, false) as CardView

            val txtName = itemView.findViewById<TextView>(R.id.txtName)
            val txtEmail = itemView.findViewById<TextView>(R.id.txtEmail)
            val txtLogin = itemView.findViewById<TextView>(R.id.txtLogin)
            val badgeRole = itemView.findViewById<TextView>(R.id.badgeRole)
            val badgeStatus = itemView.findViewById<TextView>(R.id.badgeStatus)
            val switchActive = itemView.findViewById<Switch>(R.id.switchActive)
            val btnDelete = itemView.findViewById<ImageButton>(R.id.btnDelete)
            val imgProfile = itemView.findViewById<ImageView>(R.id.imgProfile)

            // --- Isi data
            txtName.text = akun.nama
            txtEmail.text = akun.email
            txtLogin.text = akun.lastLogin
            badgeRole.text = akun.role
            badgeStatus.text = if (akun.status == 1) "Aktif" else "Nonaktif"
            switchActive.isChecked = akun.aktif

            // --- Ganti tampilan status badge
            if (akun.status == 1) {
                badgeStatus.setBackgroundResource(R.drawable.bg_badge_active)
                badgeStatus.setTextColor(ContextCompat.getColor(context, R.color.green_status))
            } else {
                badgeStatus.setBackgroundResource(R.drawable.bg_badge_inactive)
                badgeStatus.setTextColor(ContextCompat.getColor(context, R.color.red_status))
            }

            // --- Event listener contoh
            btnDelete.setOnClickListener {
                container.removeView(itemView)
            }

            switchActive.setOnCheckedChangeListener { _, isChecked ->
                badgeStatus.text = if (isChecked) "Aktif" else "Nonaktif"
            }

            // Tambahkan ke container
            container.addView(itemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
