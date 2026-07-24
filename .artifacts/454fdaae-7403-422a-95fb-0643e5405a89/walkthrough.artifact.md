# Overhauled Instructions Screen

I have completely redesigned the instructions screen to give it a more professional, game-like, and immersive feel.

## Key Improvements

### 1. Immersive UI Design
- **Clean & Seamless**: Switched to a professional white background with high-contrast blue accents (`bluedark`). Removed the card borders for a modern, integrated look that flows perfectly across the screen.
- **Maximized Content Space**: Optimized internal spacing and padding to ensure instruction text is never cut off, providing a clear and airy reading experience.
- **Modern Navigation**: Replaced the old circular progress bar with a sleek, low-profile `DotsIndicator` and updated to `ViewPager2` for smooth scrolling.

### 2. Visual Content
- **Instructional Visuals**: Each step now features a relevant animated GIF or icon (e.g., `books.gif` for quizzes, `troll.gif` for catastrophes) to help players understand the game mechanics visually.
- **Improved Typography**: Standardized the use of the `viga` font for titles and descriptions, ensuring a consistent look and feel throughout.

### 3. Smooth Animations
- **Page Transformer**: Added a "Zoom & Fade" effect when swiping between instruction pages, making the transition feel dynamic and polished.
- **Interactive Button**: The "Next" button now pulses gently to draw the player's attention and changes to a "Let's Play!" call-to-action on the final page.

## Changes Made

- **[NEW] [instruction_page_item.xml](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/res-instructions/layout/instruction_page_item.xml)**: Dedicated layout for instruction cards.
- **[NEW] [InstructionStep.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/instructions/InstructionStep.java)**: Model for mapping text to visual assets.
- **[MODIFY] [instruction_main_activity.xml](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/res-instructions/layout/instruction_main_activity.xml)**: Themed container with `ViewPager2` and `DotsIndicator`.
- **[MODIFY] [InstructionPageAdapter.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/instructions/InstructionPageAdapter.java)**: Updated to modern `RecyclerView.Adapter` for better lifecycle management.
- **[MODIFY] [InstructionsToPlay.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/instructions/InstructionsToPlay.java)**: Wired up the new visuals and added the pulsing button animation.

## Verification Results

### Automated Tests
- Build successfully completed with `./gradlew app:assembleDebug`.

### Manual Verification
- Navigate to the **Instructions** screen from the Home Screen.
- Swipe through the pages to see the new card animations and visual aids.
- Tap "Let's Play!" on the final page to return to the home screen.
