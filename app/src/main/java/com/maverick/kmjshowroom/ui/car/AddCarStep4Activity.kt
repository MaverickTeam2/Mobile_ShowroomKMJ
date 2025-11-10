package com.maverick.kmjshowroom.ui.car

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.AddCarstep4Binding

class AddCarStep4Activity : AppCompatActivity() {

    private lateinit var binding: AddCarstep4Binding
    private var selectedStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddCarstep4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        setupStatusButtons()
        setupProgressindicator()
    }

    private fun setupHeader(){
        binding.layoutHeaderadd.iconClose.setOnClickListener {
            finish()
        }
    }
    private fun setupStatusButtons() {
        val statusButtons = listOf(
            binding.btnAvailable to "Available",
            binding.btnReserved to "Reserved",
            binding.btnSold to "Sold",
            binding.btnShipping to "Shipping",
            binding.btnDelivered to "Delivered"
        )

        val selectedBg = ContextCompat.getColor(this, R.color.blue_50)
        val selectedText = ContextCompat.getColor(this, R.color.blue_700)
        val defaultBg = ContextCompat.getColor(this, R.color.white)
        val defaultText = ContextCompat.getColor(this, R.color.loginText)

        for ((button, statusName) in statusButtons) {
            button.setOnClickListener {
                for ((btn, _) in statusButtons) {
                    btn.setBackgroundColor(defaultBg)
                    btn.setTextColor(defaultText)
                }

                button.setBackgroundColor(selectedBg)
                button.setTextColor(selectedText)

                selectedStatus = statusName
            }
        }
    }
    private fun setupProgressindicator(){
        binding.addNewcar4.step1Icon.setImageResource(R.drawable.ic_check_blue)
        binding.addNewcar4.step2Icon.setImageResource(R.drawable.ic_check_blue)
        binding.addNewcar4.step3Icon.setImageResource(R.drawable.ic_check_blue)
        binding.addNewcar4.step4Icon.setImageResource(R.drawable.ic_number4_blue)
        binding.footerSave4.btnNext.text = "Publish"
    }
}
