package com.huawei.nearestcinema.model

import com.cansevin.walknoti.models.direction.LatLngData
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Steps (@SerializedName("duration") val duration: Double,
                  @SerializedName("orientation") val orientation: Double,
                  @SerializedName("durationText") val durationText: String,
                  @SerializedName("distance") val distance: Double,
                  @SerializedName("startLocation") val startLocation: LatLngData,
                  @SerializedName("instruction") val instruction: String,
                  @SerializedName("action") val action: String,
                  @SerializedName("distanceText") val distanceText: String,
                  @SerializedName("endLocation") val endLocation: LatLngData,
                  @SerializedName("polyline") val polyline: List<LatLngData>,
                  @SerializedName("roadName") val roadName: String) : Serializable