package com.maverick.kmjshowroom.ui.setting.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.utils.HeaderHelper
import com.maverick.kmjshowroom.utils.LoadingDialog
import kotlinx.coroutines.launch

class ContactSocialFragment : Fragment() {

    private var idContact: String? = null
    private lateinit var etWhatsapp: EditText
    private lateinit var etInstagram: EditText
    private lateinit var etFacebook: EditText
    private lateinit var etTiktok: EditText
    private lateinit var etYoutube: EditText
    private lateinit var btnSave: Button

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        HeaderHelper.setupBackButton(requireActivity(), view.findViewById(R.id.header_include))

        val headerView = view.findViewById<View>(R.id.header_include)
        val tvHeader: TextView = headerView.findViewById(R.id.text_header)
        tvHeader.text = "URL Showroom Setting"

        etWhatsapp = view.findViewById(R.id.etWhatsapp)
        etInstagram = view.findViewById(R.id.etInstagram)
        etFacebook = view.findViewById(R.id.etFacebook)
        etTiktok = view.findViewById(R.id.etTiktok)
        etYoutube = view.findViewById(R.id.etYoutube)
        val footerView = view.findViewById<View>(R.id.save)
        btnSave = footerView.findViewById(R.id.btn_next)

        loadContacts()

        btnSave.setOnClickListener {
            saveContact()
        }
    }

    private fun loadContacts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getContacts()

                if (response.isSuccessful && response.body()?.code == 200) {
                    response.body()?.data?.firstOrNull()?.let { item ->
                        idContact = item.id_contact
                        etWhatsapp.setText(item.whatsapp ?: "")
                        etInstagram.setText(item.instagram_url ?: "")
                        etFacebook.setText(item.facebook_url ?: "")
                        etTiktok.setText(item.tiktok_url ?: "")
                        etYoutube.setText(item.youtube_url ?: "")
                    }
                } else {
                    showToast("Gagal mengambil data")
                }

            } catch (e: Exception) {
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun validateFields(): Boolean {
        val wa = etWhatsapp.text.toString().trim()
        if (wa.isEmpty() || wa.length < 8) {
            etWhatsapp.error = "Nomor WhatsApp tidak valid"
            return false
        }

        val instagram = etInstagram.text.toString().trim()
        if (instagram.isNotEmpty() && !instagram.contains("instagram.com")) {
            etInstagram.error = "URL Instagram tidak valid"
            return false
        }

        val fb = etFacebook.text.toString().trim()
        if (fb.isNotEmpty() && !fb.contains("facebook.com")) {
            etFacebook.error = "URL Facebook tidak valid"
            return false
        }

        val tt = etTiktok.text.toString().trim()
        if (tt.isNotEmpty() && !tt.contains("tiktok.com")) {
            etTiktok.error = "URL TikTok tidak valid"
            return false
        }

        val yt = etYoutube.text.toString().trim()
        if (yt.isNotEmpty() && !yt.contains("youtube.com") && !yt.contains("youtu.be")) {
            etYoutube.error = "URL YouTube tidak valid"
            return false
        }

        return true
    }

    private fun saveContact() {

        if (!validateFields()) return

        loadingDialog.show("Menyimpan data...")

        val body = mapOf(
            "id_contact" to idContact,
            "whatsapp" to etWhatsapp.text.toString().trim(),
            "instagram_url" to etInstagram.text.toString().trim(),
            "facebook_url" to etFacebook.text.toString().trim(),
            "tiktok_url" to etTiktok.text.toString().trim(),
            "youtube_url" to etYoutube.text.toString().trim()
        )

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = if (idContact == null)
                    ApiClient.apiService.createContact(body)
                else
                    ApiClient.apiService.updateContact(body)

                loadingDialog.dismiss()

                if (response.isSuccessful) {
                    showToast(response.body()?.message ?: "Success")

                    if (idContact == null) loadContacts()

                } else {
                    showToast("Gagal menyimpan data")
                }

            } catch (e: Exception) {
                loadingDialog.dismiss()
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}
