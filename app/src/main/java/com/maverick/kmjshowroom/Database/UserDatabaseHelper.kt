package com.maverick.kmjshowroom.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.maverick.kmjshowroom.Model.UserData

class UserDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "user.db", null, 4) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE users (
                kode_user TEXT PRIMARY KEY,
                username TEXT,
                email TEXT,
                full_name TEXT,
                no_telp TEXT,
                alamat TEXT,
                role TEXT,
                avatar_url TEXT,
                provider_type TEXT,
                status INTEGER DEFAULT 1
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Tambahkan kolom status jika versi lama < 2
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE users ADD COLUMN status INTEGER DEFAULT 1")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE users ADD COLUMN alamat TEXT")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun insertUser(user: UserData) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("kode_user", user.kode_user ?: "")
            put("username", user.username ?: "")
            put("email", user.email ?: "")
            put("full_name", user.full_name ?: "")
            put("no_telp", user.no_telp ?: "")
            put("alamat", user.alamat ?: "")
            put("role", user.role ?: "")
            put("avatar_url", user.avatar_url ?: "")
            put("provider_type", user.provider_type ?: "local")
            put("status", user.status ?: 1) // default 1 jika null
        }
        db.insert("users", null, values)
        db.close()
    }

    fun clearAndInsertUser(user: UserData) {
        val db = writableDatabase
        db.delete("users", null, null)

        val values = ContentValues().apply {
            put("kode_user", user.kode_user ?: "")
            put("username", user.username ?: "")
            put("email", user.email ?: "")
            put("full_name", user.full_name ?: "")
            put("no_telp", user.no_telp ?: "")
            put("alamat", user.alamat ?: "")
            put("role", user.role ?: "")
            put("avatar_url", user.avatar_url ?: "")
            put("provider_type", user.provider_type ?: "local")
            put("status", user.status ?: 1)
        }
        db.insert("users", null, values)
        db.close()
    }

    fun getUser(): UserData? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users LIMIT 1", null)
        val user = if (cursor.moveToFirst()) {
            UserData(
                kode_user = cursor.getString(cursor.getColumnIndexOrThrow("kode_user")),
                username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                full_name = cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                no_telp = cursor.getString(cursor.getColumnIndexOrThrow("no_telp")),
                alamat = cursor.getString(cursor.getColumnIndexOrThrow("alamat")),
                role = cursor.getString(cursor.getColumnIndexOrThrow("role")),
                avatar_url = cursor.getString(cursor.getColumnIndexOrThrow("avatar_url")),
                provider_type = cursor.getString(cursor.getColumnIndexOrThrow("provider_type")),
                status = cursor.getInt(cursor.getColumnIndexOrThrow("status")) // sekarang aman
            )
        } else null

        cursor.close()
        db.close()
        return user
    }

    fun getUserCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM users", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        db.close()
        return count
    }
}
