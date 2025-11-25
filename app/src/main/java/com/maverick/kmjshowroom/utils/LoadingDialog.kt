package com.maverick.kmjshowroom.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.maverick.kmjshowroom.R

class LoadingDialog(private val context: Context) {

    private var dialog: Dialog? = null

    fun show(message: String = "Processing...") {
        if (dialog != null && dialog!!.isShowing) return

        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(context).inflate(R.layout.loading_fullscreen, null)

        dialog!!.setContentView(view)
        dialog!!.setCancelable(false)
        dialog!!.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT
        )

        dialog!!.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}
