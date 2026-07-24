# Implementation Plan - Enable Catastrophes for All Main Modes

The user wants catastrophes to work in all game modes (Classic, Class Hunt, Crazy) but explicitly NOT in the Roulette game mode. Currently, catastrophes are disabled in Classic mode if all players have "No Class". Since Roulette mode is handled by a different activity (`MainActivityRoulette`) that doesn't implement catastrophe logic, we simply need to enable them unconditionally in the main game activity (`MainActivityGame`).

## Proposed Changes

### [Component] Game Logic

#### [MODIFY] [MainActivityGame.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/mainActivity/MainActivityGame.java)
- Simplify `initializeCatastrophe()` to always initialize the catastrophe manager and set the limit if catastrophes are enabled in settings, regardless of player classes or game mode.

## Verification Plan

### Automated Tests
- Build the project using `./gradlew app:assembleDebug`.

### Manual Verification
- Start a game in **Classic Mode** with "No Class" selected for all players. Verify that catastrophes still occur (after a random number of turns).
- Start a game in **Class Hunt** or **Crazy Mode**. Verify that catastrophes still occur.
- Start a game in **Roulette Mode**. Verify that no random catastrophes occur (only the normal bullet mechanics).
