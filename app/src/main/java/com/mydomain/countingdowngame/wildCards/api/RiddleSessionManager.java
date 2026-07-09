package com.mydomain.countingdowngame.wildCards.api;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RiddleSessionManager {

    private static RiddleSessionManager instance;
    private final RiddleService riddleService;
    private final List<String[]> riddleCache = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger activeFetches = new AtomicInteger(0);

    private RiddleSessionManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://riddles-api.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        riddleService = retrofit.create(RiddleService.class);
    }

    public static synchronized RiddleSessionManager getInstance() {
        if (instance == null) {
            instance = new RiddleSessionManager();
        }
        return instance;
    }

    public void preloadRiddles(int amount) {
        if (activeFetches.get() > 0 || riddleCache.size() > 10) return;
        
        for (int i = 0; i < amount; i++) {
            fetchOneRiddle();
        }
    }

    private void fetchOneRiddle() {
        activeFetches.incrementAndGet();
        riddleService.getRandomRiddle().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<RiddleService.RiddleResponse> call, @NonNull Response<RiddleService.RiddleResponse> response) {
                activeFetches.decrementAndGet();
                if (response.isSuccessful() && response.body() != null) {
                    String riddle = response.body().riddle;
                    String answer = response.body().answer;

                    if (isValidRiddle(riddle, answer)) {
                        riddleCache.add(new String[]{riddle, answer});
                    } else {
                        // If invalid, try to fetch another one to fill the gap
                        fetchOneRiddle();
                    }
                }
            }

            private boolean isValidRiddle(String riddle, String answer) {
                if (riddle == null || answer == null) return false;

                // Check riddle length (roughly "a few sentences")
                if (riddle.length() > 160) return false;

                // Check answer length (one or two words)
                String[] words = answer.trim().split("\\s+");
                return words.length <= 2;
            }

            @Override
            public void onFailure(@NonNull Call<RiddleService.RiddleResponse> call, @NonNull Throwable t) {
                activeFetches.decrementAndGet();
            }
        });
    }

    public void testConnection(Callback<RiddleService.RiddleResponse> callback) {
        riddleService.getRandomRiddle().enqueue(callback);
    }

    public String[] getNextRiddle() {
        synchronized (riddleCache) {
            if (riddleCache.isEmpty()) {
                preloadRiddles(5);
                return null;
            }
            String[] riddle = riddleCache.remove(0);
            if (riddleCache.size() < 3) {
                preloadRiddles(5);
            }
            return riddle;
        }
    }
}
