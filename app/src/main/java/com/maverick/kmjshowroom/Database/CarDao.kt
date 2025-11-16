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
            put("title", car.title)
            put("tahun", car.tahun)
            put("warna", car.warna)
            put("status", car.status)
            put("jarak_tempuh", car.jarakTempuh)
            put("bahan_bakar", car.bahanBakar)
            put("harga_angsuran", car.hargaAngsuran)
            put("harga_dp", car.hargaDp)
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
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9)
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
