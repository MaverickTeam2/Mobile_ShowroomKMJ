package com.maverick.kmjshowroom.utils

import androidx.fragment.app.FragmentActivity
import android.view.View
import android.widget.ImageView
import com.maverick.kmjshowroom.R

object HeaderHelper {
    fun setupBackButton(activity: FragmentActivity, headerView: View) {
        val backIcon = headerView.findViewById<ImageView>(R.id.icon_close)
        backIcon?.setOnClickListener {
            activity.onBackPressedDispatcher.onBackPressed()
        }
    }
}
