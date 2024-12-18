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
    public static final String archerActiveDescription = "You can take two drinks off the total amount, and hand them out to players. You have to repeat your turn!";
    public static final String witchActiveDescription = "You can skip your turn. Ability resets after three turns.";
    public static final String scientistActiveDescription = "You can change the current number to be whatever you want. You have to repeat your turn!";
    public static final String soldierActiveDescription = "If the number less than or equal to 10, add 4 drinks to the counter, but you must generate the number twice.";
    public static final String quizMagicianActiveDescription = "The next wildcard will be two quiz questions (double the fun).";
    public static final String survivorActiveDescription = "Half the current number, and continue your turn. Ability resets after three turns.";
    public static final String angryJimActiveDescription = "Randomly make another player repeat their turn.";
    public static final String goblinActiveDescription = "Remove two wildcards from a random player.";
    public static final String noClassDescription = "Choose this class if you don't want to have any abilities.";


    // Passive Ability Descriptions
    public static final String archerPassiveDescription = "For every third turn, there is a 60% chance the drinking number will increase by 2 and a 40% chance it will decrease by 2.";
    public static final String witchPassiveDescription = "For every even number your player lands on, you can hand out two drinks, but for every odd number you have to take two drinks.";
    public static final String scientistPassiveDescription = "There's a 10% chance your turn is skipped.";
    public static final String soldierPassiveDescription = "If you land between 10 and 15, you escape the game. This means you no longer will be playing, and won't have to drink anymore.";
    public static final String quizMagicianPassiveDescription = "All multi-choice quizzes now have only two answers available, instead of four.";
    public static final String survivorPassiveDescription = "If the current number is 1, and you generate another 1, you can distribute the current count of drinks without reducing the drink number.";
    public static final String angryJimPassiveDescription = "When the number is below 50, you gain the passive ability of every single player. The catch is, you need to generate the number twice.";
    public static final String goblinPassiveDescription = "For every third turn, you gain one wildcard.";
}
