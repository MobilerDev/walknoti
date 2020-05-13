package com.cansevin.walknoti.network;

import com.cansevin.walknoti.models.NotificationMessage;
import com.cansevin.walknoti.models.PushResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotiInterface {
    @Headers("Content-Type:application/json")
    @POST("102178227/messages:send")
    Call<PushResult> sendNotification(
            @Header("Authorization") String authorization,  // Bearer $accessToken
            @Body NotificationMessage notificationMessage);
}
