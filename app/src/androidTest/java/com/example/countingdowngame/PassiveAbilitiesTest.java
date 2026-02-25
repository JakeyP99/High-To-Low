package com.example.countingdowngame;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

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
public class PassiveAbilitiesTest {

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
        try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Test
    public void testWitchPassiveEven() {
        setupGameWithPlayer(CharacterClassDescriptions.WITCH);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 100); // Even number

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            // Advancing past first turn to trigger passive
            onView(withId(R.id.btnGenerate)).perform(click()); // Turn 1 (Opponent)
            waitForUI();
            onView(withId(R.id.btnGenerate)).perform(click()); // Turn 2 (Tester - Witch)
            waitForUI();
            
            onView(withText(containsString("1 drink"))).check(matches(isDisplayed()));
            onView(withId(R.id.close_button)).perform(click());
        }
    }

    @Test
    public void testSoldierPassiveEscape() {
        setupGameWithPlayer(CharacterClassDescriptions.SOLDIER);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 12); // Within 10-15 range

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnGenerate)).perform(click()); // Turn 1 (Opponent)
            waitForUI();
            onView(withId(R.id.btnGenerate)).perform(click()); // Turn 2 (Tester - Soldier)
            waitForUI();
            
            onView(withText(containsString("has escaped the game"))).check(matches(isDisplayed()));
            onView(withId(R.id.close_button)).perform(click());
        }
    }

    @Test
    public void testGoblinPassiveGainWildcard() {
        setupGameWithPlayer(CharacterClassDescriptions.GOBLIN);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 1000);

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            // Need 3 turns for Goblin to gain wildcard.
            // 1. Tester (Turn 1)
            onView(withId(R.id.btnGenerate)).perform(click()); waitForUI();
            // 2. Opponent
            onView(withId(R.id.btnGenerate)).perform(click()); waitForUI();
            // 3. Tester (Turn 2)
            onView(withId(R.id.btnGenerate)).perform(click()); waitForUI();
            // 4. Opponent
            onView(withId(R.id.btnGenerate)).perform(click()); waitForUI();
            // 5. Tester (Turn 3)
            onView(withId(R.id.btnGenerate)).perform(click()); waitForUI();
            
            // Check button text for wildcard count (starts at 1 in test setup usually)
            onView(withId(R.id.btnWild)).check(matches(withText(containsString("2"))));
        }
    }

    @Test
    public void testAngryJimPassiveTriggered() {
        setupGameWithPlayer(CharacterClassDescriptions.ANGRY_JIM);
        Intent intent = new Intent(context, MainActivityGame.class);
        intent.putExtra("startingNumber", 40); // Below 50

        try (ActivityScenario<MainActivityGame> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.btnGenerate)).perform(click()); // Opponent
            waitForUI();
            onView(withId(R.id.btnGenerate)).perform(click()); // Tester
            waitForUI();
            
            // Should gain an extra turn
            onView(withId(R.id.textView_Number_Turn)).check(matches(withText(containsString("2 Turns"))));
        }
    }
}
