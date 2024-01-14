package com.example.countingdowngame.createPlayer;

public class CharacterClassDescriptions {
    // Active Ability Descriptions
    public static final String archerActiveDescription = "You can take two drinks off the total amount, and hand them out to players. You have to repeat your turn!";
    public static final String witchActiveDescription = "Once per game you can skip your turn.";
    public static final String scientistActiveDescription = "You can change the current number to be whatever you want. You have to repeat your turn!";
    public static final String soldierActiveDescription = "If the number less than or equal to 10, add 4 drinks, but you must repeat your turn twice.";
    public static final String quizMagicianActiveDescription = "Remove one wildcard from all players.";
    public static final String survivorActiveDescription = "Half the current number, and continue your turn.";
    public static final String jimActiveDescription = "";
    public static final String noClassDescription = "Choose this if you don't want to have any abilities.";


    // Passive Ability Descriptions
    public static final String archerPassiveDescription = "For every third turn, there is a 60% chance the drinking number will increase by 2 and a 40% chance it will decrease by 2.";
    public static final String witchPassiveDescription = "For every even number your player lands on, you can hand out two drinks, but for every odd number you have to take two drinks.";
    public static final String scientistPassiveDescription = "There's a 10% chance your turn is skipped.";
    public static final String soldierPassiveDescription = "If you land between 10 and 15, you escape the game. This means you no longer will be playing, and won't have to drink anymore.";
    public static final String quizMagicianPassiveDescription = "All multi-choice quizzes now have only two answers available, instead of four.";
    public static final String survivorPassiveDescription = "If the current number is 1, and you successfully endure the generated number, you can distribute the current count of drinks. The drink number is not affected.";
    public static final String jimPassiveDescription = "For every third turn, you gain one wildcard.";
}
