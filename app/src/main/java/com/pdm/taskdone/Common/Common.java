package com.pdm.taskdone.Common;

import com.pdm.taskdone.Remote.IGoogleAPI;
import com.pdm.taskdone.Remote.RetrofitClient;

public class Common {

        public static final String baseUrl = "https://maps.googleapis.com";
        public static IGoogleAPI getGoogleAPI()
        {
            return RetrofitClient.getClient(baseUrl).create(IGoogleAPI.class);

        }


}
