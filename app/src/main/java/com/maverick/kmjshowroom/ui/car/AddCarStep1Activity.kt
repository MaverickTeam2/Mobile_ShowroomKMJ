package com.maverick.kmjshowroom.ui.car

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.maverick.kmjshowroom.databinding.AddCarstep1Binding

class AddCarStep1Activity : AppCompatActivity() {

    private lateinit var binding: AddCarstep1Binding
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var currentTargetImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddCarstep1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        setupImagePicker()
        setupNextButton()
    }

    private fun setupHeader() {
        binding.layoutHeaderadd.iconClose.setOnClickListener {
            finish()
        }
    }

    private fun setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let { currentTargetImageView?.setImageURI(it) }
            }
        }

        val imageViews = listOf(
            binding.foto360, binding.fotoDepan, binding.fotoBelakang, binding.tampilanSamping,
            binding.addfoto1, binding.addfoto2, binding.addfoto3,
            binding.addfoto4, binding.addfoto5, binding.addfoto6
        )

        imageViews.forEach { imageView ->
            imageView.setOnClickListener {
                currentTargetImageView = imageView
                showImageSourceDialog()
            }
        }
    }

    private fun showImageSourceDialog() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun setupNextButton() {
        binding.footerSave1.btnNext.setOnClickListener {
            val intent = Intent(this, AddCarStep2Activity::class.java)
            startActivity(intent)
        }
    }
}