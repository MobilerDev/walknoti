package com.huawei.nearestcinema.model

import com.cansevin.walknoti.models.direction.LatLngData
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Paths (@SerializedName("duration") val duration: Double,
                  @SerializedName("durationText") val durationText: String,
                  @SerializedName("durationInTraffic") val durationInTraffic: Int,
                  @SerializedName("distance") val distance: Double,
                  @SerializedName("startLocation") val startLocation: LatLngData,
                  @SerializedName("startAddress") val startAddress: String,
                  @SerializedName("distanceText") val distanceText: String,
                  @SerializedName("steps") val steps: List<Steps>,
                  @SerializedName("endLocation") val endLocation: LatLngData,
                  @SerializedName("endAddress") val endAddress: String) : Serializable