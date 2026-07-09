package com.mydomain.countingdowngame.wildCards.api;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RiddleService {

    @GET("{category}")
    Call<RiddleResponse> getRiddleByCategory(@Path("category") String category);

    @GET("logic") // Fallback for testing connection
    Call<RiddleResponse> getRandomRiddle();

    class RiddleResponse {
        @SerializedName("riddle")
        public String riddle;

        @SerializedName("answer")
        public String answer;
    }
}
