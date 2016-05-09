package com.oczeretko.dsbforsinket.fake;

import com.google.gson.annotations.*;

import java.util.*;

import retrofit2.*;
import retrofit2.http.*;

public interface FakeNotificationHubService {

    @GET("registrationsapi")
    Call<ArrayList<FakeRegistrationModel>> getRegistrations(@Query("tag") String tag);

    @POST("notificationsapi")
    Call<String> postNofication(@Body FakeNotificationModel notification);

    class FakeRegistrationModel {
        @SerializedName("Tags")
        private ArrayList<String> tags;

        public ArrayList<String> getTags() {
            return tags;
        }
    }

    class FakeNotificationModel {
        @SerializedName("Tag")
        private String tag;
        @SerializedName("Body")
        private String body;

        public FakeNotificationModel(String tag, String body) {
            this.tag = tag;
            this.body = body;
        }
    }
}
