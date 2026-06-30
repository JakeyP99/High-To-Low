package com.mydomain.countingdowngame.wildCards.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TriviaService {

    @GET("api.php")
    Call<TriviaResponse> getQuestions(
            @Query("amount") int amount,
            @Query("type") String type
    );

    class TriviaResponse {
        @SerializedName("results")
        public List<TriviaResult> results;
    }

    class TriviaResult {
        @SerializedName("category")
        public String category;
        @SerializedName("question")
        public String question;
        @SerializedName("correct_answer")
        public String correctAnswer;
        @SerializedName("incorrect_answers")
        public List<String> incorrectAnswers;
    }
}
