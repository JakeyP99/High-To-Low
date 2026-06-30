package com.mydomain.countingdowngame.wildCards.api;

import android.text.Html;

import androidx.annotation.NonNull;

import com.mydomain.countingdowngame.wildCards.WildCardProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TriviaSessionManager {

    private static TriviaSessionManager instance;
    private final TriviaService triviaService;
    private final List<WildCardProperties> questionCache = Collections.synchronizedList(new ArrayList<>());
    private final Set<String> seenQuestions = Collections.synchronizedSet(new HashSet<>());
    private boolean isFetching = false;

    private TriviaSessionManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        triviaService = retrofit.create(TriviaService.class);
    }

    public static synchronized TriviaSessionManager getInstance() {
        if (instance == null) {
            instance = new TriviaSessionManager();
        }
        return instance;
    }

    public void preloadQuestions(int amount) {
        if (isFetching || questionCache.size() > 10) return;
        isFetching = true;

        triviaService.getQuestions(amount, "multiple").enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<TriviaService.TriviaResponse> call, @NonNull Response<TriviaService.TriviaResponse> response) {
                isFetching = false;
                if (response.isSuccessful() && response.body() != null) {
                    for (TriviaService.TriviaResult result : response.body().results) {
                        WildCardProperties property = convertToWildCard(result);
                        if (!seenQuestions.contains(property.getWildCard())) {
                            questionCache.add(property);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<TriviaService.TriviaResponse> call, @NonNull Throwable t) {
                isFetching = false;
            }
        });
    }

    public void testConnection(Callback<TriviaService.TriviaResponse> callback) {
        triviaService.getQuestions(1, "multiple").enqueue(callback);
    }

    public WildCardProperties getNextQuestion() {
        synchronized (questionCache) {
            if (questionCache.isEmpty()) {
                return null;
            }
            WildCardProperties question = questionCache.remove(0);
            seenQuestions.add(question.getWildCard());

            // Trigger background reload if cache is low
            if (questionCache.size() < 5) {
                preloadQuestions(10);
            }

            return question;
        }
    }

    private WildCardProperties convertToWildCard(TriviaService.TriviaResult result) {
        String question = Html.fromHtml(result.question, Html.FROM_HTML_MODE_LEGACY).toString();
        String answer = Html.fromHtml(result.correctAnswer, Html.FROM_HTML_MODE_LEGACY).toString();

        List<String> incorrect = new ArrayList<>();
        for (String s : result.incorrectAnswers) {
            incorrect.add(Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY).toString());
        }
        Collections.shuffle(incorrect);

        String w1 = !incorrect.isEmpty() ? incorrect.get(0) : "N/A";
        String w2 = incorrect.size() > 1 ? incorrect.get(1) : "N/A";
        String w3 = incorrect.size() > 2 ? incorrect.get(2) : "N/A";

        return new WildCardProperties(question, true, true, answer, w1, w2, w3, result.category);
    }
}
