package com.maverick.kmjshowroom.ui.setting.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Database.UserDatabaseHelper
import com.maverick.kmjshowroom.Model.UpdateProfileResponse
import com.maverick.kmjshowroom.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class EditProfileFragment : Fragment() {

    private lateinit var db: UserDatabaseHelper
    private var selectedImageFile: File? = null
    private lateinit var etFullName: EditText
    private lateinit var etNoTelp: EditText
    private lateinit var etAlamat: EditText
    private lateinit var profileImage: ImageView
    private lateinit var btnUpload: Button
    private val REQ_PICK_IMAGE = 1001
    private val REQ_CAMERA = 1002

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = UserDatabaseHelper(requireContext())

        etFullName = view.findViewById(R.id.etFullName)
        etNoTelp = view.findViewById(R.id.etNoTelp)
        etAlamat = view.findViewById(R.id.etAlamat)
        profileImage = view.findViewById(R.id.profileImage)
        btnUpload = view.findViewById(R.id.btnUploadPhoto)

        loadInitialData()

        btnUpload.setOnClickListener {
            showImagePicker()
        }

        val btnSave = view.findViewById<Button>(R.id.btn_next)
        btnSave.setOnClickListener {
            uploadProfile()
        }
    }

    private fun loadInitialData() {
        val user = db.getUser() ?: return

        etFullName.setText(user.full_name)
        etNoTelp.setText(user.no_telp)
        etAlamat.setText(user.alamat)

        if (!user.avatar_url.isNullOrEmpty()) {
            Glide.with(this)
                .load(ApiClient.BASE_URL + user.avatar_url)
                .placeholder(R.drawable.ic_person) // opsional
                .into(profileImage)
        }
    }

    // PICK IMAGE DARI CAMERA/GALLERY
    private fun showImagePicker() {
        val dialog = PopupMenu(requireContext(), btnUpload)
        dialog.menu.add("Pilih dari Galeri")
        dialog.menu.add("Gunakan Kamera")

        dialog.setOnMenuItemClickListener {
            when (it.title) {
                "Pilih dari Galeri" -> pickGallery()
                "Gunakan Kamera" -> openCamera()
            }
            true
        }
        dialog.show()
    }

    private fun pickGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQ_PICK_IMAGE)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQ_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            REQ_PICK_IMAGE -> {
                data?.data?.let { uri -> startCrop(uri) }
            }

            REQ_CAMERA -> {
                val bitmap = data?.extras?.get("data") as Bitmap
                val uri = saveTempBitmap(bitmap)
                startCrop(uri)
            }

            2001 -> {
                val extras = data?.extras
                val bitmap = extras?.getParcelable<Bitmap>("data")

                bitmap?.let {
                    val uri = saveTempBitmap(it)
                    selectedImageFile = uri.toFile()
                    profileImage.setImageBitmap(it)
                }
            }
        }
    }


    private fun saveTempBitmap(bitmap: Bitmap): Uri {
        val file = File(requireContext().cacheDir, "camera_${System.currentTimeMillis()}.jpg")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return Uri.fromFile(file)
    }

    private fun startCrop(uri: Uri) {
        try {
            val cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri, "image/*")

            cropIntent.putExtra("crop", "true")
            cropIntent.putExtra("aspectX", 1)
            cropIntent.putExtra("aspectY", 1)
            cropIntent.putExtra("scale", true)
            cropIntent.putExtra("outputX", 512)
            cropIntent.putExtra("outputY", 512)
            cropIntent.putExtra("return-data", true)

            startActivityForResult(cropIntent, 2001)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Crop bawaan HP tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }


    // UPLOAD PROFILE TO API
    private fun uploadProfile() {
        val user = db.getUser() ?: return

        val bodyMap = HashMap<String, RequestBody>()
        fun toRB(v: String) = RequestBody.create("text/plain".toMediaTypeOrNull(), v)

        bodyMap["kode_user"] = toRB(user.kode_user!!)
        bodyMap["full_name"] = toRB(etFullName.text.toString())
        bodyMap["no_telp"] = toRB(etNoTelp.text.toString())
        bodyMap["alamat"] = toRB(etAlamat.text.toString())

        var avatarPart: MultipartBody.Part? = null

        selectedImageFile?.let { file ->
            val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            avatarPart = MultipartBody.Part.createFormData("avatar_file", file.name, reqFile)
        }

        ApiClient.apiService.updateProfile(
            bodyMap["kode_user"]!!,
            bodyMap["full_name"],
            bodyMap["no_telp"],
            bodyMap["alamat"],
            avatarPart
        ).enqueue(object : Callback<UpdateProfileResponse> {

            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()

                    val body = response.body()

                    // UPDATE SQLITE
                    val newUser = user.copy(
                        full_name = etFullName.text.toString(),
                        no_telp = etNoTelp.text.toString(),
                        alamat = etAlamat.text.toString(),
                        avatar_url = body?.avatar_url ?: user.avatar_url
                    )

                    db.clearAndInsertUser(newUser)
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
