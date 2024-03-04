package com.yeel.drc.fcm;
import static com.yeel.drc.utils.SthiramValues.isNotificationReceivedToRefresh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yeel.drc.activity.WelcomeActivity;
import com.yeel.drc.activity.login.ReturningUserActivity;
import com.yeel.drc.activity.main.HomeActivity;
import com.yeel.drc.utils.CommonFunctions;
import com.yeel.drc.utils.SthiramValues;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;
    SharedPreferences sharedPreferences;
    Intent resultIntent;
    int pushID=0;
    String notification,sound, time;
    CommonFunctions commonFunctions;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sharedPreferences = getApplicationContext().getSharedPreferences("yeelNotification", MODE_PRIVATE);
        notification="on";
        sound="on";
        time="on";
        commonFunctions=new CommonFunctions(getApplicationContext());
        pushID=sharedPreferences.getInt("pushID", 0);
        if (remoteMessage == null) {
            return;
        }
        if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData().size() > 0) {
            try{
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                JSONObject data = json.getJSONObject("data");
                String title = data.getString("title");
                String message = data.getString("message");
                String type = data.getString("type");
                String transationId = data.getString("transationId");
                String kycStatus=data.getString("kyc_status");
                String pushReceiverId=data.getString("recieverId");
                handleDataMessage(title,message,type,transationId,kycStatus,pushReceiverId);

                Log.e("Notification",json.toString());
            }catch(Exception e){
                commonFunctions.logAValue("FCm Error",e+"");
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            Intent pushNotification = new Intent("pushNotification");
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
        }
    }

    private void handleDataMessage(String title,String message,String type,String transationId,String kycStatus,String pushReceiverId) {
        try {
            pushID++;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("pushID", pushID);
            editor.commit();

            if(commonFunctions.getUserType()!=null){
                if(commonFunctions.getUserType().equals(SthiramValues.ACCOUNT_TYPE_AGENT)&&type.equals("AgentIn-cashpickup")){
                    commonFunctions.setAgentNotificationStatus(true);
                }
            }


            Intent in = new Intent("notification");
            LocalBroadcastManager.getInstance(this).sendBroadcast(in);
            isNotificationReceivedToRefresh = true;

        /*    if(NotificationUtils.isAppIsInBackground(getApplicationContext())){
                Log.e("App Status","background");
                if(commonFunctions.getLoginStatus()){
                    if(commonFunctions.isSequrityEnabled()){
                        resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    }else{
                        resultIntent = new Intent(getApplicationContext(), ReturningUserActivity.class);
                    }
                }else {
                    resultIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
                }
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }else{*/

                if(commonFunctions.getLoginStatus()){
                    if(!commonFunctions.getLogoutStatus()){
                        //home
                        if(pushReceiverId.equals(commonFunctions.getUserId())){
                            if(type.equalsIgnoreCase("kyc")){
                                Log.e("Type","KYC");
                                Log.e("kycStatus",kycStatus);
                                if(kycStatus.equals(SthiramValues.KYC_STATUS_REJECTED) ||
                                        kycStatus.equals(SthiramValues.KYC_STATUS_EXPIRED) ||
                                        kycStatus.equals(SthiramValues.KYC_STATUS_RE_SUBMIT)){
                                    resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                                    resultIntent.putExtra("isFromNotification",true);
                                }else if(kycStatus.equals(SthiramValues.KYC_STATUS_DEACTIVATED)){
                                    //account blocked
                                    resultIntent = new Intent(getApplicationContext(), ReturningUserActivity.class);
                                }else{
                                    resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                                }
                            }else{
                                resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                            }
                        }else{
                            resultIntent = new Intent(getApplicationContext(), ReturningUserActivity.class);
                        }
                    }else{
                        resultIntent = new Intent(getApplicationContext(), ReturningUserActivity.class);
                        if(type.equalsIgnoreCase("kyc")){
                            resultIntent.putExtra("from-kyc","kyc");
                            resultIntent.putExtra("receiver-push-id",pushReceiverId);
                        }
                    }
                }else {
                    resultIntent = new Intent(getApplicationContext(), WelcomeActivity.class);
                }
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if (notification.equals("on")) {
                showNotificationMessage(getApplicationContext(), title, message, resultIntent, pushID);
            }
        } catch (Exception e) {

        }
    }

    private void showNotificationMessage(Context context, String title, String message,Intent intent,int pushID) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message,intent,pushID,sound);
    }

}
