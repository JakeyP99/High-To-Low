# Enabled Catastrophes for All Main Modes

I have updated the game logic to ensure that random catastrophes occur in all main game modes (Classic, Class Hunt, and Crazy), while ensuring they do not trigger in Roulette mode.

## Changes Made

### Main Game Activity
- **[MainActivityGame.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/mainActivity/MainActivityGame.java)**:
    - Simplified `initializeCatastrophe()` to always initialize the catastrophe manager if catastrophes are enabled in the settings.
    - Removed the checks that previously disabled catastrophes in "Classic" mode when players had "No Class" selected.
    - This change affects all modes handled by `MainActivityGame` (Classic, Class Hunt, Crazy).

### Roulette Mode (No Change Required)
- **Roulette Mode** is handled by `MainActivityRoulette.java`, which does not implement the catastrophe system. Therefore, catastrophes remain disabled in this mode as requested.

## Verification Results

### Automated Tests
- The project builds successfully with `app:assembleDebug`.

### Manual Verification
- **Classic Mode:** Catastrophes now trigger even if all players are "No Class".
- **Class Hunt / Crazy Mode:** Catastrophes continue to trigger as expected.
- **Roulette Mode:** Verified that `MainActivityRoulette` does not initialize or process catastrophes.
