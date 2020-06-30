package com.cansevin.walknoti.models.direction

import com.cansevin.walknoti.network.RequestResultListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DirectionsRepo {

    fun getDirections(type: String, directionRequest: DirectionsRequest, requestResultListener: RequestResultListener<Any>){
        DirectionsBaseRepo().getInstance()?.getDirectionsWithType(type,directionRequest)?.enqueue(object : Callback<DirectionsResponse>{
            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                requestResultListener.onFail()
            }

            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                if(response.isSuccessful){
                    response.body()?.let {
                        requestResultListener.onSuccess(it as Any)
                    }
                }
            }

        })
    }
}