package com.example.countingdowngame.mainActivity.classAbilities;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.*;

import com.example.countingdowngame.player.Player;


public class AbilityComplimentary {

    public static void assignActiveAbilityCooldown(Player player) {
        String classChoice = player.getClassChoice();
        if (classChoice.equals(ANGRY_JIM)) {
            player.setActiveAbilityCooldown(5);
        } else if (classChoice.equals(WITCH) || classChoice.equals(SURVIVOR)) {
            player.setActiveAbilityCooldown(3);
        } else {
            player.setActiveAbilityCooldown(4); // fallback
        }
    }
}
