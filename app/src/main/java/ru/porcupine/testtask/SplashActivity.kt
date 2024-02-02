package ru.porcupine.testtask

import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import ru.porcupine.testtask.web.WebViewActiv
import java.net.Inet4Address
import java.net.NetworkInterface


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        val ipAddress = getPublicIpAddress()
        val apiUrl = "http://ip-api.com/json/?fields=status,message,countryCode"

        lifecycleScope.launch {
            val country = getCountry(apiUrl)
            delay(3000)
            if (country == "RU") {
                startActivity(Intent(this@SplashActivity, WebViewActiv::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private suspend fun getCountry(apiUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(apiUrl).header("Content-Type", "application/json").build()

            val response = client.newCall(request).execute()
            return@withContext if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val countryCode = responseBody?.let { JSONObject(it).getString("countryCode") }
                println("Country Code: $countryCode")
                countryCode
            } else {
                println("Error: ${response.code}")
                "Unknown"
            }
        } catch (e: Exception) {
            println("Error2: $e")
            return@withContext "Unknown"
        }
    }
}