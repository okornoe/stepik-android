package org.stepik.android.remote.analytic.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class AnalyticBatchEvent(
    @SerializedName("name")
    val name: String,
    @SerializedName("timestamp")
    val timeStamp: Double,
    @SerializedName("data")
    val data: JsonElement?
)