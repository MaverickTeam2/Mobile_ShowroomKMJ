package com.maverick.kmjshowroom.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CarDatabase(context: Context) :
    SQLiteOpenHelper(context, "car_cache.db", null, 2) {  // ⬅ version naik ke 2

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE car_cache (
                kode_mobil TEXT PRIMARY KEY,
                nama_mobil TEXT,
                tahun_mobil TEXT,
                warna_exterior TEXT,
                tipe_bahan_bakar TEXT,
                jarak_tempuh TEXT,
                angsuran TEXT,
                tenor TEXT,
                dp TEXT,
                status TEXT,
                foto_utama TEXT
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS car_cache")
        onCreate(db)
    }
}
