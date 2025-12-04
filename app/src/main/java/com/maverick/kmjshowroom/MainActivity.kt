package com.maverick.kmjshowroom

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maverick.kmjshowroom.API.ApiClient
import com.maverick.kmjshowroom.Database.UserDatabaseHelper
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val splashMinDuration = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkUserFlow()
    }

    private fun checkUserFlow() {
        val startTime = System.currentTimeMillis()

        CoroutineScope(Dispatchers.IO).launch {

            val sqliteCount = getSQLiteCount()
            val serverCount = getServerUserCount()

            val elapsed = System.currentTimeMillis() - startTime
            val remaining = splashMinDuration - elapsed
            if (remaining > 0) delay(remaining)

            withContext(Dispatchers.Main) {
                when {
                    serverCount == 0 -> {
                        startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
                    }

                    sqliteCount == 0 && serverCount > 1 -> {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    }

                    // 3. SQLite ada user (exact 1) → Login Save
                    else -> {
                        startActivity(Intent(this@MainActivity, loginSaveActivity::class.java))
                    }
                }
                finish()
            }
        }
    }

    private fun getSQLiteCount(): Int {
        val db = UserDatabaseHelper(this)
        return db.getUserCount()
    }

    private suspend fun getServerUserCount(): Int {
        return try {
            val response = ApiClient.apiService.checkUser()
            if (response.isSuccessful) {
                response.body()?.user_count ?: 0
            } else 0
        } catch (e: Exception) {
            0
        }
    }

}
