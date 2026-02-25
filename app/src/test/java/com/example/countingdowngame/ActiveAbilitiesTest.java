package com.example.countingdowngame;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.countingdowngame.createPlayer.CharacterClassDescriptions;
import com.example.countingdowngame.createPlayer.PlayerModelLocalStore;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.player.Player;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34, shadows = {ActiveAbilitiesTest.ShadowGifImageView.class})
public class ActiveAbilitiesTest {

    private Context context;

    @Implements(GifImageView.class)
    public static class ShadowGifImageView {
        @Implementation
        public void __constructor__(Context context) {
            // Do nothing
        }
    }

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        
        Game.getInstance().reset();
        MainActivityGame.resetStaticState();
        MainActivityGame.catastrophesEnabled = false; 
        
        context.getSharedPreferences("playerModelSettings", Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences("generalSettings", Context.MODE_PRIVATE).edit().putInt("playerWildCardCount", 5).commit();
    }

    private void setupGameWithPlayer(String className) {
        List<Player> players = new ArrayList<>();
        players.add(new Player(context, "id1", null, "Tester", className));
        players.add(new Player(context, "id2", null, "Opponent", CharacterClassDescriptions.NO_CLASS));
        PlayerModelLocalStore.fromContext(context).saveSelectedPlayers(players);
        Game.getInstance().setPlayCards(false); 
    }

    @Test
    public void testArcherAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.ARCHER);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 10000);
        
        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnGenerate)).perform(click()); // T1 plays
            onView(withId(R.id.btnGenerate)).perform(click()); // O1 plays
            onView(withId(R.id.btnGenerate)).perform(click()); // T2 plays
            onView(withId(R.id.btnGenerate)).perform(click()); // O2 plays -> Drinks = 2
            onView(withId(R.id.btnGenerate)).perform(click()); // T3 (Archer turn)
            
            onView(allOf(withId(R.id.btnClassAbility), withText(CharacterClassDescriptions.archerActiveButtonText))).perform(click());
            onView(withText("Archer's Active: \n\nTester hand out two drinks!")).check(matches(isDisplayed()));
            onView(withId(R.id.close_button)).perform(click());
        }
    }

    @Test
    public void testScientistAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.SCIENTIST);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 10000);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnClassAbility)).perform(click());
            onView(withId(R.id.editCurrentNumberTextView)).perform(replaceText("50"));
            onView(withId(R.id.close_button)).perform(click());
            onView(withId(R.id.textView_NumberText)).check(matches(withText("50")));
        }
    }

    @Test
    public void testWitchAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.WITCH);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 10000);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnClassAbility)).perform(click());
            onView(withId(R.id.textView_Number_Turn)).check(matches(withText("Opponent has 1 Turn")));
        }
    }

    @Test
    public void testSoldierAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.SOLDIER);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 10); 

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnGenerate)).perform(click()); // O1
            onView(withId(R.id.btnGenerate)).perform(click()); // T2 (Soldier turn)
            
            onView(allOf(withId(R.id.btnClassAbility), withText(CharacterClassDescriptions.soldierActiveButtonText))).perform(click());
            onView(withId(R.id.textView_numberCounter)).check(matches(withText("5 Drinks")));
        }
    }

    @Test
    public void testSurvivorAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.SURVIVOR);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 10000);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnClassAbility)).perform(click());
            onView(withId(R.id.textView_NumberText)).check(matches(withText("5000")));
        }
    }

    @Test
    public void testAngryJimAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.ANGRY_JIM);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 10000);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnGenerate)).perform(click()); // T1 plays
            onView(withId(R.id.btnGenerate)).perform(click()); // O1 plays
            onView(withId(R.id.btnGenerate)).perform(click()); // T2 (Angry Jim turn)
            
            onView(withId(R.id.btnClassAbility)).perform(click());
            onView(withText("Angry Jim's Active: \n\nOpponent must repeat their turn.")).check(matches(isDisplayed()));
            onView(withId(R.id.close_button)).perform(click());
        }
    }

    @Test
    public void testGoblinAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.GOBLIN);
        
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 10000);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnGenerate)).perform(click()); // T1
            onView(withId(R.id.btnGenerate)).perform(click()); // O1
            onView(withId(R.id.btnGenerate)).perform(click()); // T2 (Goblin turn)

            onView(withId(R.id.btnClassAbility)).perform(click());
            onView(withText("Goblin's Active: \n\nOpponent lost two wildcards.\n\nOpponent now has 3 wildcards left.")).check(matches(isDisplayed()));
            onView(withId(R.id.close_button)).perform(click());
        }
    }
}
