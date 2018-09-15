package com.pdm.taskdone.Remote;

import com.pdm.taskdone.Model.FCMResponse;
import com.pdm.taskdone.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {


    @Headers({

            "Content-Type:application/json",
            "Authorization:key=AAAArAIvqkI:APA91bGsEeVjuUAm20G5jyOlhZNAfxSDipSJCuaEfF11Ms2Hht_YZ4iwa5sh1V_IwAxbDPlgVPuhu-efIFd9vpKjhV4a61m7TdG6svTuL2r6aZOnnaS7DJ_Af5Jb84kVNAtAPAm3i-QXy1yDOTqFlPvyyXkKAnoMFw"

    })
    @POST("fcm/send")
    Call<IFCMService> sendMessage (@Body Sender body);



}
