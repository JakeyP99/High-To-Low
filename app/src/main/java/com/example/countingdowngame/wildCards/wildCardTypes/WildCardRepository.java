package com.example.countingdowngame.wildCards.wildCardTypes;

import android.content.Context;

import com.example.countingdowngame.settings.WildCardSettingsLocalStore;
import com.example.countingdowngame.wildCards.WildCardProperties;

import java.util.Random;

public class WildCardRepository {

    private final Context context;

    public WildCardRepository(Context context) {
        this.context = context;
    }

    public WildCardProperties[] loadQuizCards() {
        return load(WildCardData.QUIZ_WILD_CARDS, "QuizPrefs");
    }

    public WildCardProperties[] loadTaskCards() {
        return load(WildCardData.TASK_WILD_CARDS, "TaskPrefs");
    }

    public WildCardProperties[] loadTruthCards() {
        return load(WildCardData.TRUTH_WILD_CARDS, "TruthPrefs");
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

    public static WildCardProperties getRandom(WildCardProperties[] cards) {

        if (cards == null || cards.length == 0) {
            return null;
        }

        Random random = new Random();
        return cards[random.nextInt(cards.length)];
    }

}