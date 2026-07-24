# Smooth Scrolling for Statistics Page

I have optimized the statistics page to fix the slow scrolling issue reported.

## Changes Made

### Caching Decoded Bitmaps
The slow scrolling was caused by decoding player photos (Base64 to Bitmap) on the main thread every time a list item became visible during scrolling. I have moved this decoding process to the initial data loading phase.

- **[PlayerStatistic.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/statistics/PlayerStatistic.java)**: Added a `Bitmap` field to hold the decoded photo.
- **[Statistics.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/statistics/Statistics.java)**: Now decodes the photo string into a `Bitmap` once when the list is populated.
- **[StatisticsAdapter.java](file:///C:/Users/Jake/AndroidStudioProjects/High-To-Low/app/src/main/java/com/mydomain/countingdowngame/statistics/StatisticsAdapter.java)**: Updated to use the pre-decoded `Bitmap`, removing the expensive decoding logic from `getView()`.

## Verification Results

### Automated Tests
- The project builds successfully with `./gradlew app:assembleDebug`.

### Manual Verification
- Navigating to the Statistics page and scrolling should now be significantly smoother, as the heavy image processing is no longer happening during scroll events.
