package com.huawei.nearestcinema.model

import com.cansevin.walknoti.models.direction.Bounds
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Routes (@SerializedName("paths") val paths: List<Paths>,
                   @SerializedName("bounds") val bounds: Bounds) : Serializable