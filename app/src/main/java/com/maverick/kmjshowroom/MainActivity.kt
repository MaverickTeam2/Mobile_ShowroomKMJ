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
            val hasUserInSQLite = checkUserInSQLite()

            val hasUserInServer = withTimeoutOrNull(5000) {
                checkUserInServer()
            } ?: false

            val elapsed = System.currentTimeMillis() - startTime
            val remaining = splashMinDuration - elapsed

            if (remaining > 0) delay(remaining)

            withContext(Dispatchers.Main) {
                when {
                    hasUserInServer || hasUserInSQLite -> {
                        startActivity(Intent(this@MainActivity, loginSaveActivity::class.java))
                    }
                    else -> {
                        startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
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

    private suspend fun checkUserInServer(): Boolean {
        return try {
            val response = ApiClient.apiService.checkUser()
            if (response.isSuccessful) {
                val body = response.body()
                (body?.user_count ?: 0) > 0
            } else false
        } catch (e: Exception) {
            false
        }
    }
}
