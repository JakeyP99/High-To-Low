# Fix slow scrolling on Statistics page

The user reported that the statistics page scrolls very slowly. Investigation revealed that the `StatisticsAdapter` performs expensive Base64 and Bitmap decoding on the UI thread inside `getView()`.

## Proposed Changes

### [Component] Statistics Data & UI

#### [MODIFY] [PlayerStatistic.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/statistics/PlayerStatistic.java)
- Add a `Bitmap` field to cache the player's photo.
- Update constructor to accept the `Bitmap`.

#### [MODIFY] [Statistics.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/statistics/Statistics.java)
- In `setPlayerStatistics()`, decode the player's photo string from `SharedPreferences` into a `Bitmap` once when populating the list.
- Pass the decoded `Bitmap` to the `PlayerStatistic` constructor.

#### [MODIFY] [StatisticsAdapter.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/statistics/StatisticsAdapter.java)
- Remove Base64 and Bitmap decoding logic from `getView()`.
- Use the pre-cached `Bitmap` from the `PlayerStatistic` object.

## Verification Plan

### Automated Tests
- I'll check if the code compiles and if there are any obvious memory leaks. Since this is a UI performance fix, manual verification is key.

### Manual Verification
- Deploy the app and navigate to the Statistics page.
- Scroll through a list of players (especially those with photos) and verify that the scrolling is smooth.
- Verify that player photos are still displayed correctly.
