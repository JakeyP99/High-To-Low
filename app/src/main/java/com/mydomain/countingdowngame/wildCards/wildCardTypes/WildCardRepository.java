package com.mydomain.countingdowngame.wildCards.wildCardTypes;

import android.content.Context;

import com.google.gson.Gson;
import com.mydomain.countingdowngame.settings.WildCardSettingsLocalStore;
import com.mydomain.countingdowngame.wildCards.WildCardProperties;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class WildCardRepository {

    private final Context context;

    public WildCardRepository(Context context) {
        this.context = context;
    }

    public static WildCardProperties getRandom(WildCardProperties[] cards) {

        if (cards == null || cards.length == 0) {
            return null;
        }

        Random random = new Random();
        return cards[random.nextInt(cards.length)];
    }

    public WildCardProperties[] loadQuizCards() {
        return load(loadFromAssets("quizzes.json"), "QuizPrefs");
    }

    public WildCardProperties[] loadTaskCards() {
        return load(loadFromAssets("tasks.json"), "TaskPrefs");
    }

    public WildCardProperties[] loadTruthCards() {
        return load(loadFromAssets("truths.json"), "TruthPrefs");
    }

    private WildCardProperties[] loadFromAssets(String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);
            return new Gson().fromJson(json, WildCardProperties[].class);
        } catch (IOException e) {
            e.printStackTrace();
            return new WildCardProperties[0];
        }
    }

    private WildCardProperties[] load(WildCardProperties[] defaults, String key) {

        WildCardSettingsLocalStore prefs =
                WildCardSettingsLocalStore.fromContext(context, key);

        WildCardProperties[] result = new WildCardProperties[defaults.length];

        for (int i = 0; i < defaults.length; i++) {

            WildCardProperties d = defaults[i];

            boolean enabled = prefs.isWildcardEnabled(i, d.isEnabled());
            String activity = prefs.getWildcardActivityText(i, d.getWildCard());
            String answer = prefs.getWildcardAnswer(i, d.getAnswer());

            String w1 = prefs.getWildcardWrongAnswer(i, d.getWrongAnswer1());
            String w2 = prefs.getWildcardWrongAnswer2(i, d.getWrongAnswer2());
            String w3 = prefs.getWildcardWrongAnswer3(i, d.getWrongAnswer3());

            String category = prefs.getWildCardCategory(i, d.getCategory());

            result[i] = new WildCardProperties(
                    activity,
                    enabled,
                    d.isUsedWildCard(),
                    answer,
                    w1,
                    w2,
                    w3,
                    category
            );
        }

        return result;
    }

}