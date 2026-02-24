package com.example.countingdowngame.createPlayer;

public class CharacterClassDescriptions {
    public static final String ARCHER = "Archer";
    public static final String WITCH = "Witch";
    public static final String SCIENTIST = "Scientist";
    public static final String SOLDIER = "Soldier";
    public static final String ANGRY_JIM = "Angry Jim";
    public static final String QUIZ_MAGICIAN = "Quiz Magician";
    public static final String SURVIVOR = "Survivor";
    public static final String GOBLIN = "Goblin";
    public static final String NO_CLASS = "No Class";

    // Active Ability Descriptions
    public static final String archerActiveDescription =
            "You may remove 2 drinks from the total and give them to any players.";

    public static final String witchActiveDescription =
            "Skip your turn. Resets after 3 turns.";

    public static final String scientistActiveDescription =
            "Change the current number to any number. Your turn continues.";

    public static final String soldierActiveDescription =
            "If the number is 10 or less, add 4 drinks. You must generate twice.";

    public static final String quizMagicianActiveDescription =
            "The next wildcard becomes 2 quiz questions.";

    public static final String survivorActiveDescription =
            "Halve the current number and continue your turn. Resets after 3 turns.";

    public static final String angryJimActiveDescription =
            "Force a random player to repeat their turn. Resets after 5 turns.";

    public static final String goblinActiveDescription =
            "Remove 2 wildcards from a random player.";

    public static final String noClassDescription =
            "No abilities.";

    // Active Ability Button Text (Shortened versions)
    public static final String archerActiveButtonText = "Hand out 2 drinks";
    public static final String witchActiveButtonText = "Skip your turn";
    public static final String scientistActiveButtonText = "Change current number";
    public static final String soldierActiveButtonText = "Add 4 drinks";
    public static final String quizMagicianActiveButtonText = "Next wildcard is 2 quizzes";
    public static final String survivorActiveButtonText = "Halve current number";
    public static final String angryJimActiveButtonText = "Force turn repeat";
    public static final String goblinActiveButtonText = "Destroy 2 wildcards";

    // Passive Ability Descriptions
    public static final String archerPassiveDescription =
            "Every third turn: 60% chance +2 drinks, 40% chance -2 drinks.";

    public static final String witchPassiveDescription =
            "On your turn: Even number = give 1 drink. Odd number = take 1 drink.";

    public static final String scientistPassiveDescription =
            "The lower the number, the higher the chance your turn is skipped.";

    public static final String soldierPassiveDescription =
            "If you land between 10–15, you leave the game, and no more drinking. Only one Soldier can escape.";

    public static final String quizMagicianPassiveDescription =
            "Multiple-choice quizzes have 2 answers. Correct answers: everyone drinks 2.";

    public static final String survivorPassiveDescription =
            "If the number is 1 and you survive a roll, you may distribute the current drinks (counter stays the same).";

    public static final String angryJimPassiveDescription =
            "When the number is below 50, you gain all other passives, but must take another turn.";

    public static final String goblinPassiveDescription =
            "After every third turn, gain 1 wildcard.";
}
