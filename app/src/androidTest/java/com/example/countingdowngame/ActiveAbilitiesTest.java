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
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.countingdowngame.createPlayer.CharacterClassDescriptions;
import com.example.countingdowngame.createPlayer.PlayerModelLocalStore;
import com.example.countingdowngame.game.Game;
import com.example.countingdowngame.mainActivity.MainActivityGame;
import com.example.countingdowngame.player.Player;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ActiveAbilitiesTest {

    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        Game.getInstance().reset();
        MainActivityGame.resetStaticState();
        MainActivityGame.catastrophesEnabled = false;

        context.getSharedPreferences("playerModelSettings", Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences("PlayerStats", Context.MODE_PRIVATE).edit().clear().commit();
    }

    private void setupGameWithPlayer(String className) {
        List<Player> players = new ArrayList<>();
        players.add(new Player(context, "id1", null, "Tester", className));
        players.add(new Player(context, "id2", null, "Opponent", CharacterClassDescriptions.NO_CLASS));
        PlayerModelLocalStore.fromContext(context).saveSelectedPlayers(players);
        Game.getInstance().setPlayCards(false);
    }

    private void waitForUI() {
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Test
    public void testArcherAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.ARCHER);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 100000);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            waitForUI();
            onView(withId(R.id.btnGenerate)).perform(click()); // T1
            waitForUI();
            onView(withId(R.id.btnGenerate)).perform(click()); // O1
            waitForUI();
            onView(withId(R.id.btnGenerate)).perform(click()); // T2
            waitForUI();
            onView(withId(R.id.btnGenerate)).perform(click()); // O2 -> Drinks = 2
            waitForUI();
            onView(withId(R.id.btnGenerate)).perform(click()); // T3 (Tester turn)
            waitForUI();

            onView(allOf(withId(R.id.btnClassAbility), withText(CharacterClassDescriptions.archerActiveButtonText))).perform(click());
            onView(withText("Archer's Active: \n\nTester hand out two drinks!")).check(matches(isDisplayed()));
            onView(withId(R.id.close_button)).perform(click());
        }
    }

    @Test
    public void testScientistAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.SCIENTIST);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 100);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            waitForUI();
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
        intent.putExtra("startingNumber", 100);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            waitForUI();
            onView(withId(R.id.btnClassAbility)).perform(click());
            waitForUI();
            onView(withId(R.id.textView_Number_Turn)).check(matches(withText("Opponent has 1 Turn")));
        }
    }

    @Test
    public void testSoldierAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.SOLDIER);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 10);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            waitForUI();
            onView(withId(R.id.btnGenerate)).perform(click()); // O1
            waitForUI();
            onView(withId(R.id.btnGenerate)).perform(click()); // T2 (Tester)
            waitForUI();

            onView(allOf(withId(R.id.btnClassAbility), withText(CharacterClassDescriptions.soldierActiveButtonText))).perform(click());
            onView(withId(R.id.textView_numberCounter)).check(matches(withText("5 Drinks")));
        }
    }

    @Test
    public void testSurvivorAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.SURVIVOR);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 100);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            waitForUI();
            onView(withId(R.id.btnClassAbility)).perform(click());
            onView(withId(R.id.textView_NumberText)).check(matches(withText("50")));
        }
    }

    @Test
    public void testAngryJimAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.ANGRY_JIM);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 100);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            waitForUI();
            onView(withId(R.id.btnGenerate)).perform(click()); // O1
            waitForUI();
            onView(withId(R.id.btnGenerate)).perform(click()); // T2
            waitForUI();

            onView(withId(R.id.btnClassAbility)).perform(click());
            onView(withText("Angry Jim's Active: \n\nOpponent must repeat their turn.")).check(matches(isDisplayed()));
            onView(withId(R.id.close_button)).perform(click());
        }
    }

    @Test
    public void testGoblinAbility() {
        setupGameWithPlayer(CharacterClassDescriptions.GOBLIN);

        List<Player> players = PlayerModelLocalStore.fromContext(context).loadSelectedPlayers();
        players.get(1).gainWildCards(5);
        PlayerModelLocalStore.fromContext(context).saveSelectedPlayers(players);

        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 100);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            waitForUI();
            onView(withId(R.id.btnClassAbility)).perform(click());
            onView(withText("Goblin's Active: \n\nOpponent lost two wildcards.\n\nOpponent now has 3 wildcards left.")).check(matches(isDisplayed()));
            onView(withId(R.id.close_button)).perform(click());
        }
    }
}
