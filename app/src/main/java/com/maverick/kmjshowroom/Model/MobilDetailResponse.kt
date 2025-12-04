import java.math.BigInteger

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
    val tahun_mobil: Int,
    val jarak_tempuh: Int,
    val full_prize: BigInteger,
    val uang_muka: Int,
    val tenor: Int,
    val angsuran: Int,
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
