package com.example.countingdowngame.mainActivity.classAbilities;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.*;

import com.example.countingdowngame.player.Player;


public class AbilityComplimentary {

    public static void assignClassAbilityCooldown(Player player) {
        if (player.getClassChoice().equals(ANGRY_JIM)) {
            player.setClassAbilityCooldown(5);
        } else {
            player.setClassAbilityCooldown(3); // fallback
        }
    }
}
