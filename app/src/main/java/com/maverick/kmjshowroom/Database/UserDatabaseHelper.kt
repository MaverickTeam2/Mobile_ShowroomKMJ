package com.maverick.kmjshowroom.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.maverick.kmjshowroom.Model.UserData

class UserDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "user.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE users (
                kode_user TEXT PRIMARY KEY,
                username TEXT,
                email TEXT,
                full_name TEXT,
                role TEXT,
                provider_type TEXT
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    fun insertUser(user: UserData) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("kode_user", user.kode_user ?: "")
            put("username", user.username ?: "")
            put("email", user.email ?: "")
            put("full_name", user.full_name ?: "")
            put("role", user.role ?: "")
            put("provider_type", user.provider_type ?: "local")
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
            put("role", user.role ?: "")
            put("provider_type", user.provider_type ?: "local")
        }
        db.insert("users", null, values)
        db.close()
    }

    fun getUser(): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users LIMIT 1", null)
        return if (cursor.moveToFirst()) {
            mapOf(
                "kode_user" to cursor.getString(cursor.getColumnIndexOrThrow("kode_user")),
                "username" to cursor.getString(cursor.getColumnIndexOrThrow("username")),
                "email" to cursor.getString(cursor.getColumnIndexOrThrow("email")),
                "full_name" to cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                "role" to cursor.getString(cursor.getColumnIndexOrThrow("role")),
                "provider_type" to cursor.getString(cursor.getColumnIndexOrThrow("provider_type"))
            )
        } else null
    }

    fun getUserCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM users", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }
}
