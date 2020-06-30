package com.cansevin.walknoti.models.direction

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Bounds (@SerializedName("southwest") val southwest: LatLngData,
                   @SerializedName("northeast") val northeast: LatLngData) : Serializable