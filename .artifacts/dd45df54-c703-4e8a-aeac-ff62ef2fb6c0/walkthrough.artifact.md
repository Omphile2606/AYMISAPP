# Walkthrough - Enhanced Profile Navigation

I have updated the navigation logic in the home fragments to allow users to access their profile page via the top-right profile menu. This is particularly useful as the "Profile" option was recently removed from the bottom navigation bar.

## Changes Made

### Navigation Logic
- **[HomeFragment.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/HomeFragment.kt)**:
    - Updated `showLogoutMenu` to handle the "Profile" menu item click.
    - When "Profile" is selected, the app now navigates to `ProfileFragment` using `parentFragmentManager`.
- **[TutorHomeFragment.kt](file:///C:/Users/choki/StudioProjects/MyTutor/app/src/main/java/com/example/mytutor/TutorHomeFragment.kt)**:
    - Added the "Profile" option to the popup menu for consistency.
    - Implemented identical navigation logic to ensure tutors can also edit their profiles.

## Verification Results

### Automated Tests
- Ran `./gradlew :app:compileDebugKotlin`: **Build Successful**.

### Manual Verification
1. **Home Screen**: Click the profile icon in the top right.
2. **Popup Menu**: Select "Profile" from the menu.
3. **Navigation**: Verify that the `ProfileFragment` is displayed and that you can return to the Home screen using the back button.
