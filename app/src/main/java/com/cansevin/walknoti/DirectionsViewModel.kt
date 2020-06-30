package com.cansevin.walknoti

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cansevin.walknoti.models.direction.DirectionsRepo
import com.cansevin.walknoti.models.direction.DirectionsRequest
import com.cansevin.walknoti.models.direction.DirectionsResponse
import com.cansevin.walknoti.network.RequestResultListener

class DirectionsViewModel : ViewModel() {

    var directionsResponse = MutableLiveData<DirectionsResponse>()

    fun directions(type: String ,directionRequest: DirectionsRequest){
        DirectionsRepo().getDirections(type,directionRequest,object :
                RequestResultListener<Any> {
            override fun onSuccess(any: Any) {
                if(any is DirectionsResponse){
                    directionsResponse.postValue(any)
                }
            }

            override fun onUnsuccess(any: Any) {

            }

            override fun onFail() {

            }

        })
    }
}