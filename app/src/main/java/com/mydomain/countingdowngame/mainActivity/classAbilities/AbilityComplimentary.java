package com.mydomain.countingdowngame.mainActivity.classAbilities;

import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.ARCHER;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.WITCH;

import com.mydomain.countingdowngame.player.Player;


public class AbilityComplimentary {

    public static void assignActiveAbilityCooldown(Player player) {
        String classChoice = player.getClassChoice();
        player.setActiveAbilityCooldown(getClassCooldown(classChoice));
    }

    public static int getClassCooldown(String classChoice) {
        if (classChoice == null) return 999;

        // Special Reset Classes (Always reset)
        if (ANGRY_JIM.equals(classChoice) || WITCH.equals(classChoice)) return 5;
        if (ARCHER.equals(classChoice) || SURVIVOR.equals(classChoice)) return 3;

        // All other classes do not reset (set to a very high number)
        return 999;
    }
}
