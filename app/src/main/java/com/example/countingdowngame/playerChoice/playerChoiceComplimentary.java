package com.example.countingdowngame.playerChoice;

import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.angryJimActiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.angryJimPassiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.archerActiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.archerPassiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.goblinActiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.goblinPassiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.noClassDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.quizMagicianActiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.quizMagicianPassiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.scientistActiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.scientistPassiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.soldierActiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.soldierPassiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.survivorActiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.survivorPassiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.witchActiveDescription;
import static com.example.countingdowngame.createPlayer.CharacterClassDescriptions.witchPassiveDescription;
import static com.example.countingdowngame.playerChoice.PlayerChoice.ANGRY_JIM;
import static com.example.countingdowngame.playerChoice.PlayerChoice.ARCHER;
import static com.example.countingdowngame.playerChoice.PlayerChoice.GOBLIN;
import static com.example.countingdowngame.playerChoice.PlayerChoice.NO_CLASS;
import static com.example.countingdowngame.playerChoice.PlayerChoice.QUIZ_MAGICIAN;
import static com.example.countingdowngame.playerChoice.PlayerChoice.SCIENTIST;
import static com.example.countingdowngame.playerChoice.PlayerChoice.SOLDIER;
import static com.example.countingdowngame.playerChoice.PlayerChoice.SURVIVOR;
import static com.example.countingdowngame.playerChoice.PlayerChoice.WITCH;

import com.example.countingdowngame.R;
import com.example.countingdowngame.createPlayer.CharacterClassStore;
import com.example.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.List;

public class playerChoiceComplimentary extends ButtonUtilsActivity {
    public List<CharacterClassStore> generateCharacterClasses() {
        List<CharacterClassStore> characterClasses = new ArrayList<>();
        characterClasses.add(new CharacterClassStore(1, ARCHER, archerActiveDescription, archerPassiveDescription, R.drawable.archer));
        characterClasses.add(new CharacterClassStore(2, WITCH, witchActiveDescription, witchPassiveDescription, R.drawable.witch));
        characterClasses.add(new CharacterClassStore(3, SCIENTIST, scientistActiveDescription, scientistPassiveDescription, R.drawable.scientist));
        characterClasses.add(new CharacterClassStore(4, SOLDIER, soldierActiveDescription, soldierPassiveDescription, R.drawable.helmet));
        characterClasses.add(new CharacterClassStore(5, QUIZ_MAGICIAN, quizMagicianActiveDescription, quizMagicianPassiveDescription, R.drawable.books));
        characterClasses.add(new CharacterClassStore(6, SURVIVOR, survivorActiveDescription, survivorPassiveDescription, R.drawable.bandaids));
        characterClasses.add(new CharacterClassStore(7, ANGRY_JIM, angryJimActiveDescription, angryJimPassiveDescription, R.drawable.angry_jim));
        characterClasses.add(new CharacterClassStore(8, GOBLIN, goblinActiveDescription, goblinPassiveDescription, R.drawable.goblin));
        characterClasses.add(new CharacterClassStore(9, NO_CLASS, noClassDescription, null, R.drawable.noclass));
        return characterClasses;
    }
}
