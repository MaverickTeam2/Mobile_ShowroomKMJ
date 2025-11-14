package com.maverick.kmjshowroom

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Database.UserDatabaseHelper
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserState()
        }, 2000)
    }

    private fun checkUserState() {
        CoroutineScope(Dispatchers.IO).launch {
            val hasUserInServer = checkUserInDatabase()
            val hasUserInSQLite = checkUserInSQLite()

            withContext(Dispatchers.Main) {
                when {
                    !hasUserInServer -> {
                        startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
                    }
                    hasUserInSQLite -> {
                        startActivity(Intent(this@MainActivity, loginSaveActivity::class.java))
                    }
                    else -> {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    }
                }
                finish()
            }
        }
    }

    private fun checkUserInSQLite(): Boolean {
        val dbHelper = UserDatabaseHelper(this)
        return dbHelper.getUserCount() > 0
    }

    private suspend fun checkUserInDatabase(): Boolean {
        return try {
            val response = ApiClient.apiService.checkUser()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.code == 200) {
                    (body.user_count ?: 0) > 0
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}
