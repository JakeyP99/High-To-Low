package com.mydomain.countingdowngame.playerChoice;

import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.ANGRY_JIM;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.ARCHER;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.GAMBLER;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.GOBLIN;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.NO_CLASS;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.QUIZ_MAGICIAN;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.SCIENTIST;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.SOLDIER;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.SURVIVOR;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.TROLL;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.WITCH;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.angryJimActiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.angryJimCooldown;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.angryJimPassiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.angryJimQuote;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.archerActiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.archerCooldown;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.archerPassiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.archerQuote;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.gamblerActiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.gamblerCooldown;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.gamblerPassiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.gamblerQuote;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.goblinActiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.goblinCooldown;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.goblinPassiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.goblinQuote;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.noClassDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.quizMagicianActiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.quizMagicianCooldown;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.quizMagicianPassiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.quizMagicianQuote;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.scientistActiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.scientistCooldown;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.scientistPassiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.scientistQuote;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.soldierActiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.soldierCooldown;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.soldierPassiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.soldierQuote;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.survivorActiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.survivorCooldown;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.survivorPassiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.survivorQuote;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.trollActiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.trollCooldown;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.trollPassiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.trollQuote;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.witchActiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.witchCooldown;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.witchPassiveDescription;
import static com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassDescriptions.witchQuote;

import com.mydomain.countingdowngame.R;
import com.mydomain.countingdowngame.playerChoice.createPlayer.CharacterClassStore;
import com.mydomain.countingdowngame.utils.ButtonUtilsActivity;

import java.util.ArrayList;
import java.util.List;

public class playerChoiceComplimentary extends ButtonUtilsActivity {

    public static int getClassIcon(String classChoice) {
        if (classChoice == null) return R.drawable.wine;
        switch (classChoice) {
            case ANGRY_JIM:
                return R.drawable.angry_jim;
            case ARCHER:
                return R.drawable.archer;
            case GAMBLER:
                return R.drawable.slotmachine;
            case GOBLIN:
                return R.drawable.goblin;
            case QUIZ_MAGICIAN:
                return R.drawable.books;
            case SCIENTIST:
                return R.drawable.scientist;
            case SOLDIER:
                return R.drawable.helmet;
            case SURVIVOR:
                return R.drawable.bandaids;
            case TROLL:
                return R.drawable.troll;
            case WITCH:
                return R.drawable.witch;
            case NO_CLASS:
                return R.drawable.noclass;
            default:
                return R.drawable.wine;
        }
    }

    public List<CharacterClassStore> generateCharacterClasses() {
        List<CharacterClassStore> characterClasses = new ArrayList<>();
        characterClasses.add(new CharacterClassStore(1, ANGRY_JIM, angryJimActiveDescription, angryJimPassiveDescription, angryJimQuote, angryJimCooldown, getClassIcon(ANGRY_JIM)));
        characterClasses.add(new CharacterClassStore(2, ARCHER, archerActiveDescription, archerPassiveDescription, archerQuote, archerCooldown, getClassIcon(ARCHER)));
        characterClasses.add(new CharacterClassStore(3, GAMBLER, gamblerActiveDescription, gamblerPassiveDescription, gamblerQuote, gamblerCooldown, getClassIcon(GAMBLER)));
        characterClasses.add(new CharacterClassStore(4, GOBLIN, goblinActiveDescription, goblinPassiveDescription, goblinQuote, goblinCooldown, getClassIcon(GOBLIN)));
        characterClasses.add(new CharacterClassStore(5, QUIZ_MAGICIAN, quizMagicianActiveDescription, quizMagicianPassiveDescription, quizMagicianQuote, quizMagicianCooldown, getClassIcon(QUIZ_MAGICIAN)));
        characterClasses.add(new CharacterClassStore(6, SCIENTIST, scientistActiveDescription, scientistPassiveDescription, scientistQuote, scientistCooldown, getClassIcon(SCIENTIST)));
        characterClasses.add(new CharacterClassStore(7, SOLDIER, soldierActiveDescription, soldierPassiveDescription, soldierQuote, soldierCooldown, getClassIcon(SOLDIER)));
        characterClasses.add(new CharacterClassStore(8, SURVIVOR, survivorActiveDescription, survivorPassiveDescription, survivorQuote, survivorCooldown, getClassIcon(SURVIVOR)));
        characterClasses.add(new CharacterClassStore(9, TROLL, trollActiveDescription, trollPassiveDescription, trollQuote, trollCooldown, getClassIcon(TROLL)));
        characterClasses.add(new CharacterClassStore(10, WITCH, witchActiveDescription, witchPassiveDescription, witchQuote, witchCooldown, getClassIcon(WITCH)));
        characterClasses.add(new CharacterClassStore(11, NO_CLASS, noClassDescription, null, null, 0, getClassIcon(NO_CLASS)));

        return characterClasses;
    }
}
