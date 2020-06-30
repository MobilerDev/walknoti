package com.cansevin.walknoti.network

interface RequestResultListener<any : Any> {
    fun onSuccess(any : Any)
    fun onUnsuccess(any : Any)
    fun onFail()
}