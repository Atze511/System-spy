package com.example.systemserviceupdate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Starte den Hintergrunddienst
        val serviceIntent = Intent(this, BackgroundService::class.java)
        startService(serviceIntent)
        
        // Zeige kurze Bestätigung
        Toast.makeText(this, "System aktualisiert", Toast.LENGTH_SHORT).show()
        
        // Beende die Aktivität sofort
        finish()
    }
}