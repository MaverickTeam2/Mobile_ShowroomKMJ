package com.maverick.kmjshowroom.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class CarDao(context: Context) {

    private val dbHelper = CarDatabase(context)
    private val db = dbHelper.writableDatabase

    fun insertOrUpdate(car: CachedCar) {
        val values = ContentValues().apply {
            put("kode_mobil", car.kodeMobil)
            put("nama_mobil", car.namaMobil)
            put("tahun_mobil", car.tahunMobil)
            put("warna_exterior", car.warnaExterior)
            put("tipe_bahan_bakar", car.tipeBahanBakar)
            put("jarak_tempuh", car.jarakTempuh)
            put("angsuran", car.angsuran)
            put("tenor", car.tenor)
            put("dp", car.dp)
            put("status", car.status)
            put("foto_utama", car.fotoUtama)
        }

        db.insertWithOnConflict(
            "car_cache",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getAll(): List<CachedCar> {
        val list = mutableListOf<CachedCar>()
        val cursor = db.rawQuery("SELECT * FROM car_cache", null)

        if (cursor.moveToFirst()) {
            do {
                list.add(
                    CachedCar(
                        kodeMobil = cursor.getString(0),
                        namaMobil = cursor.getString(1),
                        tahunMobil = cursor.getString(2),
                        warnaExterior = cursor.getString(3),
                        tipeBahanBakar = cursor.getString(4),
                        jarakTempuh = cursor.getString(5),
                        angsuran = cursor.getString(6),
                        tenor = cursor.getString(7),
                        dp = cursor.getString(8),
                        status = cursor.getString(9),
                        fotoUtama = cursor.getString(10)
                    )
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        return list
    }

    fun clear() {
        db.delete("car_cache", null, null)
    }
}
