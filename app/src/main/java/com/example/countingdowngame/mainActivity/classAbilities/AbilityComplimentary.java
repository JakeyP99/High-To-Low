package com.example.countingdowngame.mainActivity.classAbilities;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.*;

import com.example.countingdowngame.player.Player;


public class AbilityComplimentary {

    public static void assignActiveAbilityCooldown(Player player) {
        if (player.getClassChoice().equals(ANGRY_JIM)) {
            player.setActiveAbilityCooldown(6);
        } else {
            player.setActiveAbilityCooldown(4); // fallback
        }
    }
}
