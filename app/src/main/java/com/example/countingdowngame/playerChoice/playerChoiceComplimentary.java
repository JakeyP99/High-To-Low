package com.mydomain.countingdowngame.playerChoice;

import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.ARCHER;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.GAMBLER;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.GOBLIN;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.NO_CLASS;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.SCIENTIST;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.SOLDIER;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.WITCH;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.angryJimActiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.angryJimPassiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.archerActiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.archerPassiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.gamblerActiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.gamblerPassiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.goblinActiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.goblinPassiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.noClassDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.quizMagicianActiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.quizMagicianPassiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.scientistActiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.scientistPassiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.soldierActiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.soldierPassiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.survivorActiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.survivorPassiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.TROLL;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.trollActiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.trollPassiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.witchActiveDescription;
import static com.mydomain.countingdowngame.createPlayer.CharacterClassDescriptions.witchPassiveDescription;

import com.mydomain.countingdowngame.R;
import com.mydomain.countingdowngame.createPlayer.CharacterClassStore;
import com.mydomain.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.List;

public class playerChoiceComplimentary extends ButtonUtilsActivity {

    public List<CharacterClassStore> generateCharacterClasses() {
        List<CharacterClassStore> characterClasses = new ArrayList<>();
        characterClasses.add(new CharacterClassStore(1, ANGRY_JIM, angryJimActiveDescription, angryJimPassiveDescription, getClassIcon(ANGRY_JIM)));
        characterClasses.add(new CharacterClassStore(2, ARCHER, archerActiveDescription, archerPassiveDescription, getClassIcon(ARCHER)));
        characterClasses.add(new CharacterClassStore(3, GAMBLER, gamblerActiveDescription, gamblerPassiveDescription, getClassIcon(GAMBLER)));
        characterClasses.add(new CharacterClassStore(4, GOBLIN, goblinActiveDescription, goblinPassiveDescription, getClassIcon(GOBLIN)));
        characterClasses.add(new CharacterClassStore(5, QUIZ_MAGICIAN, quizMagicianActiveDescription, quizMagicianPassiveDescription, getClassIcon(QUIZ_MAGICIAN)));
        characterClasses.add(new CharacterClassStore(6, SCIENTIST, scientistActiveDescription, scientistPassiveDescription, getClassIcon(SCIENTIST)));
        characterClasses.add(new CharacterClassStore(7, SOLDIER, soldierActiveDescription, soldierPassiveDescription, getClassIcon(SOLDIER)));
        characterClasses.add(new CharacterClassStore(8, SURVIVOR, survivorActiveDescription, survivorPassiveDescription, getClassIcon(SURVIVOR)));
        characterClasses.add(new CharacterClassStore(9, TROLL, trollActiveDescription, trollPassiveDescription, getClassIcon(TROLL)));
        characterClasses.add(new CharacterClassStore(10, WITCH, witchActiveDescription, witchPassiveDescription, getClassIcon(WITCH)));
        characterClasses.add(new CharacterClassStore(11, NO_CLASS, noClassDescription, null, getClassIcon(NO_CLASS)));

        return characterClasses;
    }

    public static int getClassIcon(String classChoice) {
        if (classChoice == null) return R.drawable.wine;
        switch (classChoice) {
            case ANGRY_JIM: return R.drawable.angry_jim;
            case ARCHER: return R.drawable.archer;
            case GAMBLER: return R.drawable.slotmachine;
            case GOBLIN: return R.drawable.goblin;
            case QUIZ_MAGICIAN: return R.drawable.books;
            case SCIENTIST: return R.drawable.scientist;
            case SOLDIER: return R.drawable.helmet;
            case SURVIVOR: return R.drawable.bandaids;
            case TROLL: return R.drawable.troll;
            case WITCH: return R.drawable.witch;
            case NO_CLASS: return R.drawable.noclass;
            default: return R.drawable.wine;
        }
    }
}
