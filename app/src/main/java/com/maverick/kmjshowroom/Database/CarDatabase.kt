package com.maverick.kmjshowroom.Database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CarDatabase(context: Context) :
    SQLiteOpenHelper(context, "car_cache.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE car_cache (
                kode_mobil TEXT PRIMARY KEY,
                title TEXT,
                tahun TEXT,
                warna TEXT,
                status TEXT,
                jarak_tempuh TEXT,
                bahan_bakar TEXT,
                harga_angsuran TEXT,
                harga_dp TEXT,
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
