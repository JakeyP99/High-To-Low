package com.mydomain.countingdowngame.wildCards.api;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RiddleService {

    @GET("random")
    Call<RiddleResponse> getRandomRiddle();

    class RiddleResponse {
        @SerializedName("riddle")
        public String riddle;

        @SerializedName("answer")
        public String answer;
    }
}
