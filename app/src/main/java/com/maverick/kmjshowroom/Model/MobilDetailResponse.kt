data class MobilDetailResponse(
    val code: Int,
    val mobil: MobilData,
    val foto: List<FotoData>,
    val fitur: List<FiturData>
)

data class MobilData(
    val kode_mobil: String,
    val kode_user: String,
    val nama_mobil: String,
    val tahun_mobil: Int,        // ✅ ubah dari String ke Int
    val jarak_tempuh: Int,       // ✅ ubah dari String ke Int
    val full_prize: Int,         // ✅ ubah dari String ke Int
    val uang_muka: Int,          // ✅ ubah dari String ke Int
    val tenor: Int,              // ✅ ubah dari String ke Int
    val angsuran: Int,           // ✅ ubah dari String ke Int
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

data class FiturData(
    val id: Int,
    val nama: String
)
