package com.cansevin.walknoti.models.direction

import com.google.gson.annotations.SerializedName
import com.huawei.nearestcinema.model.Routes
import java.io.Serializable

data class DirectionsResponse (@SerializedName("routes") val routes: List<Routes>,
                               @SerializedName("returnCode") val returnCode: String,
                               @SerializedName("returnDesc") val returnDesc: String) : Serializable