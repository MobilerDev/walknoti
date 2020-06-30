package com.cansevin.walknoti.network

import com.cansevin.walknoti.models.direction.DirectionsRequest
import com.cansevin.walknoti.models.direction.DirectionsResponse
import retrofit2.Call
import retrofit2.http.*


interface DirectionsApis {
    @POST("routeService/{type}")
    fun getDirectionsWithType(
        @Path(value = "type",encoded = true) type : String
        ,@Body directionRequest: DirectionsRequest): Call<DirectionsResponse>
}