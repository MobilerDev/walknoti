package com.cansevin.walknoti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.cansevin.walknoti.models.AccessToken;
import com.cansevin.walknoti.models.NotificationMessage;
import com.cansevin.walknoti.models.PushResult;
import com.cansevin.walknoti.network.AccessInterface;
import com.cansevin.walknoti.network.NotiInterface;
import com.cansevin.walknoti.network.RetrofitClient;
import com.cansevin.walknoti.models.NotificationMessage.Builder;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hianalytics.hms.HiAnalyticsTools;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.ads.AdParam;
import com.huawei.hms.ads.BannerAdSize;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.ads.banner.BannerView;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
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
    String pushtoken,event;
    Button btn_oky;
    Integer delayQ,eventQ,delay = null;
    private BannerView bannerView;
    HiAnalyticsInstance instance;
    public static final String NOTI_URL = "https://push-api.cloud.huawei.com/v1/";
    public static final String ACCESS_TOKEN = "https://login.cloud.huawei.com/oauth2/";
    String accesstoken;
    String[] eventdata = new String[]{"Su","Yemek","Tuvalet","Hatırlatıcı"};
    String[] minutedata = new String[]{"15", "30", "45", "60","120","180"};
    NumberPicker minutepicker,eventpicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bannerView=findViewById(R.id.banner_view);
        bannerView.setAdId("testw6vs28auh3");
        bannerView.setBannerAdSize(BannerAdSize.BANNER_SIZE_320_50);
        AdParam adParam = new AdParam.Builder().build();
        bannerView.loadAd(adParam);

        HiAnalyticsTools.enableLog();
        HiAnalyticsInstance instance = HiAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("exam_difficulty","high");
        bundle.putString("exam_level","1-1");
        bundle.putString("exam_time","20190520-08");
        instance.onEvent("begin_examination", bundle);

        HwAds.init(this);
        keepStillBarrier = BehaviorBarrier.keeping(BehaviorBarrier.BEHAVIOR_STILL);
        eventpicker = findViewById(R.id.event_picker);
        minutepicker = findViewById(R.id.minute_picker);
        btn_oky = findViewById(R.id.btn_success);

        getActivity();
        minutepicker.setMinValue(0);
        eventpicker.setMinValue(0);
        minutepicker.setMaxValue(minutedata.length-1);
        eventpicker.setMaxValue(eventdata.length-1);
        minutepicker.setDisplayedValues(minutedata);
        eventpicker.setDisplayedValues(eventdata);
        btn_oky.setOnClickListener(v -> SetMin());
        GetBehavior();
    }

    private void SetMin() {
        delayQ = minutepicker.getValue();
        eventQ = eventpicker.getValue();
        delay = Integer.parseInt(minutedata[delayQ]) * 60000;
        event = eventdata[eventQ];
        final Handler handler = new Handler();
        handler.postDelayed(this::getToken, 1000);
    }

    private void getActivity() {
        Awareness.getCaptureClient(this).getBehavior()
                .addOnSuccessListener(behaviorResponse -> {
                    BehaviorStatus behaviorStatus = behaviorResponse.getBehaviorStatus();
                    DetectedBehavior mostLikelyBehavior = behaviorStatus.getMostLikelyBehavior();
                    String str = "Most likely behavior type is " + mostLikelyBehavior.getType() +
                            ",the confidence is " + mostLikelyBehavior.getConfidence();
                    Log.i("activity", str);
                })
                .addOnFailureListener(e -> Log.e("activityfail", "get behavior failed", e));
    }

    public void getAccessToken(){
        AccessInterface apiInterface = RetrofitClient.getClient(ACCESS_TOKEN).create(AccessInterface.class);
        Call<AccessToken> call = apiInterface.GetAccessToken("client_credentials",102178227,
                "4356f4efd87c29b08c040edf71b135f7c4325ec4103d08ac96dd715b230eced3");
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
        NotificationMessage notificationMessage = (new Builder(event,event + " ihtiyacın için seni yönlendireyim mi?",pushtoken,intent.toString(),eventQ)).build();
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
