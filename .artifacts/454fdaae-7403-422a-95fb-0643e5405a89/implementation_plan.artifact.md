# Professional & Game-Themed Instructions Overhaul

This plan overhauls the instruction screen to make it feel immersive, professional, and consistent with the game's aesthetic. We will move away from a plain white background to a themed experience with visual aids for each step.

## Proposed Changes

### [Component] UI/UX Design

#### [NEW] [instruction_page_item.xml](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/res-instructions/layout/instruction_page_item.xml)
- Create a dedicated layout for each instruction page.
- Top: Large `GifImageView` for visual representation.
- Center: Large, bold `TextView` for the Step Title.
- Bottom: Descriptive `TextView` for the explanation.

#### [MODIFY] [instruction_main_activity.xml](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/res-instructions/layout/instruction_main_activity.xml)
- Change background to a themed gradient or dark color.
- Replace `ViewPager` with `ViewPager2`.
- Add a `DotsIndicator` for modern navigation.
- Update the "Next" button to a themed floating action button or a large game-like button.

### [Component] Logic & Data

#### [NEW] `InstructionStep` Data Class
- Create a simple model to hold Title, Description (string resource), and Image/GIF resource.

#### [MODIFY] [InstructionsToPlay.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/instructions/InstructionsToPlay.java)
- Update to use `ViewPager2` and a new `InstructionAdapter`.
- Define a list of `InstructionStep` objects, mapping each instruction to a relevant visual asset (e.g., `witch.gif` for "Choose Class").

#### [MODIFY] [InstructionPageAdapter.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/instructions/InstructionPageAdapter.java)
- Convert to a `RecyclerView.Adapter` to work with `ViewPager2`.

## Visual Mapping Plan

| Step | Visual Asset |
| :--- | :--- |
| Welcome | `books.gif` |
| Aim | `shots.gif` |
| Players | `selection.png` |
| Classes | `witch.gif` |
| Number Choice | `dice.png` |
| Wildcards | `playingcards.png` |
| Catastrophe | `troll.gif` |
| The End | `gun.gif` |

## Verification Plan

### Automated Tests
- Verify successful compilation with `gradlew app:assembleDebug`.

### Manual Verification
- Deploy to device and navigate to Instructions.
- Verify smooth transitions between pages.
- Verify GIFs play correctly.
- Verify "Next" button correctly transitions to "Play!" or "Done" on the final page.
