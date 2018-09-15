package com.pdm.taskdone.Service;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.pdm.taskdone.Client_Request_Popup;

public class MyFirebaseMessaging extends FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

       //this will send Firebase message with contain lat and lng from rider app;
        LatLng client_location = new Gson().fromJson(remoteMessage.getNotification().getBody(),LatLng.class);

        Intent intent = new Intent(getBaseContext(), Client_Request_Popup.class);
        intent.putExtra("lat",client_location.latitude);
        intent.putExtra("lng",client_location.longitude);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);



    }
}
