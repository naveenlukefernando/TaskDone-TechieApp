package com.pdm.taskdone.Common;

import android.location.Location;

import com.pdm.taskdone.Model.User_worker;
import com.pdm.taskdone.Remote.FCMClient;
import com.pdm.taskdone.Remote.IFCMService;
import com.pdm.taskdone.Remote.IGoogleAPI;
import com.pdm.taskdone.Remote.RetrofitClient;

public class Common {


    public static String currentToken = "";


    public static final String user_client = "user_client";
    public static final String user_worker = "user_worker";
    public static final String client_location_GPS_Request = "client_location_GPS_Request";
    public static final String worker_location_GPS = "worker_location_GPS";
    public static final String token_table = "token";

    public static User_worker currentUser;

    public static Location mLastlocation = null;


        public static final String baseUrl = "https://maps.googleapis.com";
        public static final String fcmURL = "https://fcm.googleapis.com";

        public static IGoogleAPI getGoogleAPI()
        {
            return RetrofitClient.getClient(baseUrl).create(IGoogleAPI.class);

        }


    public static IFCMService getIFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);

    }



}
