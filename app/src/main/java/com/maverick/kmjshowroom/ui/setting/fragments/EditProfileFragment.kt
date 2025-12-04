package com.maverick.kmjshowroom.ui.setting.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Database.UserDatabaseHelper
import com.maverick.kmjshowroom.Model.UpdateProfileResponse
import com.maverick.kmjshowroom.R
import com.yalantis.ucrop.UCrop
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
    private lateinit var btnNext: Button
    private var cameraImageUri: Uri? = null
    private lateinit var etPasswordLama: EditText
    private lateinit var etPasswordBaru: EditText
    private lateinit var etKonfirmasiPassword: EditText


    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCamera()
            else Toast.makeText(requireContext(), "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
        }

    // Ucrop result launcher
    private val cropLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val uri = UCrop.getOutput(result.data!!)
            uri?.let {
                val input = requireContext().contentResolver.openInputStream(it)
                val tempFile = File(requireContext().cacheDir, "upload_${System.currentTimeMillis()}.jpg")
                tempFile.outputStream().use { out ->
                    input?.copyTo(out)
                }

                selectedImageFile = tempFile
                profileImage.setImageURI(it)
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val error = UCrop.getError(result.data!!)
            Toast.makeText(requireContext(), "Crop error: ${error?.message}", Toast.LENGTH_LONG).show()
        }
    }

    //galery launcher
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                requireContext().contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                startCrop(it)
            }
        }

    //camera launcher
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                cameraImageUri?.let { startCrop(it) }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_setting_edit_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = UserDatabaseHelper(requireContext())

        etFullName = view.findViewById(R.id.etFullName)
        etNoTelp = view.findViewById(R.id.etNoTelp)
        etAlamat = view.findViewById(R.id.etAlamat)
        profileImage = view.findViewById(R.id.profileImage)
        btnUpload = view.findViewById(R.id.btnUploadPhoto)
        btnNext = view.findViewById(R.id.btn_next)
        etPasswordLama = view.findViewById(R.id.etPasswordLama)
        etPasswordBaru = view.findViewById(R.id.etPasswordBaru)
        etKonfirmasiPassword = view.findViewById(R.id.etKonfirmasiPassword)


        loadInitialData()

        btnUpload.setOnClickListener { showImagePicker() }
        btnNext.setOnClickListener { validateAndUpload() }
    }

    private fun loadInitialData() {
        val user = db.getUser() ?: return

        etFullName.setText(user.full_name)
        etNoTelp.setText(user.no_telp)
        etAlamat.setText(user.alamat)

        if (!user.avatar_url.isNullOrEmpty()) {
            Glide.with(this)
                .load(ApiClient.BASE_URL + user.avatar_url)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(profileImage)
        }
    }

    // image picker
    private fun showImagePicker() {
        val menu = PopupMenu(requireContext(), btnUpload)
        menu.menu.add("Pilih dari Galeri")
        menu.menu.add("Gunakan Kamera")

        menu.setOnMenuItemClickListener {
            when (it.title) {
                "Pilih dari Galeri" -> pickGallery()
                "Gunakan Kamera" -> checkCameraPermission()
            }
            true
        }
        menu.show()
    }

    private fun pickGallery() {
        galleryLauncher.launch(arrayOf("image/*"))
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Toast.makeText(
                    requireContext(),
                    "Izin kamera diperlukan untuk mengambil foto",
                    Toast.LENGTH_LONG
                ).show()
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val photoFile = File(
            requireContext().getExternalFilesDir("Pictures"),
            "camera_${System.currentTimeMillis()}.jpg"
        )

        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().packageName + ".fileprovider",
            photoFile
        )

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        cameraLauncher.launch(intent)
    }

    // Ucrop start
    private fun startCrop(uri: Uri) {
        val destination = Uri.fromFile(
            File(requireContext().cacheDir, "crop_${System.currentTimeMillis()}.jpg")
        )

        val options = UCrop.Options().apply {
            setCompressionQuality(90)
            setFreeStyleCropEnabled(false)
        }

        val cropIntent = UCrop.of(uri, destination)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(800, 800)
            .withOptions(options)
            .getIntent(requireContext())

        cropLauncher.launch(cropIntent)
    }

    private fun saveTempBitmap(bitmap: Bitmap): Uri {
        val file = File(requireContext().cacheDir, "camera_${System.currentTimeMillis()}.jpg")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return Uri.fromFile(file)
    }

    //validasi dan update profile
    private fun validateAndUpload() {
        val fullName = etFullName.text.toString().trim()
        val noTelp = etNoTelp.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()

        val oldPassword = view?.findViewById<EditText>(R.id.etPasswordLama)?.text.toString().trim()
        val newPassword = view?.findViewById<EditText>(R.id.etPasswordBaru)?.text.toString().trim()
        val confirmPassword = view?.findViewById<EditText>(R.id.etKonfirmasiPassword)?.text.toString().trim()

        val user = db.getUser()

        //validasi data user
        when {
            fullName.isEmpty() -> {
                etFullName.error = "Nama lengkap tidak boleh kosong"
                etFullName.requestFocus()
                return
            }
            noTelp.isEmpty() -> {
                etNoTelp.error = "Nomor telepon tidak boleh kosong"
                etNoTelp.requestFocus()
                return
            }
            alamat.isEmpty() -> {
                etAlamat.error = "Alamat tidak boleh kosong"
                etAlamat.requestFocus()
                return
            }
        }
        //validasi password
        if (oldPassword.isEmpty() && newPassword.isEmpty() && confirmPassword.isEmpty()) {
            uploadProfile()
            return
        }

        // password baru diisi, password lama kosong / eror
        if ((!newPassword.isEmpty() || !confirmPassword.isEmpty()) && oldPassword.isEmpty()) {
            etPasswordLama.error = "Masukkan password lama"
            etPasswordLama.requestFocus()
            return
        }

        // password lama terisi, password baru kosong
        if (!oldPassword.isEmpty() && (newPassword.isEmpty() || confirmPassword.isEmpty())) {
            etPasswordBaru.error = "Masukkan password baru"
            etPasswordBaru.requestFocus()
            return
        }

        // validasi konfirmasi password
        if (newPassword != confirmPassword) {
            etKonfirmasiPassword.error = "Konfirmasi password tidak cocok"
            etKonfirmasiPassword.requestFocus()
            return
        }
        uploadProfile()

    }


    private fun uploadProfile() {
        val user = db.getUser() ?: return

        // Show loading
        setLoading(true)

        val bodyMap = HashMap<String, RequestBody>()

        fun createRB(value: String) =
            RequestBody.create("text/plain".toMediaTypeOrNull(), value)

        bodyMap["kode_user"] = createRB(user.kode_user!!)
        bodyMap["full_name"] = createRB(etFullName.text.toString().trim())
        bodyMap["no_telp"] = createRB(etNoTelp.text.toString().trim())
        bodyMap["alamat"] = createRB(etAlamat.text.toString().trim())

        bodyMap["password_lama"] = createRB(etPasswordLama.text.toString().trim())
        bodyMap["password_baru"] = createRB(etPasswordBaru.text.toString().trim())
        bodyMap["konfirmasi_password"] = createRB(etKonfirmasiPassword.text.toString().trim())

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
            bodyMap["password_lama"],
            bodyMap["password_baru"],
            bodyMap["konfirmasi_password"],
            avatarPart
        ).enqueue(object : Callback<UpdateProfileResponse> {

            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                setLoading(false)

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null && body.code == 200) {
                        Toast.makeText(
                            requireContext(),
                            body.message ?: "Profil berhasil diperbarui",
                            Toast.LENGTH_SHORT
                        ).show()

                        val updatedUser = if (body.user != null) {
                            body.user
                        } else {
                            user.copy(
                                full_name = etFullName.text.toString().trim(),
                                no_telp = etNoTelp.text.toString().trim(),
                                alamat = etAlamat.text.toString().trim(),
                                avatar_url = body.avatar_url ?: user.avatar_url
                            )
                        }

                        db.clearAndInsertUser(updatedUser)

                        // Kembali ke halaman sebelumnya
                        requireActivity().onBackPressed()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            body?.message ?: "Gagal memperbarui profil",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // Handle error response
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(
                    requireContext(),
                    "Koneksi gagal: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun handleErrorResponse(response: Response<UpdateProfileResponse>) {
        try {
            val errorBody = response.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, UpdateProfileResponse::class.java)

            val message = when (response.code()) {
                400 -> errorResponse?.message ?: "Data tidak valid"
                401 -> "Password lama salah"
                403 -> "Anda tidak memiliki izin untuk mengubah profil"
                404 -> "Data pengguna tidak ditemukan"
                413 -> "Ukuran file terlalu besar"
                500 -> errorResponse?.message ?: "Terjadi kesalahan pada server"
                else -> errorResponse?.message ?: "Terjadi kesalahan (${response.code()})"
            }

            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Terjadi kesalahan (${response.code()})",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        btnNext.isEnabled = !isLoading
        btnUpload.isEnabled = !isLoading
        etFullName.isEnabled = !isLoading
        etNoTelp.isEnabled = !isLoading
        etAlamat.isEnabled = !isLoading

        btnNext.text = if (isLoading) "Menyimpan..." else "Simpan"
    }
}
