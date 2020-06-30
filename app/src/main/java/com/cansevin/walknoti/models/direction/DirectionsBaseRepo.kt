package com.cansevin.walknoti.models.direction

import com.cansevin.walknoti.network.DirectionsApis

open class DirectionsBaseRepo {
    private var directionsApis : DirectionsApis? = null
    fun getInstance(): DirectionsApis?{
        if(directionsApis==null)
            setMainApis()
        return directionsApis
    }

    private fun setMainApis(){
        directionsApis = DirectionsRetrofit()
            .createService(DirectionsApis::class.java)
    }
}
