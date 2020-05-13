package com.cansevin.walknoti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cansevin.walknoti.models.AccessToken;
import com.cansevin.walknoti.models.NotificationMessage;
import com.cansevin.walknoti.models.PushResult;
import com.cansevin.walknoti.network.AccessInterface;
import com.cansevin.walknoti.network.NotiInterface;
import com.cansevin.walknoti.network.RetrofitClient;
import com.cansevin.walknoti.models.NotificationMessage.Builder;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.barrier.AwarenessBarrier;
import com.huawei.hms.kit.awareness.barrier.BehaviorBarrier;
import com.huawei.hms.kit.awareness.status.BehaviorStatus;
import com.huawei.hms.kit.awareness.status.DetectedBehavior;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    AwarenessBarrier keepStillBarrier;
    String pushtoken;
    public static final String NOTI_URL = "https://push-api.cloud.huawei.com/v1/";
    public static final String ACCESS_TOKEN = "https://login.cloud.huawei.com/oauth2/";
    String accesstoken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        keepStillBarrier = BehaviorBarrier.keeping(BehaviorBarrier.BEHAVIOR_STILL);
        GetBehavior();
        getToken();
    }

    public void getAccessToken(){
        AccessInterface apiInterface = RetrofitClient.getClient(ACCESS_TOKEN).create(AccessInterface.class);
        Call<AccessToken> call = apiInterface.GetAccessToken("client_credentials",102178227,"4356f4efd87c29b08c040edf71b135f7c4325ec4103d08ac96dd715b230eced3");
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                accesstoken = "Bearer "+response.body().getAccessToken();
                sendNotification(accesstoken);
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {

            }
        });
    }


    public void sendNotification(String accesstoken){
        NotiInterface notiInterface = RetrofitClient.getClient(NOTI_URL).create(NotiInterface.class);
        Intent intent = new Intent(this, MapsActivity.class);
        NotificationMessage notificationMessage = (new Builder("Title","Başarılı",pushtoken,intent.toString())).build();
        Call<PushResult> callNoti = notiInterface.sendNotification(accesstoken,notificationMessage);
        callNoti.enqueue(new Callback<PushResult>() {
            @Override
            public void onResponse(Call<PushResult> call, Response<PushResult> response) {
                Log.i("callnoti", response.body().getMsg());
            }

            @Override
            public void onFailure(Call<PushResult> call, Throwable t) {
                Log.i("callnoti",t.toString());
            }
        });
    }

    public void GetBehavior(){
        Awareness.getCaptureClient(this).getBehavior()
                .addOnSuccessListener(behaviorResponse -> {
                    BehaviorStatus behaviorStatus = behaviorResponse.getBehaviorStatus();
                    DetectedBehavior mostLikelyBehavior = behaviorStatus.getMostLikelyBehavior();
                    String str = "Most likely behavior type is " + mostLikelyBehavior.getType() +
                            ",the confidence is " + mostLikelyBehavior.getConfidence();
                    Log.i("str", str);
                })
                .addOnFailureListener(e -> Log.e("strfail", "get behavior failed", e));
    }

    private void getToken() {
        Log.i("token", "get token: begin");
        new Thread() {
            @Override
            public void run() {
                try {
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    pushtoken = HmsInstanceId.getInstance(MainActivity.this).getToken(appId, "HCM");
                    if(!TextUtils.isEmpty(pushtoken)) {
                        Log.i("token", "get token:" + pushtoken);
                        showLog(pushtoken);
                        getAccessToken();
                    }
                } catch (Exception e) {
                    Log.i("token","getToken failed, " + e);

                }
            }
        }.start();
    }

    private void showLog(final String log) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, pushtoken, Toast.LENGTH_SHORT).show());
    }

    private void deleteToken() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    HmsInstanceId.getInstance(MainActivity.this).deleteToken(appId, "HCM");
                    Log.i("delete", "deleteToken success.");
                } catch (ApiException e) {
                    Log.e("delete", "deleteToken failed." + e);
                }
            }
        }.start();
    }

}
