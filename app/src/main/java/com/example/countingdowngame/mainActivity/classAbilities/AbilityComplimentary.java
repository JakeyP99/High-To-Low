package com.example.countingdowngame.mainActivity.classAbilities;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.*;

import com.example.countingdowngame.player.Player;


public class AbilityComplimentary {

    public static void assignActiveAbilityCooldown(Player player) {
        String classChoice = player.getClassChoice();
        player.setActiveAbilityCooldown(getClassCooldown(classChoice));
    }

    public static int getClassCooldown(String classChoice) {
        if (classChoice == null) return 999;

        // Special Reset Classes (Always reset)
        if (ANGRY_JIM.equals(classChoice) || WITCH.equals(classChoice)) return 5;
        if (ARCHER.equals(classChoice) ||SURVIVOR.equals(classChoice)) return 3;

        // All other classes do not reset (set to a very high number)
        return 999;
    }
}
