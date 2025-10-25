package com.example.systemmonitor.data

import com.google.gson.annotations.SerializedName

data class DeviceData(
    @SerializedName("id") val id: Int,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("data_type") val dataType: String,
    @SerializedName("data_value") val dataValue: String
)

data class ApiResponse(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: List<DeviceData>
)