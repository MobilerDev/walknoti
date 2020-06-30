package com.cansevin.walknoti.models.direction

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DirectionsRequest(@SerializedName("origin") val origin: LatLngData,
                             @SerializedName("destination") val destination: LatLngData): Serializable