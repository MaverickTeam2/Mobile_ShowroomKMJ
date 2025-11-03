package com.maverick.kmjshowroom.ui.setting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.utils.HeaderHelper
import com.google.android.material.card.MaterialCardView

class SettingMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        HeaderHelper.setupBackButton(requireActivity(), view.findViewById(R.id.header_include))

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
        view.findViewById<MaterialCardView>(R.id.itemBackup).setOnClickListener {
            findNavController().navigate(R.id.action_to_backup)
        }
    }
}
