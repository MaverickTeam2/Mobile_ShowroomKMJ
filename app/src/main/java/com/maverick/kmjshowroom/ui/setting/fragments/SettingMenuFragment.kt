package com.maverick.kmjshowroom.ui.setting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Database.UserDatabaseHelper
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.utils.HeaderHelper

class SettingMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val headerView = view.findViewById<View>(R.id.header_include)
        val tvHeader: TextView = headerView.findViewById(R.id.text_header)
        tvHeader.text = "Setting"

        HeaderHelper.setupBackButton(requireActivity(), view.findViewById(R.id.header_include))

        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvRole: TextView = view.findViewById(R.id.tvRole)
        val imgProfile: ImageView = view.findViewById(R.id.imgProfile)

        val db = UserDatabaseHelper(requireContext())
        val user = db.getUser()

        user?.let {
            tvName.text = it.full_name ?: "Unknown"
            tvRole.text = it.role + " Showroom" ?: "No Role"

            val avatarPath = (it.avatar_url ?: "").removePrefix("/")
            val fullAvatarUrl = ApiClient.BASE_URL + avatarPath

            Glide.with(requireContext())
                .load(fullAvatarUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .circleCrop()
                .into(imgProfile)
        }

        view.findViewById<MaterialCardView>(R.id.itemGeneral).setOnClickListener {
            findNavController().navigate(R.id.action_to_general)
        }
        view.findViewById<MaterialCardView>(R.id.itemEditProfile).setOnClickListener {
            findNavController().navigate(R.id.action_to_editProfile)
        }
        view.findViewById<MaterialCardView>(R.id.itemSchedule).setOnClickListener {
            findNavController().navigate(R.id.action_to_schedule)
        }
        view.findViewById<MaterialCardView>(R.id.itemContact).setOnClickListener {
            findNavController().navigate(R.id.action_to_contactSocial)
        }
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }
}
