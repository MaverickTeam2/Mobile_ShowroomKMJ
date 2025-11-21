data class MobilDetailResponse(
    val code: Int,
    val success: Boolean,
    val mobil: MobilData,
    val foto: List<FotoData>,
    val fitur: List<Int>
)

data class MobilData(
    val kode_mobil: String,
    val kode_user: String,
    val nama_mobil: String,
    val tahun_mobil: String,
    val jarak_tempuh: String,
    val full_prize: String,
    val uang_muka: String,
    val tenor: String,
    val angsuran: String,
    val jenis_kendaraan: String,
    val sistem_penggerak: String,
    val tipe_bahan_bakar: String,
    val warna_interior: String,
    val warna_exterior: String,
    val status: String,
    val created_at: String
)

data class FotoData(
    val id_foto: String,
    val tipe_foto: String,
    val foto: String
)
