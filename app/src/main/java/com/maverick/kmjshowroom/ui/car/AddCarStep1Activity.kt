package com.maverick.kmjshowroom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class AddCarStep1Fragment : Fragment() {

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var currentTargetImageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Launcher untuk ambil hasil dari chooser
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val uri: Uri? = result.data?.data
                uri?.let {
                    currentTargetImageView?.setImageURI(it)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_carstep1, container, false)

        val closeButton: ImageView = view.findViewById(R.id.icon_close)
        closeButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val foto360 = view.findViewById<ImageView>(R.id.foto_360)
        val fotoDepan = view.findViewById<ImageView>(R.id.foto_depan)
        val fotoBelakang = view.findViewById<ImageView>(R.id.foto_belakang)
        val fotoSamping = view.findViewById<ImageView>(R.id.tampilan_samping)
        val addFoto1 = view.findViewById<ImageView>(R.id.addfoto_1)
        val addFoto2 = view.findViewById<ImageView>(R.id.addfoto_2)
        val addFoto3 = view.findViewById<ImageView>(R.id.addfoto_3)
        val addFoto4 = view.findViewById<ImageView>(R.id.addfoto_4)
        val addFoto5 = view.findViewById<ImageView>(R.id.addfoto_5)
        val addFoto6 = view.findViewById<ImageView>(R.id.addfoto_6)

        val allImageViews = listOf(
            foto360, fotoDepan, fotoBelakang, fotoSamping,
            addFoto1, addFoto2, addFoto3, addFoto4, addFoto5, addFoto6
        )

        allImageViews.forEach { imageView ->
            imageView.setOnClickListener {
                currentTargetImageView = imageView
                openImageChooser()
            }
        }

        return view
    }


    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        val chooser = Intent.createChooser(intent, "Pilih Gambar dengan")
        pickImageLauncher.launch(chooser)
    }
}
