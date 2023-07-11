package com.example.countingdowngame;

public class QuizWildCardHeadings extends WildCardHeadings {
    private String answer;

    public QuizWildCardHeadings(String activity, int probability, boolean enabled, boolean deletable, String answer) {
        super(activity, probability, enabled, deletable);
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean hasAnswer() {
        return answer != null && !answer.isEmpty();
    }
}
